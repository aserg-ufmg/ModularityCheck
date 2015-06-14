package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.parser;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNDiffGenerator;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class ChangedFile {

	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws Exception {
		File importDir = new File(args[1] + "\\Repository\\");
		if (!importDir.exists())
			importDir.mkdirs();

		String username = "guest";
		String password = "";
		String srcRepositoryURL = args[0];

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		SVNDiffClient diff = new SVNDiffClient(
				SVNWCUtil
						.createDefaultAuthenticationManager(username, password),
				SVNWCUtil.createDefaultOptions(true));
		ISVNDiffGenerator defaultSVNDiffGenerator = diff.getDiffGenerator();

		defaultSVNDiffGenerator.setDiffAdded(true);
		defaultSVNDiffGenerator.setDiffDeleted(true);

		diff.setDiffGenerator(defaultSVNDiffGenerator);
		diff.doDiff(SVNURL.parseURIDecoded(srcRepositoryURL), SVNRevision.HEAD,
				SVNRevision.create(263024), SVNRevision.create(263817),
				SVNDepth.UNKNOWN, false, stream);

		Writer writer = new BufferedWriter(new FileWriter(new File(importDir
				+ "\\arquivo.txt")));
		writer.write(stream.toString());
		writer.close();
	}

}
