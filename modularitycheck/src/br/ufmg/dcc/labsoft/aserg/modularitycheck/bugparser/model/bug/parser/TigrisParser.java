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

public class TigrisParser extends BugTrackerParser {

	public TigrisParser(String[] filename) {
		super(filename);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Bug> getBugs() throws JDOMException, IOException {
		System.out.println("Parsing Tigris XML file...");
		List<Bug> bugs = new ArrayList<Bug>();
		for (String file : filename) {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(file));

			XPath bugPath = XPath.newInstance("//issue_id");
			List bugsXML = bugPath.selectNodes(doc);
			Iterator iBugs = bugsXML.iterator();
			while (iBugs.hasNext()) {
				Element bugElement = (Element) iBugs.next();
				String id = bugElement.getValue().trim();
				Bug bug = new Bug(id);

				if (bugElement.getParentElement().getChild("subcomponent") != null)
					bug.addText("\n"
							+ bugElement.getParentElement()
									.getChild("subcomponent").getTextTrim());

				if (bugElement.getParentElement().getChild("short_desc") != null)
					bug.addText("\n"
							+ bugElement.getParentElement()
									.getChild("short_desc").getTextTrim());

				XPath commentsXPath = XPath.newInstance("long_desc//thetext");
				List comments = commentsXPath.selectNodes(bugElement
						.getParentElement());
				Iterator commentsIT = comments.iterator();
				while (commentsIT.hasNext()) {
					Element comment = (Element) commentsIT.next();
					bug.addText(comment.getTextTrim());
					break;
				}

				if (!bug.getText().isEmpty()) {
					bugs.add(bug);
				}
			}
		}
		return bugs;
	}

}
