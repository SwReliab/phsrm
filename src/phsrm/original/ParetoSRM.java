package phsrm.original;

import phsrm.common.*;

public class ParetoSRM extends CommonSRM {
	double scale;
	double shape;
	
	// constructor
	public ParetoSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMParetoSRM(this);
	}

	public String getModelString() {
		return "Pareto SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		return shape/scale * Math.pow(scale/(scale+t), shape+1.0);
	}
	
	public double getCDF(double t) {
		return 1.0 - Math.pow(scale/(scale+t), shape);
	}
	
	public String getParameterString() {
		String str = "omega: " + omega + SRMText.ln
		+ "Shape: " + shape + SRMText.ln
		+ "Scale: " + scale + SRMText.ln;
		return str;
	}
	
	public void setInitialParameters(GroupData fdat) {
		omega = 1.0;
		scale = 1.0; //fdat.getTime(fdat.getNumberOfRecords()/2+1);
		shape = 1.0;
	}
}
