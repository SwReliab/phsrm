package phsrm.original;

import phsrm.common.*;

public class TruncatedNormalSRM extends CommonSRM {
	double scale;
	double shape;
	
	// constructor
	public TruncatedNormalSRM() {
		scale = 0.0;
		shape = 1.0;
		emsrm = new EMTruncatedNormalSRM(this);
	}

	public String getModelString() {
		return "Truncated Normal SRM";
	}

	public void setScale(double r) { scale = r; }	
	public void setShape(double r) { shape = r; }

	public int getNumberOfParameters() {
		return 3;
	}

	public double getScale() { return scale; }
	public double getShape() { return shape; }

	public double getPDF(double t) {
		return Numlib.d_normal((t-scale)/shape)/ Numlib.q_normal(-scale/shape)/shape;
	}
	
	public double getCDF(double t) {
		return 1.0 - Numlib.q_normal((t-scale)/shape)/Numlib.q_normal(-scale/shape);
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
