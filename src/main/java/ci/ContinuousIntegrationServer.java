package ci;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

import java.util.Base64;

import com.google.gson.JsonObject;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    private String reposDirectory;
    private String logsDirectory;

    public ContinuousIntegrationServer(String reposDirectory, String logsDirectory){
        this.reposDirectory = reposDirectory;
        this.logsDirectory = logsDirectory;
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);


        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
    }


    public static boolean sendStatus(String pipelineStatus, String description, String repoUrl, String commitSha){




        String ownerAndRepo= repoUrl.substring(repoUrl.indexOf(".com") + 5);

        String username = "cpptz";
        String token = "50bea8897a6167ab9f05ad2a2cbc4431ed53b060";


        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        String encoding = Base64.getEncoder().encodeToString((username+":"+token).getBytes());




        try {

            // end point of github api to post the status
            String postUrl =
                    "https://api.github.com/repos/"+ownerAndRepo+ "/statuses" + "/"+commitSha;
            HttpPost request = new HttpPost(postUrl);
            request.setHeader("Authorization","Basic "+encoding);

            // create the payload
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("state", pipelineStatus);
            jsonObject.addProperty("description", pipelineStatus);
            jsonObject.addProperty("context", "ci/dd2480");
            jsonObject.addProperty("target-url", "");
            StringEntity params =new StringEntity(jsonObject.toString());
            request.setEntity(params);
            request.addHeader("Content-type", "application/json");
            request.addHeader("Accept", "application/json");
            HttpResponse response = httpClient.execute(request);
//            ((CloseableHttpClient) httpClient).close();


            return response.getStatusLine().getStatusCode()==201;

        }catch (Exception ex) {

            ex.printStackTrace();
            //handle exception here
            return false;

        }
    }


    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {

//        Properties props = new Properties();
//        props.load(new FileInputStream(args[0]));
//        String reposDirectory = props.getProperty("reposDirectory");
//        String logsDirectory= props.getProperty("logsDirectory");
//        int port = Integer.parseInt(props.getProperty("port"));
//
//        Server server = new Server(port);
//        server.setHandler(new ContinuousIntegrationServer(reposDirectory,logsDirectory));
//        server.start();
//        server.join();




    }
}
