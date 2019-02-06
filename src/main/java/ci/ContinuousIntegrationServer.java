package ci;

import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook
 * See the Jetty documentation for API documentation of those classes.
 */

public class ContinuousIntegrationServer extends AbstractHandler {


    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code
        String jsondata = (getRequestBody(request));
        Parser p = new Parser();
        Commit c = p.parseCommit(jsondata);
        response.getWriter().println("CI job done");
    }

    private String getRequestBody(final HttpServletRequest request) {
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            if (reader == null) {
                return null;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (final Exception e) {
            System.out.println("Could not obtain the request body from the http request");
            return null;
        }
    }

    public static boolean sendStatus(PipelineResult result) throws Exception {
        // convert to lower case to avoid 422 unprocessable entity error
        String status = result.status.toString().toLowerCase();
        String description = "Could not find description.";
        switch (status) {
            case "success":
                description = "The build succeeded!";
                break;
            case "error":
                description = "There was an error!";
                break;
            case "failure":
                description = "The build failed!";
        }


        String ownerAndRepo = result.remoteUrl.substring(result.remoteUrl.indexOf(".com") + 5);

        String username = "cpptz";
        String token = "YmRiMmU0NjQxZGRjNmE0ZTI2OTU0OTAzZjEwYTQ5MzE1YzZmOTdiZg==";

        byte[] asBytes = Base64.getDecoder().decode(token);
        try {
            token = new String(asBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return false;
        }


        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        String encoding = Base64.getEncoder().encodeToString((username + ":" + token).getBytes());


        try {

            // end point of github api to post the status
            String postUrl =
                    "https://api.github.com/repos/" + ownerAndRepo + "/statuses" + "/" + result.commitSha;
            HttpPost request = new HttpPost(postUrl);
            request.setHeader("Authorization", "Basic " + encoding);

            // create the payload
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("state", status);
            jsonObject.addProperty("description", description);
            jsonObject.addProperty("context", "ci/dd2480");
            jsonObject.addProperty("target-url", "");
            StringEntity params = new StringEntity(jsonObject.toString());
            request.setEntity(params);
            request.addHeader("Content-type", "application/json");
            request.addHeader("Accept", "application/json");
            HttpResponse response = httpClient.execute(request);
//            ((CloseableHttpClient) httpClient).close();
            return response.getStatusLine().getStatusCode() == 201;

        } catch (Exception ex) {

            ex.printStackTrace();
            //handle exception here
            return false;
        }
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("server");
        int port = Integer.parseInt(rb.getString("port"));
        Server server = new Server(port);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
