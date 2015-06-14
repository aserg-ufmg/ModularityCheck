package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractController implements Runnable {
	
	protected int numStages;
	protected int completedStages;
	
	protected List<File> failedProjects;
	
	protected String progressMessage;
	
	public AbstractController() {
		this.numStages = 0;
		this.completedStages = 0;
		
		this.failedProjects = new LinkedList<File>();
	}
	
	public int numStages() {
		return this.numStages;
	}
	
	protected void setNumStages(int numStages) {
		this.numStages = numStages;
	}
	
	public int numCompletedStages() {
		return this.completedStages;
	}
	
	protected void addCompletedStage() {
		this.completedStages++;
	}
	
	public List<File> getFailedProjects() {
		return this.failedProjects;
	}
	
	protected void addFailureProject(File failedProject) {
		this.failedProjects.add(failedProject);
	}
	
	public String getProgressMessage() {
		return this.progressMessage;
	}
	
	protected void setProgressMessage(String message) {
		this.progressMessage = message;
	}
	
	protected static void checkResultFolder(String folderName) {
		File resultFolder = new File(folderName);
		if (!resultFolder.exists()) resultFolder.mkdirs();
	}
}