package ci;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {


    String goodUrl = "https://github.com/Cpptz/dd22480_lab_1";
    String badUrl = "https://github.com/Cpptz/dd224";
    static String reposDirectory = null;
    static String logsDirectory = null;


    @BeforeAll
    static void initAll() {

        ResourceBundle rb = ResourceBundle.getBundle("server");
        reposDirectory = rb.getString("reposDirectory");
        logsDirectory = rb.getString("logsDirectory");


        new File(reposDirectory).mkdir();
        new File(logsDirectory).mkdir();
    }

    @AfterAll
    static void tearDownAll(){
        try {
            FileUtils.deleteDirectory(new File(reposDirectory));
            FileUtils.deleteDirectory(new File(logsDirectory));
        } catch (IOException e) {
//            e.printStackTrace();
            Assertions.fail();
        }

    }

    @Test
    void cloneRepository() {


        // use a wrong url
        Pipeline pipeline = new Pipeline(badUrl, "9ed9801", reposDirectory, logsDirectory);

        // this should throw an exception
        assertThrows(CloneException.class, () -> {
            pipeline.cloneRepository();
        });




        // test that it doesn't throw any exceptions
        Pipeline pipeline1 = new Pipeline(goodUrl, "9ed9801", reposDirectory, logsDirectory);

        try {
            pipeline1.cloneRepository();

            // we should never pass here
        } catch (CloneException e) {
//            e.printStackTrace();
            Assertions.fail();
        }
        // delete repo
        pipeline1.clear();

    }

    @Test
    void checkoutRepo() {

        // valid url, wrong commitsha
        Pipeline pipeline1 = new Pipeline(goodUrl, "8geg7ze", reposDirectory, logsDirectory);
        try {
            pipeline1.cloneRepository();
            // this should throw an exception
            assertThrows(CheckoutException.class, () -> {
                pipeline1.checkoutRepo();
            });

            // we should never pass here
        } catch (CloneException e) {
//            e.printStackTrace();
            Assertions.fail();
        }
        // delete repo
        pipeline1.clear();


        // valid url, valid commitSha
        Pipeline pipeline2 = new Pipeline(goodUrl, "9ed9801", reposDirectory, logsDirectory);
        Exception ex = null;
        try {
            pipeline2.cloneRepository();
            // this should throw an exception
            pipeline2.checkoutRepo();


            // we should never pass here
        } catch (CloneException | CheckoutException e) {
            Assertions.fail();
        }

        pipeline2.clear();


    }

    @Test
    void compileRepo() {
        Pipeline pipeline = new Pipeline(goodUrl, "9ed9801", reposDirectory, logsDirectory);

        // valid url, valid sha + valid code => should compile : return true (no exceptions should be thrown)
        try {
            pipeline.cloneRepository();
            pipeline.checkoutRepo();
            assertTrue(pipeline.compileRepo("mvn compile -q", 10));

            // we should never pass here
        } catch (CloneException | CheckoutException | InterruptedException | IOException e) {
//            e.printStackTrace();
            Assertions.fail();
        }

        // valid url, valid sha + invalid code => should not compile : return false (no exceptions should be thrown)
        File pom = new File(pipeline.getRepoDirectory() + "/pom.xml");
        pom.delete();

        try {
            assertFalse(pipeline.compileRepo("mvn compile -q", 10));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            Assertions.fail();
        }

        // delete repo
        pipeline.clear();

    }

    @Test
    void testRepo() {
        Pipeline pipeline = new Pipeline(goodUrl, "9ed9801", reposDirectory, logsDirectory);

        // valid url, valid sha + valid code => should pass tests : return true (no exceptions should be thrown)
        try {
            pipeline.cloneRepository();
            pipeline.checkoutRepo();
            assertTrue(pipeline.testRepo("mvn test -q", 10));

            // we should never pass here
        } catch (CloneException | CheckoutException | InterruptedException | IOException e) {
            Assertions.fail();
        }


        // valid url, valid sha + invalid code => should not pass tests : return false (no exceptions should be thrown)
        File pom = new File(pipeline.getRepoDirectory() + "/pom.xml");
        pom.delete();
        try {
            assertFalse(pipeline.testRepo("mvn test -q", 10));
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }

        // delete repo
        pipeline.clear();

    }
}