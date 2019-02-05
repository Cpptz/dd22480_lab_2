package ci;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContinuousIntegrationServerTest {

    @Test
    void sendStatus() {
        String url =  "https://github.com/Cpptz/dd22480_lab_2";
        String status = "success";
        String commitSha = "fc505c1f0ae097d7bc5fc3bef0b47dc3751f84e7";
        String invalidCommitSha = "fc505c1f0ae097d7bc5fc3bef0b47dc3851f84e7";
        String description = "The build succeeded!";

        // valid data, should work
        assertTrue(ContinuousIntegrationServer.sendStatus(status,description,url,commitSha));

        // invalid sha, should return false
        assertFalse(ContinuousIntegrationServer.sendStatus(status,description,url,invalidCommitSha));

    }
}