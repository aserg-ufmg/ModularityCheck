package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.jdom.JDOMException;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.Bug;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.linker.BugLinker;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.parser.BugzillaXMLParser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.parser.JiraParser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.parser.TigrisParser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.git.GITManager;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.svn.SVNManager;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.FileUtils;

public class Parser {

	// Repository manager:
	public static final int MANAGER_GIT = 1;
	public static final int MANAGER_SVN = 2;

	// Repository type:
	public static final int TYPE_BUGZILLA = 1;
	public static final int TYPE_TIGRIS = 3;
	public static final int TYPE_JIRA = 2;

	public static String line;


	/**
	 * @param args	Configurações do repositório.
	 * 		args[0] -> endereço do repositório SVN.
	 * 		args[1] -> endereço do arquivo bugzilla ou jira.
	 * 		args[2] -> data de início da análise.
	 * 		args[3] -> data de fim da análise.
	 * 		args[4] -> tipo de repositório (BUGZILLA: 0, TIGRIS: 1, JIRA: 2).
	 *		args[5] -> tipo de repositório (GIT: 1, SVN: 2)
	 */
	public static boolean parse(String [] args) {

		File[] files = new File(args[1]).listFiles();
		int type = Integer.parseInt(args[4]);
		int manager = Integer.parseInt(args[5]);
		BugLinker bugLinker = null;

		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".xml"))
				list.add(files[i].getAbsolutePath());
		}

		String[] fileName = new String[list.size()];

		for (int i = 0; i < list.size(); i++)
			fileName[i] = list.get(i);

		if (manager == MANAGER_GIT) {
			switch (type) {
			case TYPE_BUGZILLA:
				bugLinker = new BugLinker(new GITManager(args[0], args[2],
						args[3]), new BugzillaXMLParser(fileName));
				break;
			case TYPE_JIRA:
				bugLinker = new BugLinker(new GITManager(args[0], args[2],
						args[3]), new JiraParser(fileName));
				break;
			case TYPE_TIGRIS:
				bugLinker = new BugLinker(new GITManager(args[0], args[2],
						args[3]), new TigrisParser(fileName));
				break;
			default:
				break;
			}
		} else if (manager == MANAGER_SVN) {
			switch (type) {
			case TYPE_BUGZILLA:
				bugLinker = new BugLinker(new SVNManager(args[0].trim(), args[2],
						args[3]), new BugzillaXMLParser(fileName));
				break;
			case TYPE_JIRA:
				bugLinker = new BugLinker(new SVNManager(args[0].trim(), args[2],
						args[3]), new JiraParser(fileName));
				break;
			case TYPE_TIGRIS:
				bugLinker = new BugLinker(new SVNManager(args[0].trim(), args[2],
						args[3]), new TigrisParser(fileName));
				break;
			default:
				break;
			}
		}

		Set<Bug> linkedBugs = null;
		try {
			if (bugLinker != null) {
				System.out.println("Getting bugs...");
				linkedBugs = bugLinker.getLinkedBugs();
			}
		} catch (JDOMException e) {
			line = e.getMessage();
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.open(SWT.ERROR, Display.getDefault()
							.getActiveShell(), "Error Parser", line, SWT.OK);
				}
			});
			return false;
		} catch (IOException e) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.open(SWT.ERROR, Display.getDefault()
							.getActiveShell(), "Error Parser", line, SWT.OK);
				}
			});
			return false;
		}

		File xml_location = new File(fileName[0]).getParentFile();
		File commentsFolder = new File(xml_location, "comments");
		FileUtils.deleteRecursive(commentsFolder);
		File filesFolder = new File(xml_location, "files");
		FileUtils.deleteRecursive(filesFolder);
		filesFolder.mkdir();
		commentsFolder.mkdir();

		if(linkedBugs == null) triggerErrorMessage();
		else if(linkedBugs.size() == 0) triggerErrorIssues();
		else {
			for (Bug bug : linkedBugs) {
				Writer writer;
				try {
					System.out.println(bug.getId());
					writer = new BufferedWriter(new FileWriter(new File(commentsFolder, bug.getId())));
					writer.append(bug.getText());
					writer.append(System.getProperty("line.separator"));
					writer.flush();
					writer.close();
				} catch (IOException e) {e.printStackTrace();}

				try {
					writer = new BufferedWriter(new FileWriter(new File(filesFolder, bug.getId())));

					for (String modifiedFile : bug.getModifiedFiles())	writer.append(modifiedFile + System.getProperty("line.separator"));

					writer.flush();
					writer.close();
				} catch (IOException e) {e.printStackTrace();}
			}
			return true;
		}
		return false;
	}



	private static void triggerErrorIssues() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.open(SWT.ERROR, Display.getDefault()
						.getActiveShell(), "Error Parser", "There are no commits linked to issue reports", SWT.OK);
			}
		});
		return;

	}


	private static void triggerErrorMessage() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.open(SWT.ERROR, Display.getDefault()
						.getActiveShell(), "Error Parser", "There are no commits on this repository or path not found: 404 Not Found", SWT.OK);
			}
		});
		return;
	}
}
