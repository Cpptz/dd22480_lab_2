package ci;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

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

//        request.getParameter("")

        System.out.println(target);
        System.out.println(baseRequest.toString());

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
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
