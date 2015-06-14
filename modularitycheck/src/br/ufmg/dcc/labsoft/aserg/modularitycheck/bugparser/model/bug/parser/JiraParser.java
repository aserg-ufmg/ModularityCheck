package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug.Bug;

public class JiraParser extends BugTrackerParser {

	public JiraParser(String[] filename) {
		super(filename);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Bug> getBugs() throws JDOMException, IOException {
		System.out.println("Parsing Jira XML file...");
		List<Bug> bugs = new ArrayList<Bug>();
		for (String file : filename) {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(file));
			XPath bugPath = XPath.newInstance("//item");
			List bugsXML = bugPath.selectNodes(doc);
			Iterator iBugs = bugsXML.iterator();
			while (iBugs.hasNext()) {
				Element bugElement = (Element) iBugs.next();
				String id = bugElement.getChild("key").getTextTrim();
				Bug bug = new Bug(id);
				String component = null;
				String title = bugElement.getChild("title").getTextTrim();
				if (bugElement.getChild("component") != null)
					component = bugElement.getChild("component").getTextTrim();
				String summary = bugElement.getChild("summary").getTextTrim();
				String description = bugElement.getChild("description")
						.getTextTrim();
				bug.addText(title);
				if (component != null)
					bug.addText(" " + component + " ");
				bug.addText(summary);
				bug.addText(description);
				bugs.add(bug);
			}
		}
		return bugs;
	}

}
