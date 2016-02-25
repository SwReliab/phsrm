package phsrm.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import phsrm.cphsrm.*;
import phsrm.common.*;
import phsrm.hersrm.HyperErlangSRM;
import phsrm.original.CommonSRM;

public class SRMMainPanel extends JPanel implements ActionListener, Runnable {
	JToolBar toolbar;
	JButton openButton;
	JButton fittingButton;
	JButton applyButton;
	
	String currentPath;
	LogPanel logbuf;

	JPanel mainPanel;
	CardLayout mainLayout;

	DataPanel datPane;
	EstimationResult estResPane1, estResPane2;
	EstimationResult estResPane0, estResPane3;

	CPHSRMPanel srmPane1;
	HErSRMPanel srmPane2;
	CommonSRMPanel srmPane3;

	ALLSRMControl srmCtrl;
	CPHSRMControl cphsrmCtrl;
	HErSRMControl hysrmCtrl;
	CommonSRMControl csrmCtrl;

	GroupData dat;
	
	ActionListener fital;

	SRMMainPanel(LogPanel logbuf, int ncph, int nher) {
		// panel configuration
		this.logbuf = logbuf;
		setLayout(new BorderLayout());

		initToolbar();

		mainPanel = new JPanel();
		mainLayout = new CardLayout();
		mainPanel.setLayout(mainLayout);
		JScrollPane sp = new JScrollPane(mainPanel);
		add(sp, BorderLayout.CENTER);

		cphsrmCtrl = new CPHSRMControl(logbuf, ncph);
		hysrmCtrl = new HErSRMControl(logbuf, nher);
		csrmCtrl = new CommonSRMControl(logbuf);
		srmCtrl = new ALLSRMControl(logbuf, cphsrmCtrl.getNumberOfSRMs(), 
				hysrmCtrl.getNumberOfSRMs(), csrmCtrl.getNumberOfSRMs());
		srmCtrl.setCPHSRM(cphsrmCtrl.getCPHSRM());
		srmCtrl.setHErSRM(hysrmCtrl.getHErSRM());
		srmCtrl.setCommonSRM(csrmCtrl.getCommonSRM());

		estResPane0 = new EstimationResult(logbuf);
		estResPane0.setSRMs(srmCtrl.getSortedSRMs());
		mainPanel.add(estResPane0, "EstimationResultPanel0");

		estResPane1 = new EstimationResult(logbuf);
		estResPane1.setSRMs(cphsrmCtrl.getSortedSRMs());
		mainPanel.add(estResPane1, "EstimationResultPanel1");

		estResPane2 = new EstimationResult(logbuf);
		estResPane2.setSRMs(hysrmCtrl.getSortedSRMs());
		mainPanel.add(estResPane2, "EstimationResultPanel2");

		estResPane3 = new EstimationResult(logbuf);
		estResPane3.setSRMs(csrmCtrl.getSortedSRMs());
		mainPanel.add(estResPane3, "EstimationResultPanel3");

		datPane = new DataPanel();
		mainPanel.add(datPane, "DataPanel");
		
		srmPane1 = new CPHSRMPanel(logbuf);
		mainPanel.add(srmPane1, "CPHSRMPanel");
		srmPane1.setCPHSRM(cphsrmCtrl.getCPHSRM(0));

		srmPane2 = new HErSRMPanel(logbuf);
		mainPanel.add(srmPane2, "HErSRMPanel");
		srmPane2.setHErSRM(hysrmCtrl.getHErSRM(0));

		srmPane3 = new CommonSRMPanel(logbuf);
		mainPanel.add(srmPane3, "CommonSRMPanel");
		srmPane3.setCommonSRM(csrmCtrl.getCommonSRM(0));

		setPreferredSize(new Dimension(1000,350));
	}

