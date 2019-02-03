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
        FAILURE,
        /**
         * Either the compilation or the test failed
         */
        ERROR,
        /**
         * Commpiles and the tests passed
         */
        SUCCESS

    }

    /**
     * Cause of the failure
     */
    public enum FailureCause{
        CLONE,
        CHECKOUT,
        COMPILATION,
        TEST

    }

    public PipelineStatus status;
    public String remoteUrl;
    public String commitSha;

    public FailureCause failureCause;
    public boolean compileLog;
    public boolean testLog;
    public String compileLogPath;
    public String testLogPath;


    public PipelineResult(String commitSha, String remoteUrl) {
        this.commitSha = commitSha;
        this.remoteUrl = remoteUrl;
    }
}
