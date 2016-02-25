package phsrm.original;

import phsrm.common.*;

public class TruncatedLogisticSRM extends CommonSRM {
	double scale;
	double shape;
	
	// constructor
	public TruncatedLogisticSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMTruncatedLogisticSRM(this);
	}

	public String getModelString() {
		return "Truncated Logistic SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		double y = Math.exp(-(t-scale)/shape);
		return (Math.exp(-t/shape)+y)/(shape*(1.0+y)*(1.0+y));
	}
	
	public double getCDF(double t) {
		return (1.0-Math.exp(-t/shape))/(1.0+Math.exp(-(t-scale)/shape));
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
		shape = fdat.getTotalTime();
	}
}
