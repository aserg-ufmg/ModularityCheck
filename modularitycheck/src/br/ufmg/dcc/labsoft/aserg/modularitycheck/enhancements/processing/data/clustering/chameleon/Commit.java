package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commit {
	private String transactionId;
	private List<Integer> files;
	private Date timestamp;
	private String author;
	private String message;
	
	public Commit(){
		transactionId = "";
		files = new ArrayList<Integer>();
	}
	
	public Commit(Commit commit){
		this.transactionId = commit.getTransactionId();
		this.author = commit.getAuthor();
		this.timestamp = commit.getTimestamp();
		this.files = new ArrayList<Integer>();
		this.files.addAll(commit.getFiles());
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public List<Integer> getFiles() {
		return files;
	}
	public void setFiles(int file) {
		if(!files.contains(file)) files.add(file);
	}
	
	public void setVariouFiles(List<Integer> files){
		files.addAll(files);
	}
	
	public String getAuthor(){
		return author;
	}
	
	public void setAuthor(String author){
		this.author = author;
	}

	public void setMsg(String msg) {
		message = msg;
		
	}
	
	public String getMsg(){
		return message;
	}


}
