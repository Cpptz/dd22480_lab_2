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

    }

    @AfterAll
    static void tearDownAll(){
        try {
            File rootTestDir = new File(ResourceBundle.getBundle("server").getString("rootTestDirectory"));
            FileUtils.deleteDirectory(rootTestDir);
        } catch (IOException e) {
//            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void cloneRepository() {


        // use a wrong url
        Pipeline pipeline = new Pipeline(badUrl, "9ed9801");

        // this should throw an exception
        assertThrows(CloneException.class, () -> {
            pipeline.cloneRepository();
        });




        // TEST that it doesn't throw any exceptions
        Pipeline pipeline1 = new Pipeline(goodUrl, "9ed9801");

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
        Pipeline pipeline1 = new Pipeline(goodUrl, "8geg7ze");
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
        Pipeline pipeline2 = new Pipeline(goodUrl, "9ed9801");
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
        Pipeline pipeline = new Pipeline(goodUrl, "9ed9801");

        // valid url, valid sha + valid code => should compile : return true (no exceptions should be thrown)
        try {
            pipeline.cloneRepository();
            pipeline.checkoutRepo();
            assertTrue(pipeline.compileRepo("mvn compile -B", 10));

            // we should never pass here
        } catch (CloneException | CheckoutException | CompileException e) {
//            e.printStackTrace();
            Assertions.fail();
        }

        // valid url, valid sha + invalid code => should not compile : return false (no exceptions should be thrown)
        File pom = new File(pipeline.getClonedRepoDirectory() + "pom.xml");
        pom.delete();

        try {
            assertFalse(pipeline.compileRepo("mvn compile -B", 10));
        } catch (CompileException e) {
            e.printStackTrace();
            Assertions.fail();
        }

        // delete repo
        pipeline.clear();

    }

    @Test
    void testRepo() {
        Pipeline pipeline = new Pipeline(goodUrl, "9ed9801");

        // valid url, valid sha + valid code => should pass tests : return true (no exceptions should be thrown)
        try {
            pipeline.cloneRepository();
            pipeline.checkoutRepo();
            assertTrue(pipeline.testRepo("mvn test -B", 10));

            // we should never pass here
        } catch (CloneException | CheckoutException | TestException e) {
            Assertions.fail();
        }


        // valid url, valid sha + invalid code => should not pass tests : return false (no exceptions should be thrown)
        File pom = new File(pipeline.getClonedRepoDirectory() + "pom.xml");
        pom.delete();
        try {
            assertFalse(pipeline.testRepo("mvn test -B", 10));
        } catch (TestException e) {
            Assertions.fail();
        }

        // delete repo
        pipeline.clear();

    }


    @Test
    void runPipeline(){
        // valid url, valid commitSha, valid code
        Pipeline pipeline = new Pipeline(goodUrl, "9ed9801");

        PipelineResult result = pipeline.runPipeline();


        assertEquals(PipelineResult.PipelineStatus.SUCCESS,result.status);
        assertTrue(result.compileLog);
        assertTrue(result.testLog);

        pipeline.clear();

        // valid url, not valid commitSha, valid code
        Pipeline pipeline_1 = new Pipeline(goodUrl, "881114");

        PipelineResult result_1 = pipeline_1.runPipeline();

        assertEquals(PipelineResult.PipelineStatus.ERROR,result_1.status);
        assertEquals(PipelineResult.ErrorCause.CHECKOUT,result_1.errorCause);
        assertFalse(result_1.compileLog);
        assertFalse(result_1.testLog);

        pipeline_1.clear();
    }
}