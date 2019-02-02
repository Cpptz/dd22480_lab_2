package ci;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
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
    /**
     * directory  in which the repo  will be cloned
     */
    private String localWorkingDirectory;
    /**
     * directory where log files are stored
     */
    private String logDirectory;


    /**
     *
     * @param repoUrl URL of the repository to clone
     * @param commitSha unique id of the commit to build
     * @param localWorkingDirectory directory  in which the repo  will be cloned
     * @param logDirectory directory where log files will be stored
     */
    public Pipeline(String repoUrl, String commitSha, String localWorkingDirectory, String logDirectory) {
        this.repoUrl = repoUrl;
        this.commitSha = commitSha;
        this.localWorkingDirectory = localWorkingDirectory;
        this.logDirectory = logDirectory;

    }





    /**
     * @return the root directory of the repo
     */
    public File getRepoDirectory() {
        return this.git.getRepository().getDirectory().getParentFile();
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
                    .setDirectory(new File(this.localWorkingDirectory + "/" + this.commitSha))
                    .call();

            // store the git object in class variable
            this.git = git;


        } catch (GitAPIException | JGitInternalException e) {
            throw new CloneException(e.getCause());
        }


    }


    /**
     * @throws CheckoutException if an error happens
     */
    public void checkoutRepo() throws CheckoutException {
        try {
            this.git.checkout().setName(this.commitSha).call();
        } catch (GitAPIException e) {
            throw new CheckoutException(e.getCause());
        }
    }


    /**
     * @param compileCommand string of the command to compile (e.g "mvn compile")
     * @param waitForSecond  max number of seconds a process can execute
     * @return true if it compiles, false if not
     */
    public boolean compileRepo(String compileCommand, int waitForSecond) throws InterruptedException, IOException {
        String[] commandArray = compileCommand.split(" ");
        String compileOutputPath = this.logDirectory+"/compile_"+this.commitSha+".log";
        return subProcessLauncher(waitForSecond, commandArray, compileOutputPath);

    }


    /**
     * @param testCommand   string of the command to test
     * @param waitForSecond max number of seconds a process can execute
     * @return true if it passes tests, false if not
     */
    public boolean testRepo(String testCommand, int waitForSecond) throws InterruptedException,
            IOException {
        String[] commandArray = testCommand.split(" ");
        String testOutputPath = this.logDirectory+"/test_"+this.commitSha+".log";
        return subProcessLauncher(waitForSecond, commandArray,testOutputPath);

    }


    /**
     *
     * @param waitForSecond
     * @param commandArray command to execute
     * @param logPath path of the file in which the output will be redirected
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean subProcessLauncher(int waitForSecond, String[] commandArray, String logPath) throws IOException,
            InterruptedException {
        ProcessBuilder testProcessBuilder;
        try {
            testProcessBuilder =
                    new ProcessBuilder().command(commandArray).redirectErrorStream(true).directory(this.getRepoDirectory());

            testProcessBuilder.redirectOutput(new File(logPath));
            Process testProcess = testProcessBuilder.start();
            testProcess.waitFor(waitForSecond, TimeUnit.SECONDS);
            int returnCode = testProcess.exitValue();
            return ((returnCode == 0) ? true : false);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Deletes the clone repo and the git object
     */
    public void clear() {
        try {
            FileUtils.deleteDirectory(this.git.getRepository().getDirectory().getParentFile());
            this.git = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




