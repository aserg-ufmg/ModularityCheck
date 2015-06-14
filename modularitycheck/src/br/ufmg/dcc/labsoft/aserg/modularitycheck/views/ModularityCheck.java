package br.ufmg.dcc.labsoft.aserg.modularitycheck.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.*;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import swing2swt.layout.BorderLayout;

import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.service.prefs.Preferences;

import data.handler.CarryFileMemory;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.parser.Parser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.DistributionMap;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.FileUtilities;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.map.DistributionMapPanel;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.enhancement.Enhancement;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.views.tables.FillTables;

public class ModularityCheck extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufmg.dcc.labsoft.aserg.modularitycheck.views.ModularityCheck";

	public static final String PLUGIN_ID = "br.ufmg.dcc.labsoft.aserg.modularitycheck";
	public static final String REPO_BEGIN = "REPO_BEGIN";
	public static final String REPO_END = "REPO_END";
	public static final String REPO_MANAGER = "REPO_MANAGER";
	public static final String REPO_TYPE = "REPO_TYPE";
	public static final String REPO_URL = "REPO_URL";
	public static final String REPO_XML = "REPO_XML";
	public static final String LAST_EXEC_PARAMS = "LAST_EXEC_PARAMS";
	public static final String DMAP_FILE = "CLUSTERS_GLUTO.data";

	/**
	 * Loading message components.
	 */
	public String line;
	public BufferedReader reader;
	@Inject Shell shell ;
	/**
	 * Graphic components.
	 */
	private Composite cmpMainContainer;
	private Composite cmpResultsContainer;
	private ScrolledComposite scpScrollResults;
	private Group grpResults;
	private Group grpDistributionMap;
	private Button btnExecute;
	private Button btnConfigure;
	private ExpandBar expTablesContainer;
	private ExpandItem xpndtmClusterSize;
	private ExpandItem xpndtmClusterDensity;
	private ExpandItem xpndtmClusterFocus;
	private ExpandItem xpndtmClusterSpread;
	private ExpandItem xpndtmClusterWeight;
	private Table tbClusterSize;
	private TableColumn sizeMin;
	private TableColumn sizeMax;
	private TableColumn sizeAvg;
	private TableColumn sizeStd;
	private TableItem tbClusterSizeItem;
	private Table tbClusterDensity;
	private TableColumn densityMin;
	private TableColumn densityMax;
	private TableColumn densityStd;
	private TableColumn densityAvg;
	private TableItem tbClusterDensityItem;
	private Table tbClusterFocus;
	private TableColumn focusMin;
	private TableColumn focusMax;
	private TableColumn focusStd;
	private TableColumn focusAvg;
	private TableItem tbClusterFocusItem;
	private Table tbClusterSpread;
	private TableColumn spreadMin;
	private TableColumn spreadMax;
	private TableColumn spreadStd;
	private TableColumn spreadAvg;
	private TableItem tbClusterSpreadItem;
	private Table tbClusterWeight;
	private TableColumn weightMin;
	private TableColumn weightMax;
	private TableColumn weightStd;
	private TableColumn weightAvg;
	private TableItem tbClusterWeightItem;
	private Composite cmpDMapContainer;
	private Frame frame;
	private Panel panel;
	private JRootPane rootPane;
	private JScrollPane scrollPane;
	private Composite btContainer;
	private Label lblMaxScattering;
	private Spinner spMaxScattering;
	private Label lblMinimunClusterSize;
	private Spinner spMinClusterSize;
	private TabFolder tabFolder;
	private TabItem tabTables;
	private Table tbCoChangePatterns;
	private TableColumn patternsId;
	private TableColumn patternsFocus;
	private TableColumn patternsSpread;
	private TableColumn patternsType;
	private TabItem tabPatterns;
	private SashForm sashForm;
	private IActionBars bar;

	/**
	 * Repository informations.
	 */
	private int repoType;
	private int repoManager;
	private int maxScattering;
	private int minClusterSize;
	private Date repoBegin;
	private Date repoEnd;
	private String repoUrl;
	private String repoXml;
	private String lastExec;

	/**
	 * The constructor.
	 */
	public ModularityCheck() {
		repoType = 0;
		repoManager = 0;
		repoUrl = "";
		repoXml = "";
		lastExec = "";
		repoBegin = new Date();
		repoEnd = new Date();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		cmpMainContainer = new Composite(parent, SWT.NONE);
		cmpMainContainer.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		cmpMainContainer.setLayout(new BorderLayout(0, 0));

		grpResults = new Group(cmpMainContainer, SWT.NONE);
		grpResults.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		grpResults.setText("Results");
		grpResults.setLayoutData(BorderLayout.WEST);
		grpResults.setLayout(new FillLayout(SWT.HORIZONTAL));

		scpScrollResults = new ScrolledComposite(grpResults, SWT.H_SCROLL
				| SWT.V_SCROLL);
		scpScrollResults.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		scpScrollResults.setExpandHorizontal(true);
		scpScrollResults.setExpandVertical(true);

		cmpResultsContainer = new Composite(scpScrollResults, SWT.NONE);
		cmpResultsContainer.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		cmpResultsContainer.setSize(200, 200);
		cmpResultsContainer.setLayout(new GridLayout(1, false));

		btContainer = new Composite(cmpResultsContainer, SWT.NONE);
		btContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btContainer.setLayout(new GridLayout(2, false));

		btnExecute = new Button(btContainer, SWT.NONE);
		btnExecute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnExecute.setText("Execute");
						
		new Label(btContainer, SWT.NONE);

		btnConfigure = new Button(btContainer, SWT.NONE);
		btnConfigure.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnConfigure.setText("Configure");
		
		new Label(btContainer, SWT.NONE);

		lblMaxScattering = new Label(btContainer, SWT.NONE);
		lblMaxScattering.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblMaxScattering.setText("Maximum Scattering");

		spMaxScattering = new Spinner(btContainer, SWT.BORDER);
		spMaxScattering.setMinimum(10);

		lblMinimunClusterSize = new Label(btContainer, SWT.NONE);
		lblMinimunClusterSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblMinimunClusterSize.setText("Minimum Cluster Size");

		spMinClusterSize = new Spinner(btContainer, SWT.BORDER);
		spMinClusterSize.setMinimum(4);

		scpScrollResults.setContent(cmpResultsContainer);
		scpScrollResults.setMinSize(new Point(200, 640));

		tabFolder = new TabFolder(cmpResultsContainer, SWT.NONE);
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		tabTables = new TabItem(tabFolder, SWT.NONE);
		tabTables.setText("Information");

		expTablesContainer = new ExpandBar(tabFolder, SWT.NONE);
		expTablesContainer.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		tabTables.setControl(expTablesContainer);

		xpndtmClusterSize = new ExpandItem(expTablesContainer, SWT.NONE);
		xpndtmClusterSize.setExpanded(true);
		xpndtmClusterSize.setText("Cluster Size");

		tbClusterSize = new Table(expTablesContainer, SWT.BORDER
				| SWT.FULL_SELECTION);
		xpndtmClusterSize.setControl(tbClusterSize);
		tbClusterSize.setHeaderVisible(true);
		tbClusterSize.setLinesVisible(true);
		xpndtmClusterSize.setHeight(65);

		sizeMin = new TableColumn(tbClusterSize, SWT.CENTER);
		sizeMin.setResizable(false);
		sizeMin.setWidth(50);
		sizeMin.setText("Min");

		sizeMax = new TableColumn(tbClusterSize, SWT.CENTER);
		sizeMax.setResizable(false);
		sizeMax.setWidth(50);
		sizeMax.setText("Max");

		sizeAvg = new TableColumn(tbClusterSize, SWT.CENTER);
		sizeAvg.setResizable(false);
		sizeAvg.setWidth(50);
		sizeAvg.setText("Avg");

		sizeStd = new TableColumn(tbClusterSize, SWT.CENTER);
		sizeStd.setWidth(50);
		sizeStd.setText("Std");

		tbClusterSizeItem = new TableItem(tbClusterSize, SWT.NONE);
		tbClusterSizeItem
				.setText(new String[] { "0.00", "0.00", "0.00", "0.00" });

		xpndtmClusterDensity = new ExpandItem(expTablesContainer, SWT.NONE);
		xpndtmClusterDensity.setExpanded(true);
		xpndtmClusterDensity.setText("Cluster Density");

		tbClusterDensity = new Table(expTablesContainer, SWT.BORDER
				| SWT.FULL_SELECTION);
		xpndtmClusterDensity.setControl(tbClusterDensity);
		tbClusterDensity.setHeaderVisible(true);
		tbClusterDensity.setLinesVisible(true);
		xpndtmClusterDensity.setHeight(65);

		densityMin = new TableColumn(tbClusterDensity, SWT.CENTER);
		densityMin.setWidth(50);
		densityMin.setText("Min");

		densityMax = new TableColumn(tbClusterDensity, SWT.CENTER);
		densityMax.setWidth(50);
		densityMax.setText("Max");

		densityAvg = new TableColumn(tbClusterDensity, SWT.CENTER);
		densityAvg.setWidth(50);
		densityAvg.setText("Avg");

		densityStd = new TableColumn(tbClusterDensity, SWT.CENTER);
		densityStd.setWidth(50);
		densityStd.setText("Std");

		tbClusterDensityItem = new TableItem(tbClusterDensity, SWT.NONE);
		tbClusterDensityItem.setText(new String[] { "0.00", "0.00", "0.00",
				"0.00" });

		xpndtmClusterWeight = new ExpandItem(expTablesContainer, SWT.NONE);
		xpndtmClusterWeight.setExpanded(true);
		xpndtmClusterWeight.setText("Cluster Average Edge's Weight");

		tbClusterWeight = new Table(expTablesContainer, SWT.BORDER
				| SWT.FULL_SELECTION);
		xpndtmClusterWeight.setControl(tbClusterWeight);
		tbClusterWeight.setHeaderVisible(true);
		tbClusterWeight.setLinesVisible(true);
		xpndtmClusterWeight.setHeight(65);

		weightMin = new TableColumn(tbClusterWeight, SWT.CENTER);
		weightMin.setWidth(50);
		weightMin.setText("Min");

		weightMax = new TableColumn(tbClusterWeight, SWT.CENTER);
		weightMax.setWidth(50);
		weightMax.setText("Max");

		weightAvg = new TableColumn(tbClusterWeight, SWT.CENTER);
		weightAvg.setWidth(50);
		weightAvg.setText("Avg");

		weightStd = new TableColumn(tbClusterWeight, SWT.CENTER);
		weightStd.setWidth(50);
		weightStd.setText("Std");

		tbClusterWeightItem = new TableItem(tbClusterWeight, SWT.NONE);
		tbClusterWeightItem.setText(new String[] { "0.00", "0.00", "0.00",
				"0.00" });

		xpndtmClusterFocus = new ExpandItem(expTablesContainer, SWT.NONE);
		xpndtmClusterFocus.setExpanded(true);
		xpndtmClusterFocus.setText("Cluster Focus");

		tbClusterFocus = new Table(expTablesContainer, SWT.BORDER
				| SWT.FULL_SELECTION);
		xpndtmClusterFocus.setControl(tbClusterFocus);
		tbClusterFocus.setHeaderVisible(true);
		tbClusterFocus.setLinesVisible(true);
		xpndtmClusterFocus.setHeight(65);

		focusMin = new TableColumn(tbClusterFocus, SWT.CENTER);
		focusMin.setWidth(50);
		focusMin.setText("Min");

		focusMax = new TableColumn(tbClusterFocus, SWT.CENTER);
		focusMax.setWidth(50);
		focusMax.setText("Max");

		focusAvg = new TableColumn(tbClusterFocus, SWT.CENTER);
		focusAvg.setWidth(50);
		focusAvg.setText("Avg");

		focusStd = new TableColumn(tbClusterFocus, SWT.CENTER);
		focusStd.setWidth(50);
		focusStd.setText("Std");

		tbClusterFocusItem = new TableItem(tbClusterFocus, SWT.NONE);
		tbClusterFocusItem.setText(new String[] { "0.00", "0.00", "0.00",
				"0.00" });

		xpndtmClusterSpread = new ExpandItem(expTablesContainer, SWT.NONE);
		xpndtmClusterSpread.setExpanded(true);
		xpndtmClusterSpread.setText("Cluster Spread");

		tbClusterSpread = new Table(expTablesContainer, SWT.BORDER
				| SWT.FULL_SELECTION);
		xpndtmClusterSpread.setControl(tbClusterSpread);
		tbClusterSpread.setHeaderVisible(true);
		tbClusterSpread.setLinesVisible(true);
		xpndtmClusterSpread.setHeight(65);

		spreadMin = new TableColumn(tbClusterSpread, SWT.CENTER);
		spreadMin.setWidth(50);
		spreadMin.setText("Min");

		spreadMax = new TableColumn(tbClusterSpread, SWT.CENTER);
		spreadMax.setWidth(50);
		spreadMax.setText("Max");

		spreadAvg = new TableColumn(tbClusterSpread, SWT.CENTER);
		spreadAvg.setWidth(50);
		spreadAvg.setText("Avg");

		spreadStd = new TableColumn(tbClusterSpread, SWT.CENTER);
		spreadStd.setWidth(50);
		spreadStd.setText("Std");

		tbClusterSpreadItem = new TableItem(tbClusterSpread, SWT.NONE);
		tbClusterSpreadItem.setText(new String[] { "0.00", "0.00", "0.00",
				"0.00" });

		tabPatterns = new TabItem(tabFolder, SWT.NONE);
		tabPatterns.setText("Co-change Patterns");

		tabPatterns.setControl(tbCoChangePatterns);

		sashForm = new SashForm(tabFolder, SWT.NONE);
		tabPatterns.setControl(sashForm);

		tbCoChangePatterns = new Table(sashForm, SWT.BORDER
				| SWT.FULL_SELECTION);
		tbCoChangePatterns.setHeaderVisible(true);
		tbCoChangePatterns.setLinesVisible(true);

		patternsId = new TableColumn(tbCoChangePatterns, SWT.NONE);
		patternsId.setWidth(30);
		patternsId.setText("ID");

		patternsFocus = new TableColumn(tbCoChangePatterns, SWT.NONE);
		patternsFocus.setWidth(50);
		patternsFocus.setText("Focus");

		patternsSpread = new TableColumn(tbCoChangePatterns, SWT.NONE);
		patternsSpread.setWidth(50);
		patternsSpread.setText("Spread");

		patternsType = new TableColumn(tbCoChangePatterns, SWT.NONE);
		patternsType.setWidth(60);
		patternsType.setText("Type");

		grpDistributionMap = new Group(cmpMainContainer, SWT.NONE);
		grpDistributionMap.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		grpDistributionMap.setText("Distribution Map");
		grpDistributionMap.setLayoutData(BorderLayout.CENTER);
		grpDistributionMap.setLayout(new FillLayout(SWT.HORIZONTAL));

		cmpDMapContainer = new Composite(grpDistributionMap, SWT.EMBEDDED);
		cmpDMapContainer.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));

		frame = SWT_AWT.new_Frame(cmpDMapContainer);
		frame.setForeground(new Color(255, 255, 255));
		frame.setBackground(Color.WHITE);

		panel = new Panel();
		panel.setForeground(new Color(255, 255, 255));
		panel.setBackground(Color.WHITE);
		frame.add(panel);
		panel.setLayout(new java.awt.BorderLayout(0, 0));

		rootPane = new JRootPane();
		rootPane.setForeground(new Color(255, 255, 255));
		rootPane.getContentPane().setForeground(
				UIManager.getColor("Desktop.background"));
		rootPane.getContentPane().setBackground(Color.WHITE);
		rootPane.setBackground(Color.WHITE);
		panel.add(rootPane);

		scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		rootPane.getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
		
		
		/**** listeners ******/
		
		
		btnExecute.addSelectionListener(new MySelectionAdapter(shell));
		
		btnConfigure.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadPreferences();
				ConfigurationsDialog cd = new ConfigurationsDialog(repoType,
						repoManager, repoUrl, repoXml, repoBegin, repoEnd);
				cd.showDialog(Display.getDefault().getActiveShell());
				loadPreferences();
			}
		});
		
		
		
		
	}/******************* END PART CONTROL ****************/

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		btnExecute.setFocus();
	}
	
	

	
	  /**
		 * Load the parameters to run ModularityCheck.
		 */
		public void loadPreferences() {
			@SuppressWarnings("deprecation")
			Preferences preferences = new InstanceScope().getNode(PLUGIN_ID);
			try {
				repoType = preferences.getInt(REPO_TYPE, 0);
				repoManager = preferences.getInt(REPO_MANAGER, 0);
				repoUrl = preferences.get(REPO_URL, " ");
				repoXml = preferences.get(REPO_XML, " ");
				repoBegin = new SimpleDateFormat("yyyy-MM-dd").parse(preferences
						.get(REPO_BEGIN, (new Date()).toString()));
				repoEnd = new SimpleDateFormat("yyyy-MM-dd").parse(preferences.get(
						REPO_END, (new Date()).toString()));
				lastExec = preferences.get(LAST_EXEC_PARAMS, " ");
			} catch (ParseException e) {
				ConfigurationsDialog cd = new ConfigurationsDialog();
				cd.showDialog(Display.getDefault().getActiveShell());
			}
		}
		
		
		/**
		 * Fill clusters metrics table.
		 */
		public void fillTables() {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					tbClusterSizeItem.setText(FillTables
							.fillTableClusterSize(repoXml));
					tbClusterDensityItem.setText(FillTables
							.fillTableClusterDensity(repoXml));
					tbClusterFocusItem.setText(FillTables
							.fillTableClusterFocus(repoXml));
					tbClusterSpreadItem.setText(FillTables
							.fillTableClusterSpread(repoXml));
					tbClusterWeightItem.setText(FillTables
							.fillTableClusterWeight(repoXml));
					fillCoChangeTable(repoXml);
				}
			});
		}

		/**
		 * Fill co-change metrics table.
		 */
		public void fillCoChangeTable(String root) {
			tbCoChangePatterns.removeAll();
			//Properties.setDefaultPaths(root);
			try{
				double[] focuses = loadArray(Properties.getResultPath()
						+ Properties.FOCUS_DATA);
				double[] spreads = loadArray(Properties.getResultPath()
						+ Properties.SPREAD_DATA);
				String clusterType;
				for (int i = 0; i < focuses.length; i++) {
					if (focuses[i] == 1)
						clusterType = "well-encapsulated";
					else if ((focuses[i] >= 0.8) && (focuses[i] < 1)
							&& (spreads[i] > 1))
						clusterType = "partially encapsulated";
					else if (focuses[i] <= 0.4 && spreads[i] > 2)
						clusterType = "crosscuting";
					else if (spreads[i] == 1)
						clusterType = "well-confined";
					else
						clusterType = "undefined";
					TableItem item = new TableItem(tbCoChangePatterns, SWT.NONE);
					item.setText(new String[] { String.valueOf(i),
							String.format("%.2f", focuses[i]),
							String.format("%.2f", spreads[i]), clusterType });
				}
			}catch(Exception e){
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.open(SWT.ERROR, Display.getDefault()
								.getActiveShell(), "Error ModularityCheck", "There is no clusters to display", SWT.OK);
					}
				});
			}
		}
		
		/**
		 * Load an array with co-change metrics.
		 * 
		 * @param fileName
		 * @return
		 */
		private double[] loadArray(String fileName) {
			try {
				CarryFileMemory carry = new CarryFileMemory(fileName);
				String[] buffer = carry.carryCompleteFile();
				double array[] = new double[buffer.length];
				for (int i = 0; i < buffer.length; i++)
					array[i] = Double.parseDouble(buffer[i]);
				return array;
			} catch (Exception e) {
				return null;
			}
		}
		

		/**
		 * Draw the distribution map.
		 */
		public void loadDistributionMap() {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						//Properties.setDefaultPaths(repoXml);
						File file = new File(Properties.getResultPath() + DMAP_FILE);
						String projectName = file.getName();
						String[] classIds = FileUtilities.readClassIds(file
								.getAbsolutePath());
						int[][] clusters = FileUtilities.readClusters(file
								.getAbsolutePath());
						DistributionMap distributionMap = DistributionMap
								.generateDistributionMap(projectName, classIds,
										clusters);
						DistributionMapPanel distributionMapPanel = new DistributionMapPanel(
								distributionMap);
						distributionMapPanel
								.setBackground(new Color(255, 255, 255));
						scrollPane.setViewportView(distributionMapPanel);
						scrollPane.revalidate();
						scrollPane.repaint();
						cmpDMapContainer.redraw();
					} catch (Exception e) {
						MessageDialog.open(SWT.ERROR, Display.getDefault()
								.getActiveShell(), "Error: There are no co-change clusters", e.getMessage(), SWT.OK);
					}
				}
			});
		}
		
	

		

		/**
		 * Redirects the standard system I/O to a text area in UI.
		 */
		public void loadingMessage() {
			PrintStream old = System.out;
			try {
				PipedOutputStream pOut = new PipedOutputStream();
				System.setOut(new PrintStream(pOut));
				PipedInputStream pIn;
				pIn = new PipedInputStream(pOut);
				line = "";
				reader = new BufferedReader(new InputStreamReader(pIn));
				while (!line.equals("FINISHED")) {
					line = reader.readLine();
					if (line != null) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								bar = getViewSite().getActionBars();
								bar.getStatusLineManager().setMessage(line);
							}
						});
					}
				}
				reader.close();
				//System.setOut(old);
			} catch (IOException e) {
				line = e.getMessage();
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.open(SWT.ERROR, Display.getDefault()
								.getActiveShell(), "Error", line, SWT.OK);
					}
				});
				return;
			} finally {
				System.setOut(old);
			}
		}



	


	private class MySelectionAdapter extends SelectionAdapter{
		 private final Shell shells;

		private WindowContent contents;

		 
		 public MySelectionAdapter(Shell shell) {
			    this.shells = shell;
			    contents = new WindowContent();
		  }

		 private void copyData() {
			loadPreferences();
			contents.setMaxScattering(Integer.parseInt(spMaxScattering.getText()));
			contents.setMinClusterSize(Integer.parseInt(spMinClusterSize.getText()));		
		 }

			@Override
			  public void widgetSelected(SelectionEvent e) {
				copyData();
				
				Job jobProgressMessage = new Job("Progress Monitor") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
					//	if(monitor.isCanceled()) new Status(IStatus.CANCEL, "com.ibm.jdg2e.concurrency", IStatus.CANCEL, "Job done", null); 
						try{
							monitor.beginTask("Progress Monitor", IProgressMonitor.UNKNOWN);
							loadingMessage();
						}finally{
						monitor.done();
				        }
						return Status.OK_STATUS;
					}
				};

				jobProgressMessage.setUser(true);
				jobProgressMessage.schedule();
