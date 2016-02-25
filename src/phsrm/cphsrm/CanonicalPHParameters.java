package phsrm.cphsrm;

import phsrm.common.SRMText;

public class CanonicalPHParameters {

	int ndim;
	double[] rate;
	double[] pi;
	
	CanonicalPHParameters() {
		setPhaseSize(2);
	}
	
	CanonicalPHParameters(int ndim) {
		setPhaseSize(ndim);
	}
	
	void setPhaseSize(int ndim) {
		this.ndim = ndim;
		rate = new double [ndim];
		pi = new double [ndim];
	}
	
	public int getPhaseSize() {
		return ndim;
	}
	
	public void setRateParameter(int i, double val) {
		rate[i] = val;
	}
	
	public void setRateParameter(double[] rate) {
		this.rate = rate;
	}
	
	public double[] getRateParameter() {
		return rate;
	}
	
	public void setInitialVector(int i, double val) {
		pi[i] = val;
	}
	
	public void setInitialVector(double[] pi) {
		this.pi = pi;
	}
	
	public double[] getInitialVector() {
		return pi;
	}
	
	public String getParameterString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Init:");
		for (int i=0; i<ndim; i++) {
			if ((i % 5) == 0 && i != 0) {
				buf.append(SRMText.ln + "      ");
			} else {
				buf.append(" ");
			}
			buf.append(SRMText.decimalE8.format(pi[i]));
		}
		buf.append(SRMText.ln + "Rate:");
		for (int i=0; i<ndim; i++) {
			if ((i % 5) == 0 && i != 0) {
				buf.append(SRMText.ln + "      ");
			} else {
				buf.append(" ");
			}
			buf.append(SRMText.decimalE8.format(rate[i]));
		}
		buf.append(SRMText.ln);
		return buf.toString();
	}
	
	public void transformCanonicalForm() {
		int j;
		for (int i=0; i<ndim-1; i++) {
			if (rate[i] > rate[i+1]) {
				swap(i, i+1);
				j = i;
				while (j>0 && rate[j-1] > rate[j]) {
					swap(j-1, j);
					j--;
				}
			}
		}
	}
	
	void swap(int i, int j) {
		double w, tmp;
		w = rate[j] / rate[i];
		pi[i] += (1.0 -w) * pi[j];
		pi[j] *= w;
		tmp = rate[j];
		rate[j] = rate[i];
		rate[i] = tmp;
	}
	
	void setPowerStructuredParameter(double scale, double shape) {
		double[] vec = new double [ndim];
		double[] lambda = new double [ndim];
		double tmp, total, base;
		double p = Math.exp(1.0/(ndim-1.0)*Math.log(shape));
		total = tmp = 1.0;
		for (int i=1; i<ndim; i++) {
			tmp *= (double) (i+1.0) / (i * p);
			total += tmp;
		}
		base = total / (ndim * scale);
		tmp = base;
		for (int i=0; i<ndim; i++) {
			vec[i] = 1.0/ndim;
			lambda[i] = tmp;
			tmp *= p;
		}
		setInitialVector(vec);
		setRateParameter(lambda);
	}
	
	void setLinearStructuredParameter(double scale, double shape) {
		double[] vec = new double [ndim];
		double[] lambda = new double [ndim];
		double total, base;
		double al = (shape-1.0)/(ndim-1.0);
		total = 1.0;
		for (int i=1; i<ndim; i++) {
			total += (i+1) / (al * i + 1.0);
		}
		base = total / (ndim * scale);
		for (int i=0; i<ndim; i++) {
			vec[i] = 1.0/ndim;
			lambda[i] = base * (al * i + 1.0);
		}
		setInitialVector(vec);
		setRateParameter(lambda);
	}
}
