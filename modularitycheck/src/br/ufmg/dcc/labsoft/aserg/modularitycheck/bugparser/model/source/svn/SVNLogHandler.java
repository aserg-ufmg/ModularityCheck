package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.Commit;

public class SVNLogHandler implements ISVNLogEntryHandler {

	private List<Commit> commits;

	public SVNLogHandler() {
		commits = new ArrayList<Commit>();
	}

	@Override
	public void handleLogEntry(SVNLogEntry entry) throws SVNException {
		Map<String, SVNLogEntryPath> paths = entry.getChangedPaths();
		Collection<SVNLogEntryPath> files = paths.values();
		Commit commit = new Commit(entry.getMessage());
		commit.setRevision(String.valueOf(entry.getRevision()));
		commit.setDate(String.valueOf(entry.getDate()));
		for (SVNLogEntryPath svnLogEntryPath : files) {
			commit.addFile(svnLogEntryPath.getPath());
		}
		commits.add(commit);
	}

	public List<Commit> getCommits() {
		return commits;
	}

	public void printLog() {
		for (Commit commit : commits) {
			System.out.println(commit.getMsg());
			System.out.println("------");
		}
	}

}
