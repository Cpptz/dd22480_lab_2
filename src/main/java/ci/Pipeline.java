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
     * directory  in which the directory  will be cloned
     */
    private String localWorkingDirectory;


    /**
     * @param repoUrl
     * @param commitSha
     * @param localWorkingDirectory
     */
    public Pipeline(String repoUrl, String commitSha, String localWorkingDirectory) {
        this.repoUrl = repoUrl;
        this.commitSha = commitSha;
        this.localWorkingDirectory = localWorkingDirectory;

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
        } catch (GitAPIException | JGitInternalException e) {
            throw new CloneException(e.getCause());
        }
        // store the git object in class variable
        this.git = git;

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
        ProcessBuilder compileProcessBuilder = null;
        try {
            compileProcessBuilder =
                    new ProcessBuilder().command(commandArray).inheritIO().directory(this.getRepoDirectory());


            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor(waitForSecond, TimeUnit.SECONDS);
            int returnCode = compileProcess.exitValue();
            return ((returnCode == 0) ? true : false);
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }

    }


    /**
     * @param testCommand   string of the command to test
     * @param waitForSecond max number of seconds a process can execute
     * @return true if it passes tests, false if not
     */
    public boolean testRepo(String testCommand, int waitForSecond) throws InterruptedException, IOException {
        String[] commandArray = testCommand.split(" ");
        ProcessBuilder testProcessBuilder = null;
        try {
            testProcessBuilder =
                    new ProcessBuilder().command(commandArray).inheritIO().directory(this.getRepoDirectory());


            Process testProcess = testProcessBuilder.start();
            testProcess.waitFor(waitForSecond, TimeUnit.SECONDS);
            int returnCode = testProcess.exitValue();
            return ((returnCode == 0) ? true : false);
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }

    }

    public void clear() {
        try {
            FileUtils.deleteDirectory(this.git.getRepository().getDirectory().getParentFile());
            this.git = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




