package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.bug;

import java.util.HashSet;
import java.util.Set;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.HashCodeUtils;

public class Bug {

	private String id;
	private String text;
	private Set<String> modifiedFiles;

	@Override
	public int hashCode() {
		int result = HashCodeUtils.SEED;
		result = HashCodeUtils.hash(result, id);
		return result;
	}

	public Bug(String id) {
		this.id = id;
		text = "";
		modifiedFiles = new HashSet<String>();
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public Set<String> getModifiedFiles() {
		return modifiedFiles;
	}

	public void addFile(String file) {
		this.modifiedFiles.add(file);
	}

	public void addText(String text) {
		this.text += text;
	}

}
