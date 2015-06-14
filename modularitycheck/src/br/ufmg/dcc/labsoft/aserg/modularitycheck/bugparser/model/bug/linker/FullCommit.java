package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.linker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.Commit;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.VersionControlManager;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.Utils;

public class FullCommit {
	private VersionControlManager version_control_manager;
	private ArrayList<String> files;

	public FullCommit(VersionControlManager vcparser) {
		version_control_manager = vcparser;
	}

	public void getCommit(String date) {
		System.out.println(date);
		readLogs(date);
	}

	private void readLogs(String date) {
		List<Commit> commits = version_control_manager.getCommits();
		System.out.println("Number of Commits = " + commits.size());
		int commitSequence = 0;
		Writer writer;

		File filesFolder = new File(
				"C:\\Users\\Gilmar\\Dropbox\\Testes\\JDT1\\FullCommits");
		filesFolder.mkdir();

		for (Commit commit : commits) {

			files = new ArrayList<String>();
			for (String file : commit.getFiles()) {
				if (!files.contains(file) && Utils.isTest(file))
					files.add(file);
			}
			try {
				writer = new BufferedWriter(new FileWriter(new File(
						filesFolder.getAbsolutePath(), "FILE"
								+ String.valueOf(commitSequence))));

				for (String file : files) {
					writer.append(file + System.getProperty("line.separator"));
				}
				commitSequence++;
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
