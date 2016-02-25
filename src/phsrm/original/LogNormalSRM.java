package phsrm.original;

import phsrm.common.*;

public class LogNormalSRM extends CommonSRM {
	double scale;
	double shape;	boolean fixed_shape;
	
	// constructor
	public LogNormalSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMLogNormalSRM(this);
	}

	public String getModelString() {
		return "Log Normal SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		return Numlib.d_normal((Math.log(t)-scale)/shape)/(t*shape);
	}
	
	public double getCDF(double t) {
		return Numlib.p_normal((Math.log(t)-scale)/shape);
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

