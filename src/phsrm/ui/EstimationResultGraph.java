package phsrm.ui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jfree.chart.ChartPanel;

import phsrm.common.*;
import phsrm.cphsrm.*;

public class EstimationResultGraph extends JPanel {
	JTable table;	
    LogPanel logbuf;
 
	LineChart lcmvf; // MVF
	LineChart lcrel; // reliability
	LineChart lcfar; // failure rate

	// graph parameters
    static int nos = 100;
    class Range {
    	public double start;
    	public double end;
    	
    	Range() {}
    	Range(double s, double e) { setRange(s,e); }
    	public void setRange(double s, double e) {
    		start = s;
    		end = e;
    	}
    }

    Range mvfRange;
    Range relRange;
    Range farRange;
    
	EstimationResultGraph(LogPanel logbuf){
		this.logbuf = logbuf;
		initPanel();
	}
	
	private void initPanel() {
		// panel config
		mvfRange = new Range(0.0, 0.0);
		relRange = new Range(0.0, 0.0);
		farRange = new Range(0.0, 0.0);		

		JTabbedPane graphTab = new JTabbedPane();

		lcmvf = new LineChart(2);
        ChartPanel mvfChartPanel = lcmvf.createChart("", "time", "# of faults");
        mvfChartPanel.setPreferredSize(new Dimension(500,300));
        graphTab.addTab("Mean Value", mvfChartPanel);

        lcrel = new LineChart(1);
        ChartPanel relChartPanel = lcrel.createChart("", "time", "SW Reliability");
        relChartPanel.setPreferredSize(new Dimension(500,300));
        graphTab.addTab("Reliability", relChartPanel);

        lcfar = new LineChart(1);
        ChartPanel farChartPanel = lcfar.createChart("", "time", "Failure Rate");
        farChartPanel.setPreferredSize(new Dimension(500,300));
        graphTab.addTab("Failure Rate", farChartPanel);

        graphTab.setPreferredSize(new Dimension(500,300));
        add(graphTab);
}
	
	public void setTable(JTable table) {
		this.table = table;
	}

	public void setGroupData(GroupData dat, String label) {
		lcmvf.setData(0, dat.getMeanValueFunction(), label);
		lcmvf.clearData(1);  // clear old mvf
		lcrel.clearData(0);
		lcfar.clearData(0);
		mvfRange.setRange(0.0, dat.getTotalTime() * 1.5);
		relRange.setRange(dat.getTotalTime(), dat.getTotalTime() * 1.5);
		farRange.setRange(0.0, dat.getTotalTime() * 1.5);
	}

	public void drawGraphs(NHPPSoftwareReliabilityModels srm) {
		drawMVF(srm);
		drawReliability(srm);
		drawFailureRate(srm);
	}

	public void drawMVF(NHPPSoftwareReliabilityModels srm) {
		double t = mvfRange.start;
		double h = (mvfRange.end - mvfRange.start) / nos;
		double[] time = new double [nos+1];
		for (int i=0; i<=100; i++) {
			time[i] = h;
		}
		lcmvf.setData(1, srm.getMeanValueFunction(time), srm.getModelString());
	}
	
	public void drawReliability(NHPPSoftwareReliabilityModels srm) {
		double s = relRange.start;
		double t = relRange.start;
		double h = (relRange.end - relRange.start) / nos;
		double[] time = new double [nos+1];
		for (int i=0; i<=100; i++) {
			time[i] = h;
		}
		lcrel.setData(0, srm.getReliabilityFunction(s, time), srm.getModelString());
	}

	public void drawFailureRate(NHPPSoftwareReliabilityModels srm) {
		double t = farRange.start;
		double h = (farRange.end - farRange.start) / nos;
		double[] time = new double [nos+1];
		t+=h;
		for (int i=0; i<=100; i++) {
			time[i] = h;
		}
		lcfar.setData(0, srm.getFailureRate(time), srm.getModelString());
	}
}
