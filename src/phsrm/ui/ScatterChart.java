package phsrm.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import phsrm.common.*;

public class ScatterChart {

	XYSeries xySeries;
		
	ScatterChart() {
		xySeries = new XYSeries("fault data");
		xySeries.add(0, 0);
	}
	
	void setData(MVFData dat, String label) {
		xySeries.setKey(label);
		xySeries.clear();
		xySeries.add(0, 0);
		for (int i=1; i<=dat.getSize(); i++) {
			xySeries.add(dat.getX(i), dat.getY(i));
		}
	}
	
	public ChartPanel createChart() {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(xySeries);
		JFreeChart xyScatterChart =
			ChartFactory.createScatterPlot (
					"Number of Faults", "time", "# of faults", 
					xySeriesCollection, PlotOrientation.VERTICAL,
					true, true, true);
		configXYScatterChart(xyScatterChart);
		ChartPanel chartPanel = new ChartPanel(xyScatterChart);
		return chartPanel;
	}
	
	public void configXYScatterChart(JFreeChart xyScatterChart) {
		XYPlot xyPlot = xyScatterChart.getXYPlot();
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
