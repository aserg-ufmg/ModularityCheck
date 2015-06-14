package br.ufmg.dcc.labsoft.aserg.modularitycheck.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.Preferences;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.enhancement.Enhancement;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;

public class MyNewSelectionAdapter extends SelectionAdapter {
	  private final Shell shell;
	  public String line;
	  public BufferedReader reader;

		 public MyNewSelectionAdapter(Shell shell) {
			    this.shell = shell;
			  }
		 
		 

	  @Override
	  public void widgetSelected(SelectionEvent e) {
		  
	    Job job = new Job("First Job") {
	      @Override
	      protected IStatus run(IProgressMonitor monitor) {
	        doLongThing();
	        syncWithUi();
	        // use this to open a Shell in the UI thread
	        return Status.OK_STATUS;
	      }

	    };
	    job.setUser(true);
	    job.schedule();
	  }

	  private void doLongThing() {
		  Job jobRunModularity = new Job("Running ModularityCheck") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Running ModularityCheck", IProgressMonitor.UNKNOWN);
					
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			jobRunModularity.schedule();
	  }

	  private void syncWithUi() {
	    Display.getDefault().asyncExec(new Runnable() {
	      public void run() {
	        MessageDialog.openInformation(shell, "Your Popup ",
	            "Your job has finished.");
	      }
	    });

	  }
	  
	  /*******
	   * Metodos da modularity check
	   */

	  
	  
	} 