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
        /**
         * If clone has failed
         */
        CLONE,
        /**
         * If checkout has failed
         */
        CHECKOUT,
        /**
         * If compilation has failed
         */
        COMPILATION,
        /**
         * If test has failed
         */
        TEST,
        /**
         * If nothing has failed
         */
        NONE

    }

    /**
     * status of the pipeline
     */
    public PipelineStatus status;
    /**
     * url of the remote repository
     */
    public String remoteUrl;
    /**
     * sha of the commit
     */
    public String commitSha;

    /**
     * cause of failing or error
     */
    public ErrorCause errorCause = ErrorCause.NONE;

    /**
     * indicates if there is a log file for compilation
     */
    public boolean compileLog;

    /**
     * indicates if there is a log file for testing
     */
    public boolean testLog;
    public String compileLogPath = " ";
    public String testLogPath = " ";
    /**
     * time the build was triggered
     */
    public String time;


    /**
     *
     * @param commitSha sha of the commit
     * @param remoteUrl url of the remote repository
     * @param time time the build was triggered
     */
    public PipelineResult(String commitSha, String remoteUrl, String time) {
        this.commitSha = commitSha;
        this.remoteUrl = remoteUrl;
        this.time =time;
    }

    public PipelineResult(){}
}
