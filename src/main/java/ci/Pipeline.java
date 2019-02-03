package ci;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class Pipeline {


    /**
     * git object used by JGit
     */
    private Git git;
    /**
     * URL of the repository to clone
     */
    private String repoUrl;
    /**
     * unique id of the commit to build
     */
    private String commitSha;

    public String getClonedRepoDirectory() {
        return clonedRepoDirectory;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    /**
     * directory of the cloned repo
     */
    private String clonedRepoDirectory;
    /**
     * directory where log files are stored,
     */
    private String logDirectory;

    /**
     * Part of the repoUrl to uniquely identify the repo on the local storage
     */
    private String repoId;


    /**
     * @param repoUrl            URL of the repository to clone
     * @param commitSha          unique id of the commit to build
     * @param reposDirectory     directory  in which the repo  will be cloned (e.g. "./repos")
     * @param logDirectoryGlobal directory in which the log directory for this repoUrl will be created ("./logs")
     */
    public Pipeline(String repoUrl, String commitSha, String reposDirectory, String logDirectoryGlobal) {
        this.repoUrl = repoUrl;
        this.commitSha = commitSha;
        this.clonedRepoDirectory = reposDirectory;

        // would give cpptz_dd22480_lab_1 for https://github.com/Cpptz/dd22480_lab_1
        this.repoId = this.repoUrl.substring(repoUrl.indexOf(".com") + 5).replace("/", "_").toLowerCase();
        this.logDirectory = logDirectoryGlobal + "/" + this.repoId + "/";
        if (!new File(this.logDirectory).exists()) new File(this.logDirectory).mkdir();

        this.clonedRepoDirectory = reposDirectory + "/" + this.repoId + "_" + this.commitSha + "/";
        if (!new File(this.clonedRepoDirectory).exists()) new File(this.clonedRepoDirectory).mkdirs();


    }


    /**
     * run sequentially {@link Pipeline#cloneRepository()}, {@link Pipeline#checkoutRepo()} ,
     * {@link Pipeline#compileRepo(String, int)} , {@link Pipeline#testRepo(String, int)}
     *
     * @return {@link PipelineResult}
     */
    public PipelineResult runPipeline() {

        PipelineResult result = new PipelineResult(this.commitSha, this.repoUrl);
        try {
            cloneRepository();
            checkoutRepo();

            // TODO/ OPTIONNAL: handle this with .properties file or .yml in the repo
            // currently only Java with maven
            // timeOut of 20 s
            int timeOut = 20;
            boolean isCompiling = compileRepo("mvn compile -B", timeOut);
            boolean isTestPassing = testRepo("mvn test -B", timeOut);

            result.status = (isCompiling && isTestPassing) ? PipelineResult.PipelineStatus.SUCCESS :
                    PipelineResult.PipelineStatus.ERROR;


        } catch (CloneException e) {
            result.status = PipelineResult.PipelineStatus.FAILURE;
            result.failureCause = PipelineResult.FailureCause.CLONE;
        } catch (TestException e) {
            result.status = PipelineResult.PipelineStatus.FAILURE;
            result.failureCause = PipelineResult.FailureCause.TEST;
        } catch (CompileException e) {
            e.printStackTrace();
            result.status = PipelineResult.PipelineStatus.FAILURE;
            result.failureCause = PipelineResult.FailureCause.COMPILATION;

        } catch (CheckoutException e) {
            result.status = PipelineResult.PipelineStatus.FAILURE;
            result.failureCause = PipelineResult.FailureCause.CHECKOUT;
        }


        if (new File(getCompileLogPath()).exists()) {
            result.compileLog = true;
            result.compileLogPath = getCompileLogPath();
        } else {
            result.compileLog = false;
        }

        if (new File(getTestLogPath()).exists()) {
            result.testLog = true;
            result.testLogPath = getCompileLogPath();
        } else {
            result.testLog = false;
        }

        // delete the cloned repo
        this.clear();
        return result;

    }


    /**
     * @return the path to the compilation log
     */
    public String getCompileLogPath() {
        return this.logDirectory + "compile_" + this.commitSha + ".log";
    }

    /**
     * @return the path to the testing log
     */
    public String getTestLogPath() {
        return this.logDirectory + "test_" + this.commitSha + ".log";
    }


    /**
     * @throws CloneException if an errors happens
     */
    public void cloneRepository() throws CloneException {

        Git git = null;
        try {
            git = Git.cloneRepository()
                    .setURI(this.repoUrl)
                    // use sha of the commit to have unique directories
                    .setDirectory(new File(this.clonedRepoDirectory))
                    .call();

            // store the git object in class variable
            this.git = git;


        } catch (GitAPIException | JGitInternalException e) {
            throw new CloneException(e);
        }


    }


    /**
     * @throws CheckoutException if an error happens
     */
    public void checkoutRepo() throws CheckoutException {
        try {
            this.git.checkout().setName(this.commitSha).call();
        } catch (GitAPIException e) {
            throw new CheckoutException(e);
        }
    }


    /**
     * @param compileCommand string of the command to compile (e.g "mvn compile")
     * @param waitForSecond  max number of seconds a process can execute
     * @return true if it compiles, false if not
     */
    public boolean compileRepo(String compileCommand, int waitForSecond) throws CompileException {
        String[] commandArray = compileCommand.split(" ");
        try {
            return subProcessLauncher(waitForSecond, commandArray, this.getCompileLogPath());
        } catch (IOException | InterruptedException e) {
            throw new CompileException(e);
        }

    }


    /**
     * @param testCommand   string of the command to test
     * @param waitForSecond max number of seconds a process can execute
     * @return true if it passes tests, false if not
     */
    public boolean testRepo(String testCommand, int waitForSecond) throws TestException {
        String[] commandArray = testCommand.split(" ");
        try {
            return subProcessLauncher(waitForSecond, commandArray, this.getTestLogPath());
        } catch (IOException | InterruptedException e) {
            throw new TestException(e);
        }

    }


    /**
     * @param waitForSecond
     * @param commandArray  command to execute
     * @param logPath       path of the file in which the output will be redirected
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean subProcessLauncher(int waitForSecond, String[] commandArray, String logPath) throws IOException,
            InterruptedException {
        ProcessBuilder testProcessBuilder;
        testProcessBuilder =
                new ProcessBuilder().command(commandArray).redirectErrorStream(true).directory(new File(this.clonedRepoDirectory));

        testProcessBuilder.redirectOutput(new File(logPath));
        Process testProcess = testProcessBuilder.start();
        testProcess.waitFor(waitForSecond, TimeUnit.SECONDS);
        int returnCode = testProcess.exitValue();
        return ((returnCode == 0) ? true : false);

    }

    /**
     * Deletes the directory of the repo  and the git object
     */
    public void clear() {
        try {
            FileUtils.deleteDirectory(new File(this.clonedRepoDirectory));
            this.git = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




