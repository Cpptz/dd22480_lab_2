package ci;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {



    static private String goodUrl = "https://github.com/Cpptz/dd22480_lab_2";
    static private String badUrl = "https://github.com/Cpptz/dd224";
    String directoryPath = ".";




    @Test
    void cloneRepository() {


//        // use a wrong url
//        assertThrows(CloneException.class, () -> {
//            pipeline.cloneRepository(badUrl,);
//        });
//
//
//        pipeline.cloneRepository(goodUrl);



    }

    @Test
    void checkoutRepo() {
    }

    @Test
    void compileRepo() {
    }

    @Test
    void testRepo() {
    }
}