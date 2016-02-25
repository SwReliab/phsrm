package phsrm.original;

import phsrm.common.*;

public class TruncatedExtremeValueMinSRM extends CommonSRM {
	double scale;
	double shape;
	
	// constructor
	public TruncatedExtremeValueMinSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMTruncatedExtremeValueMinSRM(this);
	}

	public String getModelString() {
		return "Truncated Extreme-Value Min SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		double y = Math.exp((t+scale)/shape);
		double y0 = Math.exp(scale/shape);
		return y*Math.exp(-y)/shape/Math.exp(-y0);
	}
	
	public double getCDF(double t) {
		double y = Math.exp((t+scale)/shape);
		double y0 = Math.exp(scale/shape);
		return 1.0-Math.exp(-y)/Math.exp(-y0);
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
		shape = fdat.getTotalTime() / 3;
	}

}
