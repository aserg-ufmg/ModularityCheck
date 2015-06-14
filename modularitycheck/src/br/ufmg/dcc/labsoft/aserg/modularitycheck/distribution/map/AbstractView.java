package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.map;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.AbstractController;

public abstract class AbstractView extends JInternalFrame {
	
	private static final long serialVersionUID = 4520698614926883560L;
	
	protected Thread progressMonitorThread;
	protected javax.swing.JProgressBar progressBar;
	protected javax.swing.JButton startExecutionButton;
	
	protected AbstractController controller;

	public AbstractView() {
		setVisible(true);
		setClosable(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        progressBar = new javax.swing.JProgressBar();
        progressBar.setStringPainted(true);
        startExecutionButton = new javax.swing.JButton();
        startExecutionButton.setText("Start");
	}
	
	public abstract void refresh();
	
    protected class PanelUpdater implements Runnable {

        private double value;
        public PanelUpdater() {}

		@Override
        public void run() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					while (progressMonitorThread != null && progressMonitorThread.isAlive()) {
		                try {
		                    Thread.sleep(1000);
		                } catch (InterruptedException ex) {
		                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		                }
		                
		                value = ((double) controller.numCompletedStages() / controller.numStages()) * 100;
		                progressBar.setValue((int) value);
		                progressBar.setString(controller.getProgressMessage());
		                progressBar.repaint();
		            }
		            
		            if (!controller.getFailedProjects().isEmpty()) {
		                StringBuilder sb = new StringBuilder();
		                for (File f : controller.getFailedProjects()) {
		                    sb.append("* ").append(f.getName());
		                    sb.append("\n");
		                }
		                JOptionPane.showMessageDialog(null, "Projects Failed:\nCause:\n" + sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		            }
		            
		            startExecutionButton.setText("Start");
				}
			});
        }
    }
}