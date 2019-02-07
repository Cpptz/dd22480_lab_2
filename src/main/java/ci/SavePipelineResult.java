package ci;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SavePipelineResult is used for saving history.
 */
public class SavePipelineResult {

    public File pipelineResult;


    /**
     * Create a new file if it's not already created.
     */
    public SavePipelineResult(String filename) {
        pipelineResult = new File(filename);
        if (!pipelineResult.exists()) {
            try {
                pipelineResult.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method will save the result of the pipeline to a file. One pipelineresult per line.
     * @param result
     */
    public void saveResult(PipelineResult result){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(pipelineResult, true));
            //Make every attribute a string and append to the file
            String resultAttributes = result.status.toString() + "," + result.remoteUrl + "," + result.commitSha +
                    "," + result.failureCause.toString() + "," + result.compileLog + "," + result.testLog +
                    "," + result.compileLogPath + "," + result.testLogPath + "\n";
            writer.append(resultAttributes);
            writer.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * retrieveAll will read through the file with the pipeline result history and restore those
     * as PipelineResult objects.
     * @return A list with PipelineResult objects.
     */
    public List<PipelineResult> retrieveAll(String filename){
        List<PipelineResult> resultList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String pipeLineObject = reader.readLine();
            String[] pipelineResultAtt;
            while (pipeLineObject != null) {
                // Create a PipelineResult object and fill in the information from the history file.
                PipelineResult pipelineResult = new PipelineResult("", "");
                pipelineResultAtt = pipeLineObject.split(",");
                pipelineResult.status = PipelineResult.PipelineStatus.valueOf(pipelineResultAtt[0]);
                pipelineResult.remoteUrl = pipelineResultAtt[1];
                pipelineResult.commitSha = pipelineResultAtt[2];
                pipelineResult.failureCause = PipelineResult.FailureCause.valueOf(pipelineResultAtt[3]);
                pipelineResult.compileLog = Boolean.valueOf(pipelineResultAtt[4]);
                pipelineResult.testLog = Boolean.valueOf(pipelineResultAtt[5]);
                pipelineResult.compileLogPath = pipelineResultAtt[6];
                pipelineResult.testLogPath = pipelineResultAtt[7];
                // Add the created object to the list.
                resultList.add(pipelineResult);

                pipeLineObject = reader.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}