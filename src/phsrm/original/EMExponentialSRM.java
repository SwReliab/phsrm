package phsrm.original;

import phsrm.common.*;

final public class EMExponentialSRM extends EMCommonSRM {
	
	ExponentialSRM expsrm;
	double omega;
	double rate;
	int dsize;

	//work
	double en1;
	double en2;

	public EMExponentialSRM(ExponentialSRM expsrm) {
		this.expsrm = expsrm;
	}

	public void doEstimation() {
		omega = expsrm.getTotal();
		rate = expsrm.getRate();
		dsize = fdat.getNumberOfRecords();
		em.runEMRunnableModel(this);
	}

	public String getParameterString() {
		return expsrm.getParameterString();
	}

	public double doEMstep() {
		double x0, t0;
		double x1, t1;
		double tmp1, tmp2, llf;

		// E-step
		t0 = fdat.getTime(1);
		x0 = fdat.getNumber(1);
		en1 = 0.0;
		en2 = 0.0;
		llf = 0.0;
		if (x0 != 0.0) {
			tmp1 = 1 - Math.exp(-rate*t0);
			tmp2 = 1.0/rate - (t0 + 1/rate) * Math.exp(-rate*t0);
			en1 = x0;
			en2 = x0 * tmp2 / tmp1;
			llf = x0 * Math.log(tmp1) - Numlib.loggamma(x0+1);
		}
		if (fdat.getType(1) == 1) {
			en1 += 1.0;
			en2 += t0;
			llf += Math.log(rate) - rate*t0;
		}
		for (int j=2; j<=fdat.getNumberOfRecords(); j++) {
			t1 = t0 + fdat.getTime(j);
			x1 = fdat.getNumber(j);
			if (x1 != 0.0) {
				tmp1 = Math.exp(-rate*t0) - Math.exp(-rate*t1);
				tmp2 = (t0 + 1.0/rate) * Math.exp(-rate*t0) - (t1 + 1.0/rate) * Math.exp(-rate*t1);
				en1 += x1;
				en2 += x1 * tmp2 / tmp1;
				llf += x1 * Math.log(tmp1) - Numlib.loggamma(x1+1);
			}
			if (fdat.getType(j) == 1) {
				en1 += 1.0;
				en2 += t1;
				llf += Math.log(rate) - rate*t1;
			}
			t0 = t1;
			x0 = x1;
		}
		llf += Math.log(omega) * en1;  // en1 is total number of faults
		en1 += omega * Math.exp(-rate*t0);  // t0 is the last time
		en2 += omega * (t0 + 1/rate) * Math.exp(-rate*t0); // t0 is the last time
		llf += - omega * (1.0 - Math.exp(-rate*t0));
		
		// M-step
		omega =  en1;
		rate = en1 / en2;
		expsrm.setOmega(omega);
		expsrm.setRate(rate);

		return llf;
	}
}
