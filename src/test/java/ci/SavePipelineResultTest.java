package ci;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class SavePipelineResultTest {

    static String historyFile = null;

    @BeforeAll
    static void initAll() {
        ResourceBundle rb = ResourceBundle.getBundle("server");
        historyFile = rb.getString("testHistoryFile");
    }

    @AfterAll
    static void tearDownAll(){
        try {
            File testFile = new File(ResourceBundle.getBundle("server").getString("testHistoryFile"));
            FileUtils.forceDelete(testFile);

        } catch (IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void saveResult() {
        //create a result object
        SavePipelineResult savePipeline = new SavePipelineResult(historyFile);
        PipelineResult result = new PipelineResult("testsha", "testurl");

        result.errorCause = PipelineResult.ErrorCause.CLONE;
        result.status = PipelineResult.PipelineStatus.ERROR;
        // save the result to the file
        savePipeline.saveResult(result);
        // check if the result is the expected outcome from saving the object to the file.
        try {
            BufferedReader reader = new BufferedReader(new FileReader(historyFile));
            String pipeLineObject = reader.readLine();
            // Should pass
            assertEquals("ERROR,testurl,testsha,CLONE,false,false, , ", pipeLineObject);
        }
        catch (IOException e) {
            Assertions.fail();
        }

    }

    @Test
    void retrieveAll() {
        // create a result object
        SavePipelineResult savePipeline = new SavePipelineResult(historyFile);
        PipelineResult result = new PipelineResult("testsha", "testurl");

        result.errorCause = PipelineResult.ErrorCause.CLONE;
        result.status = PipelineResult.PipelineStatus.ERROR;
        // save the result to the file
        savePipeline.saveResult(result);

        // restore history from file to object
        List<PipelineResult> results = new ArrayList<>();
        results = savePipeline.retrieveAll();
        result = results.get(0);

        //check if the object is created properly.
        //Should pass
        assertEquals(PipelineResult.PipelineStatus.ERROR, result.status );
        assertEquals("testurl", result.remoteUrl);
        assertEquals("testsha", result.commitSha);
        assertEquals(PipelineResult.ErrorCause.CLONE, result.errorCause);
        assertEquals(" ", result.compileLogPath);
        assertEquals(" ", result.testLogPath);
        assertFalse(result.compileLog);
        assertFalse(result.testLog);

    }
}