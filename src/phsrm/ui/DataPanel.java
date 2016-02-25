package phsrm.ui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartPanel;

import phsrm.common.GroupData;

public class DataPanel extends JPanel {

	ScatterChart smc;
    DefaultTableModel tableModel;
    JTable table;
    
	private String[] columnNames = {"Time", "Faults", "Observe"};

	DataPanel(LogPanel buf){
		initDataPanel();
	}
	
	DataPanel() {
		initDataPanel();
	}

	private void initDataPanel() {
		// panel config
		
		GroupData dat = new GroupData();

	    tableModel = new DefaultTableModel(columnNames, 0);
	    table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(300, 300));
        add(sp);
        
        smc = new ScatterChart();
        smc.setData(dat.getMeanValueFunction(), "");
        ChartPanel chartPanel = smc.createChart();
        chartPanel.setPreferredSize(new Dimension(500,300));
        add(chartPanel);
	}

	public void setGroupData(GroupData dat, String label) {
		tableModel = new DefaultTableModel(columnNames, 0);
		for (int i=1; i<=dat.getNumberOfRecords(); i++) {
			Double[] rec = new Double [3];
			rec[0] = new Double(dat.getTime(i));
			rec[1] = new Double(dat.getNumber(i));
			rec[2] = new Double(dat.getType(i));
			tableModel.addRow(rec);
		}
		table.setModel(tableModel);
		smc.setData(dat.getMeanValueFunction(), label);
	}
}
