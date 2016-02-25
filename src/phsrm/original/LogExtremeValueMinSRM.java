package phsrm.original;

import phsrm.common.*;

public class LogExtremeValueMinSRM extends CommonSRM {
	double scale;
	double shape;
	
	// constructor
	public LogExtremeValueMinSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMLogExtremeValueMinSRM(this);
	}

	public String getModelString() {
		return "Log Extreme-Value Min SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		double y = Math.exp((Math.log(t)+scale)/shape);
		return y*Math.exp(-y)/shape/t;
	}
	
	public double getCDF(double t) {
		double y = Math.exp((Math.log(t)+scale)/shape);
		return 1.0 - Math.exp(-y);
	}
	
	public String getParameterString() {
		String str = "omega: " + omega + SRMText.ln
		+ "Shape: " + shape + SRMText.ln
		+ "Scale: " + scale + SRMText.ln;
		return str;
	}
	
	public void setInitialParameters(GroupData fdat) {
		omega = 1.0;
		scale = 0.0; //fdat.getTime(fdat.getNumberOfRecords()/2+1);
		shape = Math.log(fdat.getTotalTime());
	}
}
