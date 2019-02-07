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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


        // if target is file
        if (target.substring(0, 6).equals("/file/")) {
            // making it a bit safter for the server
            if (!target.substring(6).replaceAll("[a-zA-Z0-9_]", "").equals("")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                // hard coded for now
                ResourceBundle rb = ResourceBundle.getBundle("server");
                String logDirectory = rb.getString("logsDirectory");
                String filePath = target.substring(6).replaceFirst("_", "/");
                File fileName = new File(logDirectory+"/" + filePath + ".log");
                boolean a = fileName.exists();
                Scanner scanner = new Scanner(fileName);
                String content = scanner.useDelimiter("\\A").next();
                scanner.close();
                response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                response.setContentType("text/plain");
                response.getWriter().println(content);
                baseRequest.setHandled(true);
            }

            System.out.println("/file");
        }
        // if the target is the payload from github
        else if (target.substring(0, 8).equals("/webhook")) {

            // this runs in a different thread
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                // cal pipeline and sendstatus ...
                String jsondata = (getRequestBody(request));
                Parser p = new Parser();
                Commit c = p.parseCommit(jsondata);
                Pipeline pipeline = new Pipeline(c.url, c.sha);
                PipelineResult pipelineResult = pipeline.runPipeline();
                sendStatus(pipelineResult);
                SavePipelineResult savePipelineResult =
                        new SavePipelineResult(ResourceBundle.getBundle("server").getString("historyFile"));
                savePipelineResult.saveResult(pipelineResult);

            });

            System.out.println("/webhook");
            response.getWriter().println("CI job done");
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

        } else if (target.substring(0, 8).equals("/history")) {
            System.out.println("/history");
            SavePipelineResult savePipelineResult =
                    new SavePipelineResult(ResourceBundle.getBundle("server").getString("historyFile"));
            List<PipelineResult> pipelineResults = savePipelineResult.retrieveAll();
            HistoryView.writeHistory(response.getWriter(), pipelineResults);
            response.setContentType("text/html;charset=utf-8");
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);


        }


    }

    /**
     * @param request
     * @return the body of the request
     */
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

    /**
     * @param result The result from running the pipeline
     * @return The status has been sent if no exception
     * @throws Exception Response status code is not 201
     */
    public static boolean sendStatus(PipelineResult result) {
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

        ResourceBundle rb = ResourceBundle.getBundle("server");
        String username = rb.getString("username");
        String token = rb.getString("token");

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
