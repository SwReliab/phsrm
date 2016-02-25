package phsrm.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import phsrm.common.*;
import phsrm.cphsrm.*;

public class EstimationResult extends JPanel implements ListSelectionListener, MouseListener {
	SRMControl srmCtrl;
	NHPPSoftwareReliabilityModels[] srms;
	GroupData dat;

	JTable table;
	DefaultTableModel tableModel;
	ListSelectionModel list;
	JTableHeader thead;

	EstimationResultGraph estGraph;

	LogPanel logbuf;

	private String[] columnNames = {
			"Model", "LLF", "DF", "AIC", "BIC",
			"KS (90%)", "KS (95%)", 
			"Status"};
	
	// constructors
	EstimationResult(LogPanel logbuf) {
		// panel configuration
		this.logbuf = logbuf;
		estGraph = new EstimationResultGraph(logbuf);
		tableModel = new DefaultTableModel(columnNames, 0);
		table = new JTable(tableModel);
		list = table.getSelectionModel();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		thead = table.getTableHeader();
		thead.addMouseListener(this);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(500,300));
		add(sp, BorderLayout.CENTER);
		estGraph.setTable(table);
		add(estGraph);
	}
	
	public void setSRMs(NHPPSoftwareReliabilityModels[] srms) {
		this.srms = srms;
	}

	public void setGroupData(GroupData dat, String label) {
		this.dat = dat;
		estGraph.setGroupData(dat, label);
		list.removeListSelectionListener(this);
		table.setModel(new DefaultTableModel(columnNames, 0));
		list.addListSelectionListener(this);
	}
	
	public void setEstimationResult() {
//		srmCtrl.sortByCriterion("AIC");
		sortByCriterion("AIC");
		updateEstimationResults();
//		estGraph.drawGraphs(srmCtrl.getSortedSRMs(0));
		estGraph.drawGraphs(srms[0]);
	}

	public void valueChanged(ListSelectionEvent e) {
//		estGraph.drawGraphs(srmCtrl.getSortedSRMs(table.getSelectedRow()));
		estGraph.drawGraphs(srms[table.getSelectedRow()]);
	}

	public void updateEstimationResults() {
		list.removeListSelectionListener(this);
		tableModel = new DefaultTableModel(columnNames, 0);
		DecimalFormat df1 = new DecimalFormat("0.0");
		DecimalFormat df2 = new DecimalFormat("0.000E00");
		for (int i=0; i<srms.length; i++) {
			String[] rec = new String [8];
			rec[0] = srms[i].getModelString();
			rec[1] = df1.format(srms[i].getLogLikelihood());
			rec[2] = Integer.toString(srms[i].getNumberOfParameters());
			rec[3] = df1.format(srms[i].getAIC());
			rec[4] = df1.format(srms[i].getBIC());
			rec[5] = Boolean.toString(KolmogorovSmirnovTest.
					isKSTest90(srms[i].getMeanValueFunction(dat.getTimeArray()), dat));
			rec[6] = Boolean.toString(KolmogorovSmirnovTest.
					isKSTest95(srms[i].getMeanValueFunction(dat.getTimeArray()), dat));
//			rec[6] = df1.format(srms[i].getTotal());
			rec[7] = srms[i].getEMControl().getStatus();
			tableModel.addRow(rec);
		}
		table.setModel(tableModel);
		list.addListSelectionListener(this);
	}
	
	public void mouseClicked(MouseEvent e) {
		int idx = thead.columnAtPoint(e.getPoint());
		if (idx == 1) {
//			srmCtrl.sortByCriterion("LLF");
			sortByCriterion("LLF");
		} else if (idx == 2) {
//			srmCtrl.sortByCriterion("AIC");
			sortByCriterion("AIC");
		} else if (idx == 3) {
//			srmCtrl.sortByCriterion("BIC");
			sortByCriterion("BIC");
		}
		updateEstimationResults();
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void sortByCriterion(String key) {
		class LLFcomp implements Comparator {
			public int compare(Object o1, Object o2){
				double tmp1 = ((NHPPSoftwareReliabilityModels) o1).getLogLikelihood();
				double tmp2 = ((NHPPSoftwareReliabilityModels) o2).getLogLikelihood();
				if (tmp1 < tmp2) {
					return 1;
				} else {
					return -1;
				}
			}
		}

		class AICcomp implements Comparator {
			public int compare(Object o1, Object o2){
				double tmp1 = ((NHPPSoftwareReliabilityModels) o1).getAIC();
				double tmp2 = ((NHPPSoftwareReliabilityModels) o2).getAIC();
				if (tmp1 > tmp2) {
					return 1;
				} else {
					return -1;
				}
			}
		}

		class BICcomp implements Comparator {
			public int compare(Object o1, Object o2){
				double tmp1 = ((NHPPSoftwareReliabilityModels) o1).getBIC();
				double tmp2 = ((NHPPSoftwareReliabilityModels) o2).getBIC();
				if (tmp1 > tmp2) {
					return 1;
				} else {
					return -1;
				}
			}
		}

		if (key.equals("AIC")) {
			Arrays.sort(srms, new AICcomp());
			logbuf.appendln("Reordering results by AIC.");
		} else if (key.equals("LLF")) {
			Arrays.sort(srms, new LLFcomp());
			logbuf.appendln("Reordering results by LLF.");
		} else if (key.equals("BIC")) {
			Arrays.sort(srms, new BICcomp());
			logbuf.appendln("Reordering results by BIC.");
		}
	}
}
