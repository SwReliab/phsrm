package phsrm.original;

import phsrm.common.*;

final public class GammaSRM extends CommonSRM {
	double shape;
	double rate;
	
	// constructor
	public GammaSRM() {
		rate = 1.0;
		shape = 1.0;
		emsrm = new EMGammaSRM(this);
	}

	public int getNumberOfParameters() {
		return 3;
	}
	
	public String getModelString() {
		return "Gamma SRM";
	}

	public void setRate(double r) { rate = r; }
	public double getRate() { return rate; }

	public void setShape(double s){ shape = s; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		double y;
		y = rate * t;
		return rate * Math.pow(y, shape-1) * Math.exp(-y) / Numlib.gamma(shape);
	}
	
	public double getCDF(double t) {
		double y;
		y = rate * t;
		return Numlib.p_gamma(shape, y, Numlib.loggamma(shape));
	}
	
	public void setInitialParameters(GroupData fdat) {
		omega = 1.0;
		rate = shape/fdat.getTotalTime();
	}

	public String getParameterString() {
		String str = "omega: " + omega + SRMText.ln
			+ "Shape: " + shape + SRMText.ln
			+ "Rate: " + rate + SRMText.ln;
		return str;
	}
}
