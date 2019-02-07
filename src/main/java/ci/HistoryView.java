package ci;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Static class to make the view of the history
 */
public class HistoryView {

    /**
     *
     * @param out the PrinterWriter of the response
     * @param pipelineResultList the result to display
     */
    public static void writeHistory(PrintWriter out, List<PipelineResult> pipelineResultList){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String ipAdress = ResourceBundle.getBundle("server").getString("ip");
        String port = ResourceBundle.getBundle("server").getString("port");
        try {
            String html = new String(Files.readAllBytes(Paths.get(classLoader.getResource("history.html").getPath())),
                    "UTF-8");

            String toInsert = "";

            for(PipelineResult pipelineResult: pipelineResultList){
                String row = "<tr>";
                row+="<td><a href=\""+pipelineResult.remoteUrl+"/commit/"+pipelineResult.commitSha+"\">"+pipelineResult.commitSha.substring(0,7)+"</a" +
                        "></td>";
                row+="<td>"+pipelineResult.time+"</td>";
                row+="<td value=\""+pipelineResult.status.toString().toLowerCase()+"\">"+pipelineResult.status.toString().toLowerCase()+
                        "</td>";
                if(pipelineResult.compileLog) {
                    row += "<td><a href=\"http://" + ipAdress + ":" + port + "/file/" + makePathForServer(pipelineResult.compileLogPath)+
                    "\">compile.log</a></td>";
                }else{
                    row+="<td>None</td>";
                }
                if(pipelineResult.testLog){
                    row += "<td><a href=\"http://" + ipAdress + ":" + port + "/file/" + makePathForServer(pipelineResult.testLogPath)+
                            "\">test.log</a></td>";
                }else{
                    row+="<td>None</td>";
                }

                row+="<td>"+pipelineResult.errorCause+"</td>";
                row+="</tr>";
                toInsert+=row;

            }

            html = html.replace("insert_code",toInsert);

            out.write(html);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param path
     * @return path formatted for the server
     */
    public static String makePathForServer(String path){
        ResourceBundle rb = ResourceBundle.getBundle("server");
        String globalLogDir = rb.getString("logsDirectory");
        return path.substring(path.indexOf(globalLogDir)+globalLogDir.length()+1).
                replace("/","_")
                .replace(".log","");


    }


}
