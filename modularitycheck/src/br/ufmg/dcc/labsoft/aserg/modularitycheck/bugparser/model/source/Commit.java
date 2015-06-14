package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source;

import java.util.ArrayList;
import java.util.List;

public class Commit {

	private String msg;
	private String revision;
	private String date;
	private List<String> files;

	public Commit(String msg) {
		this.msg = msg;
		this.files = new ArrayList<String>();
	}

	public String getMsg() {
		return msg;
	}

	public String getRevision() {
		return revision;
	}

	public String getDate() {
		return date;
	}

	public List<String> getFiles() {
		return files;
	}

	public boolean addFile(String file) {
		return files.add(file);
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
