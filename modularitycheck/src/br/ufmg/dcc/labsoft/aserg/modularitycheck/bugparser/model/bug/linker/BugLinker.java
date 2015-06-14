package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.linker;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jdom.JDOMException;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.Bug;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.parser.BugTrackerParser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.Commit;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.VersionControlManager;

public class BugLinker {

	private VersionControlManager version_control_manager;
	private BugTrackerParser bug_tracker_parser;

	public BugLinker(VersionControlManager vcparser, BugTrackerParser btparser) {
		version_control_manager = vcparser;
		bug_tracker_parser = btparser;
	}

	public Set<Bug> getLinkedBugs() throws JDOMException, IOException {
		Set<Bug> linkedBugs = null;
		List<Commit> commits = version_control_manager.getCommits();
		if(commits.size() > 0) {
			linkedBugs = new HashSet<Bug>();

			System.out.println("Number of Commits = " + commits.size());
			System.out.println("Linking bugs...");
			List<Bug> bugs = bug_tracker_parser.getBugs();
			
			int nbugs = bugs.size();
			System.out.println("Number of Bugs = " + nbugs);
	
			removeNotAssociatedIssues(commits, bugs);
			
			linkedBugs = removeTangledCommits(commits, bugs);
		}
		return linkedBugs;
		
	}

	/***
	 * Removing commits associated to multiple maintenance issues. Phase #4
	 * 
	 * @param commits
	 * @param bugs
	 * @return
	 */
	private Set<Bug> removeTangledCommits(List<Commit> commits, List<Bug> bugs) {
		Set<Bug> linkedBugs = new HashSet<Bug>();
		Set<Bug> tempLinkedBugs = null;
		for (Commit commit : commits) {
			tempLinkedBugs = new HashSet<Bug>();
			String msg = commit.getMsg();
			if (msg == null)
				msg = "";
			for (Bug bug : bugs) {
				String bugId = bug.getId();

				if (hasID(msg, bugId)) {

					for (String file : commit.getFiles()) {
						if (br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Utils
								.isValid(file))
							bug.addFile(file);
					}

					if (bug.getModifiedFiles().size() > 0)
						tempLinkedBugs.add(bug);
				}
			}
			if (tempLinkedBugs.size() == 1)
				linkedBugs.addAll(tempLinkedBugs);
		}
		System.out
				.println("Phase #4 - Number of Commits: " + linkedBugs.size());
		return linkedBugs;
	}

	/***
	 * When there are multiple commits referring to the same Issue-ID, we merge
	 * all of them�including the changed classes�in a single commit. Phase
	 * #3
	 * 
	 * @param commits
	 * @param bugs
	 */
	public void mergeCommits(List<Commit> commits, List<Bug> bugs) {
		int counter = 0;

		for (Commit commit : commits) {
			String msg = commit.getMsg();

			if (msg == null)
				msg = "";
			for (Bug bug : bugs) {
				String bugId = bug.getId();

				if (hasID(msg, bugId)) {

					for (String file : commit.getFiles()) {
						if (br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Utils
								.isValid(file))
							bug.addFile(file);
					}
				}
			}
		}

		for (Bug bug : bugs)
			if (bug.getModifiedFiles().size() > 0)
				counter++;

		System.out.println("Phase #3 - Number of Commits: " + counter);
	}

	/***
	 * Remove commits that change only test classes or do not change class files
	 * - Phase #2
	 * 
	 * @param commits
	 * @param bugs
	 */
	public void removeTestAndNotChangingClasses(List<Commit> commits,
			List<Bug> bugs) {

		int counter = 0;
		int unitary = 0;
		for (Commit commit : commits) {
			String msg = commit.getMsg();
			boolean committed = false;

			if (msg == null)
				msg = "";
			for (Bug bug : bugs) {
				String bugId = bug.getId();

				if (hasID(msg, bugId)) {

					for (String file : commit.getFiles()) {
						if (br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Utils
								.isValid(file))
							bug.addFile(file);
					}

					if (bug.getModifiedFiles().size() > 0) {
						counter++;
						committed = true;

					}
				}
				if (committed)
					break;
			}
		}
		for (Bug bug : bugs) {
			if (bug.getModifiedFiles().size() == 1)
				unitary++;
		}

		System.out.println("Phase #2 - Number of Commits: " + counter);
		System.out.println("Phase #2 - Number of Unitary Commits: " + unitary);

	}

	/***
	 * Count the number of mapped commits - Phase #1
	 * 
	 * @param commits
	 * @param bugs
	 */
	public void removeNotAssociatedIssues(List<Commit> commits, List<Bug> bugs) {
		int counter = 0;
		int unitary = 0;
		for (Commit commit : commits) {
			String msg = commit.getMsg();
			boolean commited = false;

			if (msg == null)
				msg = "";
			for (Bug bug : bugs) {
				String bugId = bug.getId();

				if (hasID(msg, bugId)) {
					if (!commited) {
						commited = true;
						counter++;

					}

				}
				if (commited)
					break;
			}
			if (!commited) {
				if (commit.getFiles().size() == 1)
					unitary++;
			}
		}
		System.out.println("Phase #1 - Number of Commits: " + counter);
		System.out.println("Phase #1 - Number of Unitary Commits: " + unitary);
	}

	private boolean hasID(String phrase, String word) {
		return hasID(phrase, word, null);
	}

	private boolean hasID(String phrase, String word, String extra_delims) {
		String default_delim = " .;,_[]{:}='()#\"%*|?~!";
		if (extra_delims != null)
			default_delim += extra_delims;
		StringTokenizer st = new StringTokenizer(phrase, default_delim);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.equals(word)) {
				return true;
			}
		}
		return false;
	}

	public void original(List<Commit> commits, List<Bug> bugs) {
		int counter = 0;
		Set<Bug> linkedBugs = new HashSet<Bug>();
		Set<Bug> tempLinkedBugs = null;
		for (Commit commit : commits) {
			tempLinkedBugs = new HashSet<Bug>();
			String msg = commit.getMsg();

			if (msg == null)
				msg = "";
			for (Bug bug : bugs) {
				String bugId = bug.getId();

				if (hasID(msg, bugId)) {

					for (String file : commit.getFiles()) {
						if (br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Utils
								.isValid(file))
							bug.addFile(file);
					}

					if (bug.getModifiedFiles().size() > 0) {
						tempLinkedBugs.add(bug);// pre4
						counter++;
					}

				}
			}
			if (tempLinkedBugs.size() == 1)
				linkedBugs.addAll(tempLinkedBugs);
		}
		System.out.println("Number of commits: " + counter);
		System.out.println("Linked bugs: " + linkedBugs.size());

	}
}
