package ci;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContinuousIntegrationServerTest {

    @Test
    void sendStatus() throws Exception {

        String url =  "https://github.com/Cpptz/dd22480_lab_2";
        PipelineResult.PipelineStatus status = PipelineResult.PipelineStatus.SUCCESS;
        String commitSha = "fc505c1f0ae097d7bc5fc3bef0b47dc3751f84e7";
        String invalidCommitSha = "fc505c1f0ae097d7bc5fc3bef0b47dc3851f84e7";
        String description = "The build succeeded!";

        PipelineResult res = new PipelineResult(commitSha, url,"");
        res.status = status;


        // valid data, should work
        assertTrue(ContinuousIntegrationServer.sendStatus(res));

        res.commitSha = invalidCommitSha;
        // invalid sha, should return false
        assertFalse(ContinuousIntegrationServer.sendStatus(res));

    }
}