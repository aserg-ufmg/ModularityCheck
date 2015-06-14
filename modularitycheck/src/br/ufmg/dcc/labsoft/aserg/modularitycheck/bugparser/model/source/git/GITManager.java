package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.git;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.osgi.service.prefs.Preferences;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.Commit;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.VersionControlManager;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.views.ModularityCheck;

public class GITManager extends VersionControlManager {
	private Repository repository;
	private GITLogHandler log_handler;
	private static String repositoryPath;
	private static String last_repository_url;
	
	private static final String REPOSITORY_PATH = "REPOSITORY_PATH";

	public GITManager(String repository_url, String start_date, String end_date) {
		super(repository_url, start_date, end_date);
		setupGIT();
	}

	@Override
	public void downloadCode(File path) throws Exception {

	}

	@Override
	public List<Commit> getCommits() {
		System.out.println("Getting commit log...");
		Iterable<RevCommit> logs = null;
		try {
			logs = new Git(repository).log().all().call();

		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log_handler.handleLogEntry(logs, repository);

		return log_handler.getCommits();

	}

	/**
	 * Parse the URL and set up the class to connect to the GIT repo
	 * 
	 * @param url
	 *            Repository's address (valid protocols: http://, git:// and
	 *            file://)
	 */
	private void setupGIT() {
		log_handler = new GITLogHandler();
		try {
			cloneRemoteRepository();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

	}
	
	private void cloneRemoteRepository() throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		
		loadPreferences();
		//repositoryPath="C:\\Users\\Luciana\\AppData\\Local\\Temp\\TestGitRepository8482929131009567265";
		if(repositoryPath.isEmpty()) cloneRepository();
		else if(!last_repository_url.equals(repository_url) || new File(repositoryPath).listFiles().length == 0) cloneRepository();		
		
		if(!repositoryPath.endsWith("/.git")) repositoryPath = repositoryPath + Properties.SEPARATOR + "/.git";
        openRepository(new File(repositoryPath));
	}

	private void cloneRepository() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		File localPath = File.createTempFile("TestGitRepository", "");
        localPath.delete();
        
        System.out.println("Cloning from " + repository_url + " to " + localPath);
        Git result = Git.cloneRepository()
                .setURI(repository_url)
                .setDirectory(localPath)
                .call();

        
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
	        System.out.println("Having repository: " + result.getRepository().getDirectory());
        
	        repositoryPath = result.getRepository().getDirectory().getAbsolutePath();
	        
	        @SuppressWarnings("deprecation")
			Preferences preferences = new InstanceScope().getNode(ModularityCheck.PLUGIN_ID);
			preferences.put(REPOSITORY_PATH, repositoryPath);
	}
	
	

	private void loadPreferences() {
		@SuppressWarnings("deprecation")
		Preferences preferences = new InstanceScope().getNode(ModularityCheck.PLUGIN_ID);
		
		last_repository_url = preferences.get(ModularityCheck.REPO_URL, " ");
		repositoryPath = preferences.get(REPOSITORY_PATH, "");
		
	}

	private void openRepository(File result) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		
		repository = builder
				.setGitDir(result).readEnvironment() // scan
																		// environment
																		// GIT_*
																		// variables
				.findGitDir() // scan up the file system tree
				.build();
		
		
	}

	public void openRepository() throws IOException, GitAPIException,
			InvalidRemoteException, TransportException {
		// Now open the created repository
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		String directory = System.getProperty("user.home") + Properties.SEPARATOR + "TestGit\\";///.git
		if(new File(directory).mkdir() || new File(directory).exists()){
			directory = directory + "/.git";
		repository = builder
				.setGitDir(
						new File(
								directory)).readEnvironment() // scan
																		// environment
																		// GIT_*
																		// variables
				.findGitDir() // scan up the file system tree
				.build();
		}else System.out.println("Error during repository clonage. Permission denied for creating files at user home");
	}
}