////				
////				
//				if(!jobProgressMessage.cancel()){
//					try{
//						jobProgressMessage.join();
//					}catch(InterruptedException e1) {
//						e1.printStackTrace();
//					}finally{
//						if(jobProgressMessage != null){
//							if(jobProgressMessage.getResult().getCode() == Status.CANCEL) System.out.println("Job canceled successfully");
//							else System.out.println("Job is running to completion");
//						}
//					}
//				}else System.out.println("Job canceled");
				



				
			    Job job = new Job("Running ModularityCheck") {
			      @Override
			      protected IStatus run(IProgressMonitor monitor) {

			    	if(contents.runModularityCheck()){
						fillTables();
						loadDistributionMap();
						syncWithUi();
			    	}

			    	System.out.println("FINISHED");
			    	
					return Status.OK_STATUS;

			      }

			    };

			//    job.setUser(true);
			    job.schedule();

			    
//			    job.addJobChangeListener(new JobChangeAdapter(){
//			    	@Override public void scheduled(IJobChangeEvent event){
//			    		 
//					    		Runnable update=new Runnable(){
//					    		      @Override public void run(){
//					    		    	  loadingMessage();
//					    		      }
//					    		};  
//					    		shell.getDisplay().asyncExec(update);
//			    	}
//			    });
			    

			  }

			

			  private void syncWithUi() {
				//  fillTables();
				//	loadDistributionMap();
  
				
			    Display.getDefault().asyncExec(new Runnable() {
			      public void run() {
			    	
		    	    MessageDialog.openInformation(shell, "ModularityCheck ", "Your job has finished.");
			      }
			    });
			    return;
			  }
			  
				
				 /**
				 * Load the parameters to run ModularityCheck.
				 */
				private void loadPreferences() {
					@SuppressWarnings("deprecation")
					Preferences preferences = new InstanceScope().getNode(PLUGIN_ID);
					try {
						contents.setRepoType(preferences.getInt(REPO_TYPE, 0));
						contents.setRepoManager(preferences.getInt(REPO_MANAGER, 0));
						contents.setRepoUrl(preferences.get(REPO_URL, " "));
						contents.setRepoXml(preferences.get(REPO_XML, " ")); 
						contents.setRepoBegin(new SimpleDateFormat("yyyy-MM-dd").parse(preferences
								.get(REPO_BEGIN, (new Date()).toString())));
						contents.setRepoEnd(new SimpleDateFormat("yyyy-MM-dd").parse(preferences.get(
								REPO_END, (new Date()).toString())));
						contents.setLastExec(preferences.get(LAST_EXEC_PARAMS, " "));
						
						} catch (ParseException e) {
							ConfigurationsDialog cd = new ConfigurationsDialog();
							cd.showDialog(Display.getDefault().getActiveShell());
						}
				}
	}
	
	
	
	//http://www.vogella.com/tutorials/EclipseJobs/article.html
}