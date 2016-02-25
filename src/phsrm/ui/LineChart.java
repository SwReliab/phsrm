package phsrm.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import phsrm.common.MVFData;

public class LineChart {

	int seriesCount;
	XYSeries[] xySeries;
	
	LineChart(int seriesCount) {
		this.seriesCount = seriesCount;
		xySeries = new XYSeries [seriesCount];
		for (int i=0; i<seriesCount; i++) {
			xySeries[i] = new XYSeries(i);
		}
	}
	
	void setData(int idx, MVFData dat, String label) {
		xySeries[idx].setKey(label);
		xySeries[idx].clear();
		for (int i=0; i<=dat.getSize(); i++) {
			xySeries[idx].add(dat.getX(i), dat.getY(i));
		}
	}
	
	void clearData(int idx) {
		xySeries[idx].clear();
	}

	public ChartPanel createChart(String title, String xaxis, String yaxis) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		for (int i=0; i<seriesCount; i++) {
			xySeriesCollection.addSeries(xySeries[i]);
		}
		JFreeChart xyLineChart =
			ChartFactory.createXYLineChart(title, xaxis, yaxis,
				xySeriesCollection, PlotOrientation.VERTICAL, true, false, false);
				configXYScatterChart(xyLineChart);
		ChartPanel chartPanel = new ChartPanel(xyLineChart);
		return chartPanel;
	}
	
	public void configXYScatterChart(JFreeChart xyLineChart) {
		XYPlot xyPlot = xyLineChart.getXYPlot();
		xyPlot.setDomainCrosshairVisible(true); // horizontal line at cursor point
		xyPlot.setRangeCrosshairVisible(true);  // vertical line at cursor point
		/* x-axis configuration */
		NumberAxis xAxis = (NumberAxis)xyPlot.getDomainAxis();
		xAxis.setAutoRange(true);
		/* y-axis configuration */
		NumberAxis yAxis = (NumberAxis)xyPlot.getRangeAxis();
		yAxis.setAutoRange(true);
	}
}
