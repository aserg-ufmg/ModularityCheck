package br.ufmg.dcc.labsoft.aserg.modularitycheck.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.Preferences;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.parser.Parser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.enhancement.Enhancement;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;

public class WindowContent {

	private int repoType;
	private int repoManager;
	private String repoUrl;
	private String repoXml;
	private Date repoBegin;
	private Date repoEnd;
	private String lastExec;
	private String lastParse;
	private int maxScattering;
	private int minClusterSize;
	private String line; 
	
	
		public WindowContent(){
			repoType = repoManager = maxScattering = minClusterSize = 0;
			repoUrl = repoXml = lastExec = "";
			repoBegin =  new Date();
			repoEnd = new Date();
		}
	
	  /**
		 * Call methods to verify the system modularity.
		 */
		public boolean runModularityCheck() {
			try {

					if (isExecutionNeeded()) {
						    if(runBugParser()) return runEnhancements();
						    return false;
					}
			} catch (Exception e) {
				line = e.getMessage();
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.open(SWT.ERROR, Display.getDefault()
								.getActiveShell(), "Error ModularityCheck", line, SWT.OK);
					}
				});
			}
			return true;
		}
		
		


		/**
		 * Run the
		 */
		public boolean runEnhancements() {
			try {
				if (!(new File(System.getProperty("java.io.tmpdir")
						+ "scluster.exe")).exists())
					loadResources("/lib/enhancements-Dependencies/", "scluster.exe");
				if (!(new File(System.getProperty("java.io.tmpdir")
						+ "libcluto.lib")).exists())
					loadResources("/lib/enhancements-Dependencies/", "libcluto.lib");

				String args[] = {
						repoXml,
						(new File(System.getProperty("java.io.tmpdir")
								+ "scluster.exe")).getAbsolutePath() };

				
				boolean exit = Enhancement.enhance(args, maxScattering, minClusterSize,
						repoManager);
				System.out.println("DONE");
				return exit;
				
				
			} catch (Exception e) {
				e.printStackTrace();
				line = e.getMessage();
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.open(SWT.ERROR, Display.getDefault()
								.getActiveShell(), "Error in Enhancements", line,
								SWT.OK);
					}
				});
			}
			return false;
		}
		
		
		/**
		 * Load the resources to run the clusterization program.
		 * 
		 * @param path
		 * @param fileName
		 * @throws IOException
		 * @throws FileNotFoundException
		 */
		public void loadResources(String path, String fileName) throws IOException,
				FileNotFoundException {
			InputStream input = getClass().getResourceAsStream(path + fileName);
			File file = new File(System.getProperty("java.io.tmpdir") + fileName);
			OutputStream output = new FileOutputStream(file);
			int read;
			byte[] bytes = new byte[1024];
			while ((read = input.read(bytes)) != -1)
				output.write(bytes, 0, read);
			input.close();
			output.close();
		}
		
		
		/**
		 * Run the bug parser.
		 */
		public boolean runBugParser() {
			try {
				String args[] = { repoUrl, repoXml,
						new SimpleDateFormat("yyyy-MM-dd").format(repoBegin),
						new SimpleDateFormat("yyyy-MM-dd").format(repoEnd),
						Integer.toString(repoType), Integer.toString(repoManager) };
				
				return Parser.parse(args);
			} catch (Error e) {
				MessageDialog.open(SWT.ERROR,
						Display.getDefault().getActiveShell(), "BugParser Error",
						e.getMessage(), SWT.OK);
			}
			return false;
		}
		
		
		/**
		 * Verifies if a call of runModularityCheck is necessary.
		 * 
		 * @return true if so, false otherwise.
		 */
		public boolean isExecutionNeeded() {
			String currExecParam = repoUrl + repoXml
					+ new SimpleDateFormat("yyyy-MM-dd").format(repoBegin)
					+ new SimpleDateFormat("yyyy-MM-dd").format(repoEnd)
					+ Integer.toString(repoType) + Integer.toString(repoManager)
					+ maxScattering + minClusterSize;
			if (currExecParam.equals(lastExec)) {
				if (new File(Properties.getResultPath() + ModularityCheck.DMAP_FILE).exists()
						&& new File(Properties.getResultPath()
								+ Properties.SIZE_DATA).exists()
						&& new File(Properties.getResultPath()
								+ Properties.DENSITY_DATA).exists()
						&& new File(Properties.getResultPath()
								+ Properties.FOCUS_DATA).exists()
						&& new File(Properties.getResultPath()
								+ Properties.SPREAD_DATA).exists()
						&& new File(Properties.getResultPath()
								+ Properties.WEIGHT_DATA).exists())
					return false;
				else
					return true;
			} else {
				@SuppressWarnings("deprecation")
				Preferences preferences = new InstanceScope().getNode(ModularityCheck.PLUGIN_ID);
				preferences.put(ModularityCheck.LAST_EXEC_PARAMS, currExecParam);
				lastExec = currExecParam;
			}
			return true;
		}
		
		
	
		
		/***** getters and setters *****/

		public int getRepoType() {
			return repoType;
		}

		public void setRepoType(int repoType) {
			this.repoType = repoType;
		}

		public int getRepoManager() {
			return repoManager;
		}

		public void setRepoManager(int repoManager) {
			this.repoManager = repoManager;
		}

		public String getRepoUrl() {
			return repoUrl;
		}

		public void setRepoUrl(String repoUrl) {
			this.repoUrl = repoUrl;
		}

		public String getRepoXml() {
			return repoXml;
		}

		public void setRepoXml(String repoXml) {
			this.repoXml = repoXml;
		}

		public Date getRepoBegin() {
			return repoBegin;
		}

		public void setRepoBegin(Date repoBegin) {
			this.repoBegin = repoBegin;
		}

		public Date getRepoEnd() {
			return repoEnd;
		}

		public void setRepoEnd(Date repoEnd) {
			this.repoEnd = repoEnd;
		}

		public String getLastExec() {
			return lastExec;
		}

		public void setLastExec(String lastExec) {
			this.lastExec = lastExec;
		}

		public int getMaxScattering() {
			return maxScattering;
		}

		public void setMaxScattering(int maxScattering) {
			this.maxScattering = maxScattering;
		}

		public int getMinClusterSize() {
			return minClusterSize;
		}

		public void setMinClusterSize(int minClusterSize) {
			this.minClusterSize = minClusterSize;
		}
		
		

		
		
		
}
