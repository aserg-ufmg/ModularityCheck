package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source.svn;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;

public class SVNEventHandler implements ISVNEventHandler {

	@Override
	public void checkCancelled() throws SVNCancelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
		System.out.println(arg0.getFile());
	}

}
