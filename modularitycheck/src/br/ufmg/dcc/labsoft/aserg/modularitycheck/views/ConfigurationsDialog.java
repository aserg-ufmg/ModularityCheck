package br.ufmg.dcc.labsoft.aserg.modularitycheck.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ConfigurationsDialog {

	/**
	 * Error constants.
	 */
	private static final int NO_ERROR = 0;
	private static final int BEGIN_ERROR = 1;
	private static final int END_ERROR = 2;
	private static final int TYPE_ERROR = 3;
	private static final int URL_ERROR = 4;
	private static final int XML_ERROR = 5;
	private static final int MANAGER_ERROR = 6;

	/**
	 * Graphic components.
	 */
	private Shell dialogShell;
	private ScrolledComposite scrolledComposite;
	private Composite mainContainer;
	private Group grpRepositoryInformation;
	private Combo cmbRepoType;
	private Combo cmbRepoManager;
	private Label lblType;
	private Label lblUrl;
	private Text txtRepoUrl;
	private Group grpEvaluationPeriod;
	private Group grpBegin;
	private DateTime dtBegin;
	private Group grpEnd;
	private DateTime dtEnd;
	private Composite btContainer;
	private Button btnOk;
	private Button btnCancel;
	private Button btnXmlChoose;
	private Label lblVersionManager;

	/**
	 * Configuration data.
	 */
	private int repoType;
	private int repoManager;
	private String repoUrl;
	private String repoXml;
	private Date repoBegin;
	private Date repoEnd;
	private Label lblIssues;
	private Composite issuesContainer;
	private Text txtRepoXml;

	/**
	 * Default constructor.
	 */
	public ConfigurationsDialog() {
		repoType = 0;
		repoManager = 0;
		repoUrl = "";
		repoXml = "";
		repoBegin = new Date();
		repoEnd = new Date();
	}

	/**
	 * Parameterized constructor.
	 */
	public ConfigurationsDialog(int repoType, int repoManager, String repoUrl,
			String repoXml, Date repoBegin, Date repoEnd) {
		this.repoType = repoType;
		this.repoManager = repoManager;
		this.repoUrl = repoUrl;
		this.repoXml = repoXml;
		this.repoBegin = repoBegin;
		this.repoEnd = repoEnd;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void showDialog(Shell shell) {
		Display dialog = shell.getDisplay();
		dialogShell = new Shell(shell, SWT.CLOSE | SWT.TITLE | SWT.BORDER
				| SWT.OK | SWT.APPLICATION_MODAL);

		dialogShell.setText("Configure your repository");
		dialogShell.setSize(475, 291);
		dialogShell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Rectangle screenSize = dialog.getPrimaryMonitor().getBounds();
		dialogShell.setLocation(
				(screenSize.width - dialogShell.getBounds().width) / 2,
				(screenSize.height - dialogShell.getBounds().height) / 2);

		scrolledComposite = new ScrolledComposite(dialogShell, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(new Point(330, 200));

		mainContainer = new Composite(scrolledComposite, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));

		grpRepositoryInformation = new Group(mainContainer, SWT.NONE);
		GridData gd_grpRepositoryInformation = new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1);
		gd_grpRepositoryInformation.widthHint = 313;
		grpRepositoryInformation.setLayoutData(gd_grpRepositoryInformation);
		grpRepositoryInformation.setText("Repository Information");
		grpRepositoryInformation.setLayout(new GridLayout(2, false));

		lblType = new Label(grpRepositoryInformation, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblType.setBounds(0, 0, 70, 17);
		lblType.setText("Type");

		cmbRepoType = new Combo(grpRepositoryInformation, SWT.READ_ONLY);
		cmbRepoType.setVisibleItemCount(4);
		cmbRepoType.setItems(new String[] { "Select the repository type",
				"Bugzilla", "JIRA", "Tigris" });
		cmbRepoType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		cmbRepoType.select(repoType);

		lblVersionManager = new Label(grpRepositoryInformation, SWT.NONE);
		lblVersionManager.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblVersionManager.setText("Version Manager");

		cmbRepoManager = new Combo(grpRepositoryInformation, SWT.READ_ONLY);
		cmbRepoManager.setVisibleItemCount(3);
		cmbRepoManager.setItems(new String[] { "Select your version  manager",
				"GIT", "SVN" });
		cmbRepoManager.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		cmbRepoManager.select(repoManager);

		lblUrl = new Label(grpRepositoryInformation, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblUrl.setText("URL");

		txtRepoUrl = new Text(grpRepositoryInformation, SWT.BORDER);
		GridData gd_txtRepoUrl = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_txtRepoUrl.widthHint = 242;
		txtRepoUrl.setLayoutData(gd_txtRepoUrl);
		txtRepoUrl.setText(repoUrl);

		lblIssues = new Label(grpRepositoryInformation, SWT.NONE);
		lblIssues.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblIssues.setText("Issues (XML Format)");

		issuesContainer = new Composite(grpRepositoryInformation, SWT.NONE);
		GridData gd_issuesContainer = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_issuesContainer.widthHint = 328;
		issuesContainer.setLayoutData(gd_issuesContainer);
		GridLayout gl_issuesContainer = new GridLayout(2, false);
		gl_issuesContainer.marginWidth = 0;
		gl_issuesContainer.marginHeight = 0;
		issuesContainer.setLayout(gl_issuesContainer);

		txtRepoXml = new Text(issuesContainer, SWT.BORDER);
		GridData gd_txtRepoXml = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_txtRepoXml.widthHint = 308;
		txtRepoXml.setLayoutData(gd_txtRepoXml);
		txtRepoXml.setText(repoXml);

		btnXmlChoose = new Button(issuesContainer, SWT.NONE);
		btnXmlChoose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(Display.getCurrent()
						.getActiveShell(), SWT.OPEN);
				dlg.setMessage("Select the directory where your XML files are placed");
				dlg.setText("Issues (XML Format)");
				String path = dlg.open();
				if (path == null)
					return;
				txtRepoXml.setText(path);
			}
		});
		GridData gd_btnXmlChoose = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btnXmlChoose.widthHint = 32;
		btnXmlChoose.setLayoutData(gd_btnXmlChoose);
		btnXmlChoose.setText("...");

		grpEvaluationPeriod = new Group(mainContainer, SWT.NONE);
		GridData gd_grpEvaluationPeriod = new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1);
		gd_grpEvaluationPeriod.widthHint = 330;
		grpEvaluationPeriod.setLayoutData(gd_grpEvaluationPeriod);
		grpEvaluationPeriod.setText("Evaluation Period");
		grpEvaluationPeriod.setLayout(new GridLayout(2, false));

		grpBegin = new Group(grpEvaluationPeriod, SWT.NONE);
		grpBegin.setText("Begin");
		grpBegin.setBounds(0, 0, 68, 68);
		grpBegin.setLayout(new GridLayout(1, false));

		Calendar instance = Calendar.getInstance();
		instance.setTime(repoBegin);

		dtBegin = new DateTime(grpBegin, SWT.BORDER);
		dtBegin.setBounds(0, 0, 131, 29);
		dtBegin.setYear(instance.get(Calendar.YEAR));
		dtBegin.setMonth(instance.get(Calendar.MONTH));
		dtBegin.setDay(instance.get(Calendar.DAY_OF_MONTH));

		grpEnd = new Group(grpEvaluationPeriod, SWT.NONE);
		grpEnd.setText("End");
		grpEnd.setLayout(new GridLayout(1, false));

		instance.setTime(repoEnd);

		dtEnd = new DateTime(grpEnd, SWT.BORDER);
		dtEnd.setBounds(0, 0, 131, 29);
		dtEnd.setYear(instance.get(Calendar.YEAR));
		dtEnd.setMonth(instance.get(Calendar.MONTH));
		dtEnd.setDay(instance.get(Calendar.DAY_OF_MONTH));

		btContainer = new Composite(mainContainer, SWT.NONE);
		btContainer.setLayout(new FormLayout());
		GridData gd_btContainer = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_btContainer.widthHint = 45;
		btContainer.setLayoutData(gd_btContainer);

		btnOk = new Button(btContainer, SWT.NONE);
		FormData fd_btnOk = new FormData();
		fd_btnOk.left = new FormAttachment(0, 251);
		fd_btnOk.bottom = new FormAttachment(100);
		fd_btnOk.top = new FormAttachment(100, -29);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("OK");
		btnOk.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (validateConfigurations()) {
				case NO_ERROR:
					savePreferences();
					dialogShell.dispose();
					break;
				case BEGIN_ERROR:
					MessageDialog.open(SWT.ERROR, dialogShell,
							"Begin Date Error",
							"Set the correct date of begining.", SWT.OK);
					dtBegin.setFocus();
					break;
				case END_ERROR:
					MessageDialog.open(SWT.ERROR, dialogShell,
							"End Date Error",
							"Set the correct date of ending.", SWT.OK);
					dtEnd.setFocus();
					break;
				case TYPE_ERROR:
					MessageDialog.open(SWT.ERROR, dialogShell, "Type Error",
							"Select the repository type.", SWT.OK);
					cmbRepoType.setFocus();
					break;
				case URL_ERROR:
					MessageDialog.open(SWT.ERROR, dialogShell, "URL Error",
							"Enter a valid " + cmbRepoType.getText() + " URL.",
							SWT.OK);
					txtRepoUrl.setFocus();
					txtRepoUrl.selectAll();
					break;
				case XML_ERROR:
					MessageDialog.open(SWT.ERROR, dialogShell, "Missing XML File Error",
							"Select an existing XML file.",
							SWT.OK);
					btnXmlChoose.setFocus();
					break;
				case MANAGER_ERROR:
					MessageDialog.open(SWT.ERROR, dialogShell, "Version Manager Error",
							"Select the version manager type.",
							SWT.OK);
					cmbRepoManager.setFocus();
					break;
				default:
					break;
				}
			}
		});

		btnCancel = new Button(btContainer, SWT.NONE);
		fd_btnOk.right = new FormAttachment(btnCancel, -6);
		btnCancel.setText("Cancel");
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.top = new FormAttachment(btnOk, 0, SWT.TOP);
		fd_btnCancel.right = new FormAttachment(100);
		fd_btnCancel.bottom = new FormAttachment(100);
		fd_btnCancel.left = new FormAttachment(100, -99);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.dispose();
			}
		});

		scrolledComposite.setContent(mainContainer);

		dialogShell.open();
		while (!dialogShell.isDisposed()) {
			if (!dialog.readAndDispatch()) {
				dialog.sleep();
			}
		}
	}

	/**
	 * Save the parameters to run ModularityCheck.
	 */
	private void savePreferences() {

		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.DAY_OF_MONTH, dtBegin.getDay());
		instance.set(Calendar.MONTH, dtBegin.getMonth());
		instance.set(Calendar.YEAR, dtBegin.getYear());
		String begin = new SimpleDateFormat("yyyy-MM-dd").format(instance
				.getTime());

		instance.set(Calendar.DAY_OF_MONTH, dtEnd.getDay());
		instance.set(Calendar.MONTH, dtEnd.getMonth());
		instance.set(Calendar.YEAR, dtEnd.getYear());
		String end = new SimpleDateFormat("yyyy-MM-dd").format(instance
				.getTime());

		@SuppressWarnings("deprecation")
		Preferences preferences = new InstanceScope()
				.getNode(ModularityCheck.PLUGIN_ID);

		preferences.put(ModularityCheck.REPO_BEGIN, begin);
		preferences.put(ModularityCheck.REPO_END, end);
		preferences.put(ModularityCheck.REPO_TYPE,
				Integer.toString(cmbRepoType.getSelectionIndex()));
		preferences.put(ModularityCheck.REPO_MANAGER,
				Integer.toString(cmbRepoManager.getSelectionIndex()));
		preferences.put(ModularityCheck.REPO_URL, txtRepoUrl.getText());
		preferences.put(ModularityCheck.REPO_XML, txtRepoXml.getText());

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			MessageDialog.open(SWT.ERROR, dialogShell, "Error", e.getMessage(),
					SWT.OK);
		}
	}

	/**
	 * Validate the parameters to run ModularityCheck.
	 */
	private int validateConfigurations() {
		@SuppressWarnings("deprecation")
		Date begin = new Date(dtBegin.getYear(), dtBegin.getMonth(),
				dtBegin.getDay());
		
		@SuppressWarnings("deprecation")
		Date end = new Date(dtEnd.getYear(), dtEnd.getMonth(), dtEnd.getDay());

		if (begin.compareTo(new Date()) < 0)
			return BEGIN_ERROR;
		if (end.before(begin))
			return END_ERROR;
		if (cmbRepoType.getSelectionIndex() == 0)
			return TYPE_ERROR;
		if (txtRepoUrl.getText().isEmpty())
			return URL_ERROR;
		if (!(new File(txtRepoXml.getText())).exists())
			return XML_ERROR;
		return NO_ERROR;
	}
}
