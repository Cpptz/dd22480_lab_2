package ci;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
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
    /**
     * directory of the cloned repo. Will be deleted at the end
     * of the run of the pipeline
     */
    private String clonedRepoDirectory;
    /**
     * directory where log files are stored. Will be created only once
     */
    private String logDirectory;

    /**
     * Part of the repoUrl to uniquely identify the repo on the local storage
     */
    private String repoId;


    /**
     * @param repoUrl   URL of the repository to clone
     * @param commitSha unique id of the commit to build
     */
    public Pipeline(String repoUrl, String commitSha) {
        this.repoUrl = repoUrl;
        this.commitSha = commitSha;


        ResourceBundle rb = ResourceBundle.getBundle("server");

        // would give cpptzdd22480lab1 for https://github.com/Cpptz/dd22480_lab_1
        this.repoId =
                this.repoUrl.substring(repoUrl.indexOf(".com") + 5)
                        .replace("/", "")
                        .replace("_", "")
                        .toLowerCase();
        this.logDirectory = rb.getString("logsDirectory") + "/" + this.repoId + "/";
        if (!new File(this.logDirectory).exists()) new File(this.logDirectory).mkdirs();

        this.clonedRepoDirectory = rb.getString("reposDirectory") + "/" + this.repoId + "_" + this.commitSha + "/";
        new File(this.clonedRepoDirectory).mkdirs();


    }


    /**
     * run sequentially {@link Pipeline#cloneRepository()}, {@link Pipeline#checkoutRepo()} ,
     * {@link Pipeline#compileRepo(String, int)} , {@link Pipeline#testRepo(String, int)}
     *
     * @return {@link PipelineResult}
     */
    public PipelineResult runPipeline() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        PipelineResult result = new PipelineResult(this.commitSha, this.repoUrl, formatter.format(LocalDateTime.now()));
        try {
            cloneRepository();
            checkoutRepo();

            int timeOut = 60;
            boolean isCompiling = compileRepo("mvn compile -B", timeOut);
            boolean isTestPassing = testRepo("mvn test -B", timeOut);


            if(isCompiling && isTestPassing){
                result.status = PipelineResult.PipelineStatus.SUCCESS;
            }
            else{
                result.status = PipelineResult.PipelineStatus.FAILURE;
                if(!isCompiling){
                    result.errorCause = PipelineResult.ErrorCause.COMPILATION;
                }else{
                    result.errorCause = PipelineResult.ErrorCause.TEST;
                }
            }


        } catch (CloneException e) {
            result.status = PipelineResult.PipelineStatus.ERROR;
            result.errorCause = PipelineResult.ErrorCause.CLONE;
        } catch (TestException e) {
            result.status = PipelineResult.PipelineStatus.ERROR;
            result.errorCause = PipelineResult.ErrorCause.TEST;
        } catch (CompileException e) {
            e.printStackTrace();
            result.status = PipelineResult.PipelineStatus.ERROR;
            result.errorCause = PipelineResult.ErrorCause.COMPILATION;

        } catch (CheckoutException e) {
            result.status = PipelineResult.PipelineStatus.ERROR;
            result.errorCause = PipelineResult.ErrorCause.CHECKOUT;
        }


        if (new File(getCompileLogPath()).exists()) {
            result.compileLog = true;
            result.compileLogPath = getCompileLogPath();
        } else {
            result.compileLog = false;
        }

        if (new File(getTestLogPath()).exists()) {
            result.testLog = true;
            result.testLogPath = getTestLogPath();
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
     * @return
     */
    public String getClonedRepoDirectory() {
        return clonedRepoDirectory;
    }

    /**
     * @return
     */
    public String getLogDirectory() {
        return logDirectory;
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




