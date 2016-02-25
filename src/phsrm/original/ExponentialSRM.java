package phsrm.original;

import phsrm.common.*;

final public class ExponentialSRM extends CommonSRM {
	double rate;
	
	// constructor
	public ExponentialSRM() {
		rate = 1.0;
		emsrm = new EMExponentialSRM(this);
	}

	public int getNumberOfParameters() {
		return 2;
	}
	
	public String getModelString() {
		return "Exponential SRM";
	}

	public void setRate(double r) { rate = r; }
	public double getRate() { return rate; }

	public double getPDF(double t) {
		return rate * Math.exp(-rate * t);
	}
	
	public double getCDF(double t) {
		return 1.0 - Math.exp(-rate * t);
	}
	
	public String getParameterString() {
		String str = "omega: " + omega + SRMText.ln
			+ "Rate: " + rate + SRMText.ln;
		return str;
	}
	
	public void setInitialParameters(GroupData fdat) {
		omega = 1.0;
		rate = 1.0/fdat.getTotalTime();
	}
}
