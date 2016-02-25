package phsrm.original;

import phsrm.common.*;

public class LogLogisticSRM extends CommonSRM {
	double scale;
	double shape;
	
	// constructor
	public LogLogisticSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMLogLogisticSRM(this);
	}

	public String getModelString() {
		return "Log Logistic SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		double y;
		y = Math.exp((Math.log(t)-scale)/shape);
		return y/(t*shape*(1+y)*(1+y));
	}
	
	public double getCDF(double t) {
		double y;
		y = Math.exp((Math.log(t)-scale)/shape);
		return y/(1+y);
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
