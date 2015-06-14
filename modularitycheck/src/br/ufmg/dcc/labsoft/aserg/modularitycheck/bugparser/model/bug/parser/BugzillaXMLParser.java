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

public class BugzillaXMLParser extends BugTrackerParser {

	public BugzillaXMLParser(String[] filename) {
		super(filename);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Bug> getBugs() throws JDOMException, IOException {
		System.out.println("Parsing Bugzilla XML file...");
		List<Bug> bugs = new ArrayList<Bug>();
		for (String file : filename) {
			SAXBuilder builder = new SAXBuilder();
			Document xml = builder.build(new File(file));
			XPath bugPath = XPath.newInstance("//bug");
			List bug_nodes = bugPath.selectNodes(xml);
			Iterator it = bug_nodes.iterator();
			while (it.hasNext()) {
				Element bugElement = (Element) it.next();
				String id = bugElement.getChild("bug_id").getTextTrim();
				Bug bug = new Bug(id);
				XPath desc = XPath.newInstance("long_desc/thetext");
				List text_nodes = desc.selectNodes(bugElement);
				Iterator text_it = text_nodes.iterator();
				while (text_it.hasNext()) {
					Element desc_element = (Element) text_it.next();
					String text = desc_element.getTextTrim();
					bug.addText(text);
				}
				bugs.add(bug);
			}
		}
		return bugs;
	}

}