	private void initToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		openButton = new JButton("Open");
	    openButton.addActionListener(this);
		toolbar.add(openButton);
	    fittingButton = new JButton("Fitting");
	    fital = this;
		fittingButton.addActionListener(fital);
		toolbar.add(fittingButton);
		applyButton = new JButton("Apply");
		applyButton.addActionListener(this);
		toolbar.add(applyButton);
		add(toolbar, BorderLayout.NORTH);
	}

	public void changePanel(String name) {
		mainLayout.show(mainPanel, name);
		if (name.equals("CPHSRMPanel")) {
			fittingButton.removeActionListener(fital);
			applyButton.removeActionListener(fital);
			fital = srmPane1;
			fittingButton.addActionListener(fital);
			applyButton.addActionListener(fital);
		}
		if (name.equals("HErSRMPanel")) {
			fittingButton.removeActionListener(fital);
			applyButton.removeActionListener(fital);
			fital = srmPane2;
			fittingButton.addActionListener(fital);
			applyButton.addActionListener(fital);
		}
		if (name.equals("CommonSRMPanel")) {
			fittingButton.removeActionListener(fital);
			applyButton.removeActionListener(fital);
			fital = srmPane3;
			fittingButton.addActionListener(fital);
			applyButton.addActionListener(fital);
		}
		if (name.equals("EstimationResultPanel0") ||
				name.equals("EstimationResultPanel1") ||
				name.equals("EstimationResultPanel2") ||
				name.equals("EstimationResultPanel3")) {
			fittingButton.removeActionListener(fital);
			applyButton.removeActionListener(fital);
			fital = this;
			fittingButton.addActionListener(fital);
			applyButton.addActionListener(fital);
		}
	}
	
	public CPHSRM[] getCPHSRM() {
		return cphsrmCtrl.getCPHSRM();
	}

	public HyperErlangSRM[] getHErSRM() {
		return hysrmCtrl.getHErSRM();
	}
	
	public CommonSRM[] getCommonSRM() {
		return csrmCtrl.getCommonSRM();
	}

	public void changeCPHSRMModel(int i) {
		srmPane1.setCPHSRM(cphsrmCtrl.getCPHSRM(i));
	}

	public void changeHErSRMModel(int i) {
		srmPane2.setHErSRM(hysrmCtrl.getHErSRM(i));
	}

	public void changeCommonSRMModel(int i) {
		srmPane3.setCommonSRM(csrmCtrl.getCommonSRM(i));
	}

	public void run() {
		openButton.setEnabled(false);
		fittingButton.setEnabled(false);
		applyButton.setEnabled(false);

		csrmCtrl.doFitting();
		estResPane3.setEstimationResult();
		srmPane3.update();

		cphsrmCtrl.doFitting();
		estResPane1.setEstimationResult();
		srmPane1.update();

		hysrmCtrl.doFitting();
		estResPane2.setEstimationResult();
		srmPane2.update();

		estResPane0.setEstimationResult();

		openButton.setEnabled(true);
		fittingButton.setEnabled(true);
		applyButton.setEnabled(true);
		logbuf.appendln("Update estimattion result table.");
	}
	
	public void actionPerformed(ActionEvent ae){
		String ac = ae.getActionCommand();
		if (ac == "Open") {
			JFileChooser filechooser = new JFileChooser(currentPath);
			int selected = filechooser.showOpenDialog(openButton);
			if (selected == JFileChooser.APPROVE_OPTION){
				File file = filechooser.getSelectedFile();
				currentPath = file.getPath();
				dat = new GroupData();
				dat.readDataFromFile(file.getAbsolutePath());
				logbuf.clear();
				logbuf.appendln("Open: " + file.getAbsolutePath());
				datPane.setGroupData(dat, file.getName());
				estResPane0.setGroupData(dat, file.getName());
				estResPane1.setGroupData(dat, file.getName());
				estResPane2.setGroupData(dat, file.getName());
				estResPane3.setGroupData(dat, file.getName());
				srmCtrl.setGroupData(dat);
				cphsrmCtrl.setGroupData(dat);
				hysrmCtrl.setGroupData(dat);
				csrmCtrl.setGroupData(dat);
				srmPane1.setGroupData(dat, file.getName());
				srmPane2.setGroupData(dat, file.getName());
				srmPane3.setGroupData(dat, file.getName());
			}
		} else if (ac == "Fitting") {
			Thread dofitting = new Thread(this);
			dofitting.start();
		}
	}
}
