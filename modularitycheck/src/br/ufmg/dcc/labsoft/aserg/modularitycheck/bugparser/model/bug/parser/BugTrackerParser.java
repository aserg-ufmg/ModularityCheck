package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.parser;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.Bug;

public abstract class BugTrackerParser {

	protected String[] filename;

	public BugTrackerParser(String[] filename) {
		this.filename = filename;
	}

	public abstract List<Bug> getBugs() throws JDOMException, IOException;

}
