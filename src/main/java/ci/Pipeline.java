package ci;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class Pipeline {


    private Git git;

    /**
     * @param repoUrl
     * @param localDirectoryPath
     * @return
     * @throws CloneException if an error happens
     */
    public void cloneRepository(String repoUrl, String localDirectoryPath) throws CloneException {
        try {
            Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(new File(localDirectoryPath))
                    .call();
            // store the git object in class variable
            this.git = git;
        } catch (GitAPIException e) {
            e.printStackTrace();
            throw new CloneException();
        }
    }


    /**
     * @param commitSha commitSha unique id of the commit
     * @throws CheckoutException
     */
    public void checkoutRepo(String commitSha) throws CheckoutException {
        try {
            this.git.checkout().setName(commitSha).call();
        } catch (GitAPIException e) {
            throw new CheckoutException();
        }
    }


    /**
     * @param compileCommand string of the command to compile (e.g "mvn compile")
     * @param waitForSecond  max number of seconds a process can execute
     * @return
     */
    public boolean compileRepo(String compileCommand, int waitForSecond) throws InterruptedException, IOException {
        String[] commandArray = compileCommand.split(" ");
        ProcessBuilder compileProcessBuilder = null;
        try {
            compileProcessBuilder =
                    new ProcessBuilder().command(commandArray).inheritIO().directory(this.git.getRepository().getDirectory());


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
     * @param testCommand   string of the command to compile
     * @param waitForSecond max number of seconds a process can execute
     * @return
     */
    public boolean testRepo(String testCommand, int waitForSecond) throws InterruptedException, IOException {
        String[] commandArray = testCommand.split(" ");
        ProcessBuilder testProcessBuilder = null;
        try {
            testProcessBuilder =
                    new ProcessBuilder().command(commandArray).inheritIO().directory(this.git.getRepository().getDirectory());


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

    public void clear(){
        try {
            FileUtils.deleteDirectory(this.git.getRepository().getDirectory());
            this.git = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




