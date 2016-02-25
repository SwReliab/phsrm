package phsrm.hersrm;

import phsrm.common.Numlib;
import phsrm.common.SRMText;

public class HyperErlangParameters {

	int numberOfErs;
	double[] pi;
	double[] rate;
	double[] shape;
	
	public HyperErlangParameters() {
		setNumberOfErlangDists(2);
	}
		
	public HyperErlangParameters(int m) {
		setNumberOfErlangDists(m);
	}
	
	public void setNumberOfErlangDists(int m) {
		numberOfErs = m;
		pi = new double [numberOfErs];
		rate = new double [numberOfErs];
		shape = new double [numberOfErs];
	}
	
	public int getNumberOfErlangDists() {
		return numberOfErs;
	}
	
	public void setShapeParameters(int[] vec) {
		for (int i=0; i<numberOfErs; i++) {
			shape[i] = vec[i];
		}
	}
	
	public void setShapeParameters(int i, double r) {
		shape[i] = r;
	}
	
	public double[] getShapeParameters() {
		return shape;
	}
	
	public void setRateParameters(double[] vec) {
		System.arraycopy(vec, 0, rate, 0, numberOfErs);
	}
	
	public void setRateParameters(int i, double r) {
		rate[i] = r;
	}
	
	public double[] getRateParameters() {
		return rate;
	}

	public void setInitalVector(double[] init) {
		System.arraycopy(init, 0, pi, 0, numberOfErs);
	}
	
	public void setInitalVector(int i, double init) {
		pi[i] = init;
	}
	
	public double[] getInitialVector() {
		return pi;
	}

	// pdf, cdf
	public double getPDF(int i, double t) {
		double y = rate[i] * t;
		return rate[i] * Math.pow(y, shape[i]-1) * Math.exp(-y) / Numlib.gamma(shape[i]);
	}
	
	public double getPDF(double t) {
		double sum = 0.0;
		for (int i=0; i<numberOfErs; i++) {
			sum += pi[i] * getPDF(i, t);
		}
		return sum;
	}

	public double getCDF(int i, double t) {
		double y = rate[i] * t;
		return Numlib.p_gamma(shape[i], y, Numlib.loggamma(shape[i]));
	}
	
	public double getCDF(double t) {
		double sum = 0.0;
		for (int i=0; i<numberOfErs; i++) {
			sum += pi[i] * getCDF(i, t);
		}
		return sum;
	}
	
	public String getParameterString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Init :");
		for (int i=0; i<numberOfErs; i++) {
			if ((i % 5) == 0 && i != 0) {
				buf.append(SRMText.ln + "       ");
			} else {
				buf.append(" ");
			}
			buf.append(SRMText.decimalE8.format(pi[i]));
		}
		buf.append(SRMText.ln + "Shape:");
		for (int i=0; i<numberOfErs; i++) {
			if ((i % 5) == 0 && i != 0) {
				buf.append(SRMText.ln + "       ");
			} else {
				buf.append(" ");
			}
//			buf.append(SRMText.decimalE8.format((int) shape[i]));
			buf.append((int) shape[i]);
		}
		buf.append(SRMText.ln + "Rate :");
		for (int i=0; i<numberOfErs; i++) {
			if ((i % 5) == 0 && i != 0) {
				buf.append(SRMText.ln + "       ");
			} else {
				buf.append(" ");
			}
			buf.append(SRMText.decimalE8.format(rate[i]));
		}
		buf.append(SRMText.ln);
		return buf.toString();
	}
}
