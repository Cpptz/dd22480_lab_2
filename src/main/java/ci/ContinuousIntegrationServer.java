import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.*;
/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
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
        System.out.println(request);
        JsonParser parser = new JsonParser();
        JsonObject rootObj = parser.parse(request.getReader()).getAsJsonObject();
        JsonObject locObj = rootObj.getAsJsonObject("check_run")
                .getAsJsonObject("output");

        String status = rootObj.get("title").getAsString();

        System.out.printf("Title: %s\n", status);
        //System.out.println(new Gson().fromJson(getParamsFromPost(request),JsonObject.class).getAsJsonObject().get("check_run").getAsJsonObject().get("output").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString());
        //push

        response.getWriter().println("CI job done");
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}