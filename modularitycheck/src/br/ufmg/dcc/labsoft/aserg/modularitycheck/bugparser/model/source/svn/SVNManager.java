package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.svn;

import java.io.File;
import java.util.List;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.Commit;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.VersionControlManager;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.FileUtils;

public class SVNManager extends VersionControlManager {

	private SVNURL SVN_url;
	private SVNRevisionRange revision_range;
	private SVNClientManager svn_client;
	private SVNLogHandler log_handler;

	public SVNManager(String repository_url, String start_date, String end_date) {
		super(repository_url, start_date, end_date);
		setupSVN();
	}

	/**
	 * Faz o checkout do repositório. O atributo src_path deve ser setado.
	 */
	@Override
	public void downloadCode(File path) {
		System.out.println("Downloading code...");
		this.src_path = path;
		FileUtils.deleteRecursive(path);
		SVNUpdateClient update_client = svn_client.getUpdateClient();
		update_client.setIgnoreExternals(false);
		try {
			update_client.doCheckout(SVN_url, path, null,
					revision_range.getEndRevision(), SVNDepth.INFINITY, false);
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Commit> getCommits() {
		System.out.println("Getting commit log...");
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager("guest", "");
		svn_client.setAuthenticationManager(authManager);
		SVNLogClient log_client = svn_client.getLogClient();
		try {
			log_client.doLog(SVN_url, new String[] { "" },
					SVNRevision.UNDEFINED, revision_range.getStartRevision(),
					revision_range.getEndRevision(), false, true, 0,
					log_handler);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return log_handler.getCommits();
	}

	@Override
	public void removeTestClasses() {
		super.removeTestClasses();
	}

	/**
	 * Parse the URL and set up the class to connect to the SVN repo
	 * 
	 * @param url
	 *            Repository's address (valid protocols: http://, svn:// and
	 *            file://)
	 */
	@SuppressWarnings("deprecation")
	private void setupSVN() {

		svn_client = SVNClientManager.newInstance();
		svn_client.setEventHandler(new SVNEventHandler());
		log_handler = new SVNLogHandler();
		try {
			this.SVN_url = SVNURL.parseURIDecoded(repository_url);
		} catch (SVNException e) {
			e.printStackTrace();
			System.exit(0);
		}
		SVNRevision start_revision = SVNRevision.create(time_interval
				.getStart().toDate());
		SVNRevision end_revision = SVNRevision.create(time_interval.getEnd()
				.toDate());
		revision_range = new SVNRevisionRange(start_revision, end_revision);
	}

}
