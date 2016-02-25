package phsrm.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import phsrm.common.*;
import phsrm.cphsrm.CPHSRM;
import phsrm.hersrm.HyperErlangSRM;
import phsrm.original.CommonSRM;

public class CommonSRMPanel extends JPanel implements ActionListener {
	static String ln = System.getProperty("line.separator");

	CommonSRM srm;
	EstimationResultGraph estGraph;
	GroupData dat;
	
	boolean flagDataSet;

	JTextArea params;
	JTextArea estResult;

	JTextField maxIteVal;
	JTextField accVal;
	
	JRadioButton llf;
//	JRadioButton para;
	JRadioButton fixed;
	
	LogPanel logbuf;

	CommonSRMPanel(LogPanel logbuf) {
		// panel configuration
		this.logbuf = logbuf;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel pp1 = new JPanel();
		pp1.setLayout(new BorderLayout());
		params = new JTextArea();
		JScrollPane sppara = new JScrollPane(params);
		sppara.setPreferredSize(new Dimension(400,100));
		pp1.add(new JLabel("Parameters"), BorderLayout.NORTH);
		pp1.add(sppara, BorderLayout.CENTER);

/*
		JPanel pp2 = new JPanel();
		pp2.setLayout(new BorderLayout());
		plotform = new JTextArea();
		JScrollPane spplot = new JScrollPane(plotform);
		spplot.setPreferredSize(new Dimension(400,100));
		pp2.add(new JLabel("Plot Functions"), BorderLayout.NORTH);
		pp2.add(spplot, BorderLayout.CENTER);
*/
		JPanel pp3 = new JPanel();
		pp3.setLayout(new BorderLayout());
		estResult = new JTextArea();
		JScrollPane spest = new JScrollPane(estResult);
		spest.setPreferredSize(new Dimension(400,100));
		pp3.add(new JLabel("Estimation Result"), BorderLayout.NORTH);
		pp3.add(spest, BorderLayout.CENTER);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(pp1);
//		leftPanel.add(pp2);
		leftPanel.add(pp3);
		leftPanel.setMaximumSize(new Dimension(410,450));

		estGraph = new EstimationResultGraph(logbuf);
		
		maxIteVal = new JTextField();
		accVal = new JTextField();
		
		JPanel maxItePane = new JPanel();
		maxItePane.add(new JLabel("Max Iteration:"));
		maxIteVal = new JTextField();
		maxIteVal.setPreferredSize(new Dimension(80,20));
		maxItePane.add(maxIteVal);

		JPanel accPane = new JPanel();
		accPane.add(new JLabel("Accuracy:"));
		accVal = new JTextField();
		accVal.setPreferredSize(new Dimension(80,20));
		accPane.add(accVal);
		
		JPanel methodPane = new JPanel();
		llf = new JRadioButton("Relative Log-Likelihood");
	//	para = new JRadioButton("Parameter Difference");
		fixed = new JRadioButton("Fixed Iteration");
		methodPane.add(llf);
	//	methodPane.add(para);
		methodPane.add(fixed);
		ButtonGroup group = new ButtonGroup();
		group.add(llf);
	//	group.add(para);
		group.add(fixed);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(estGraph);
//		rightPanel.add(methodPane);
//		rightPanel.add(accPane);
//		rightPanel.add(maxItePane);
		rightPanel.setMaximumSize(new Dimension(410,450));

		add(Box.createGlue());
		leftPanel.setAlignmentY(0.0f);
		add(leftPanel);
		rightPanel.setAlignmentY(0.0f);
		add(rightPanel);
		add(Box.createGlue());
		
		flagDataSet = false;
	}

	public void setCommonSRM(CommonSRM srm) {
		this.srm = srm;
		update();
	}

	public void update() {
		if (flagDataSet) {
			params.setText(srm.getParameterString());
//			estResult.setText(srm.getResultString() + ln);
//			boolean test90 = KolmogorovSmirnov.isKSTest90(srm.getNHPPModel(), dat);	
//			boolean test95 = KolmogorovSmirnov.isKSTest95(srm.getNHPPModel(), dat);
//			estResult.append("KStest90: " + test90 + ", KStest95: " + test95 + ln);
//			estResult.append("LLF: " + InformationCriteria.getLogLikelihood(srm.getNHPPModel(), dat) + ln);
//			estResult.append("AIC: " + InformationCriteria.getAIC(srm.getNHPPModel(), dat) + ln);
//			estResult.append("BIC: " + InformationCriteria.getBIC(srm.getNHPPModel(), dat) + ln);
			estResult.setText(srm.getEMControl().getResultString());
			String str = "Log-likelihood = " + srm.getLogLikelihood() + ln
					+ "Degrees of freedom = " + Integer.toString(srm.getNumberOfParameters()) + ln
					+ "AIC = " + srm.getAIC() + ln
					+ "BIC = " + srm.getBIC() + ln
					+ "KStest90 = " + Boolean.toString(KolmogorovSmirnovTest.
					isKSTest90(srm.getMeanValueFunction(dat.getTimeArray()), dat)) + ln
					+ "KStest95 = " + Boolean.toString(KolmogorovSmirnovTest.
					isKSTest95(srm.getMeanValueFunction(dat.getTimeArray()), dat)) + ln;
			estResult.append(str);
			estGraph.drawGraphs(this.srm);
//			maxIteVal.setText(Integer.toString(srm.getMaxIteration()));
//			accVal.setText(Double.toString(srm.getEps()));
/*
			int tmp = srm.getMethod();
			if (tmp == 0) {
				llf.setSelected(true);
			} else if (tmp == 1) {
				para.setSelected(true);
			} else if (tmp == 2) {
				fixed.setSelected(true);
			}
*/
		}
	}

	public void setGroupData(GroupData dat, String label) {
		this.dat = dat;
		estGraph.setGroupData(dat, label);
		flagDataSet = true;
	}

	public void actionPerformed(ActionEvent e){
		String ac = e.getActionCommand();
		if (ac.equals("Fitting") && flagDataSet) {
//			srm.start(dat);
//			logbuf.append("Start fitting " + srm.getNHPPModel().getModelString() + " ...");
//			srm.emwait();
			logbuf.appendln(" done.");
			update();
		} else if (ac.equals("Apply")) {
//			setParameters();
//			logbuf.appendln("Parameter changed: " + srm.getNHPPModel().getModelString());
		}
	}
}

