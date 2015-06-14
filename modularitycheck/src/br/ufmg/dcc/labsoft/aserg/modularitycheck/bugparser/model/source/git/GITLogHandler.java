package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.Commit;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Properties;

public class GITLogHandler {

	private List<Commit> commits;

	public GITLogHandler() {
		commits = new ArrayList<Commit>();
	}

	public void handleLogEntry(Iterable<RevCommit> logs, Repository repository) {
		List<DiffEntry> diffs = null;
		Commit commit = null;

		for (RevCommit rev : logs) {
			StringBuilder builder = new StringBuilder();
			String temp = rev.getFullMessage().replaceAll("[\n]",
					Properties.BLANK);
			temp.replaceAll("[\n]", Properties.BLANK);

			builder.append(temp).append(Properties.BLANK);
			temp = rev.getShortMessage().replaceAll("[\n]", Properties.BLANK);
			temp.replaceAll("[\t]", Properties.BLANK);
			builder.append(temp).append(Properties.BLANK);

			commit = new Commit(builder.toString());

			try {
				diffs = getPaths(repository, rev);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (DiffEntry diff : diffs) {
				commit.addFile(diff.getNewPath());
			}
			commits.add(commit);
		}
	}

	private List<DiffEntry> getPaths(Repository repository, RevCommit rev)
			throws MissingObjectException, IncorrectObjectTypeException,
			IOException {
		RevCommit parent = null;
		List<DiffEntry> diffs = null;
		RevWalk rw = new RevWalk(repository);
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setDetectRenames(true);

		if (rev.getParentCount() > 0 && rev.getParent(0) != null) {
			parent = rw.parseCommit(rev.getParent(0).getId());
			diffs = df.scan(parent.getTree(), rev.getTree());
		} else {
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(
					null, rw.getObjectReader(), rev.getTree()));
		}

		return diffs;
	}

	public List<Commit> getCommits() {
		return commits;
	}
}
