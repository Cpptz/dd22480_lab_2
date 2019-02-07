package ci;



/**
 * The attribute of this should not be modified after its creation
 * by the {@link Pipeline}
 */
public class PipelineResult {

    /**
     * Status return by the pipeline
     */
    public enum PipelineStatus{
        /**
         * The pipeline was not fully executed
         */
        ERROR,
        /**
         * Either the compilation or the test failed
         */
        FAILURE,
        /**
         * Commpiles and the tests passed
         */
        SUCCESS

    }

    /**
     * Cause of the failure
     */
    public enum ErrorCause {
        CLONE,
        CHECKOUT,
        COMPILATION,
        TEST,
        NONE

    }
    public PipelineStatus status;
    public String remoteUrl;
    public String commitSha;

    public ErrorCause errorCause = ErrorCause.NONE;
    public boolean compileLog;
    public boolean testLog;
    public String compileLogPath = " ";
    public String testLogPath = " ";


    public PipelineResult(String commitSha, String remoteUrl) {
        this.commitSha = commitSha;
        this.remoteUrl = remoteUrl;
    }
}
