package phsrm.original;

import phsrm.common.*;

public class EMGammaSRM extends EMCommonSRM {
	
	GammaSRM srm;
	double omega;
	double shape;
	double rate;
	int dsize;

	//work
	double en1;
	double en2;
	double en3;

	public EMGammaSRM(GammaSRM srm) {
		this.srm = srm;
	}

	public void doEstimation() {
		omega = srm.getTotal();
		rate = srm.getRate();
		shape = srm.getShape();
		dsize = fdat.getNumberOfRecords();
		em.runEMRunnableModel(this);
	}

	public String getParameterString() {
		return srm.getParameterString();
	}

	class logIntergate extends NIntegrate {
		public double func(double x) {
			double tmp = Math.log(x) * srm.getPDF(x);
			return tmp;
		}
	}
	
	logIntergate lif = new logIntergate();
	
	public double doEMstep() {
		double x0, t0;
		double x1, t1;
		double tmp1, tmp2, tmp3, tmp4, llf;
		double a0, a1;
		double gam10, gam11, gam20, gam21;

		// E-step
		t0 = fdat.getTime(1);
		x0 = fdat.getNumber(1);
		a0 = Numlib.loggamma(shape);
		a1 = Numlib.loggamma(shape + 1.0);
		en1 = 0.0;
		en2 = 0.0;
		en3 = 0.0;
		llf = 0.0;
		gam10 = Numlib.q_gamma(shape, rate*t0, a0);
		gam11 = Numlib.q_gamma(shape+1, rate*t0, a1);
		tmp3 = lif.solve(1.0e-10, t0);
		tmp4 = tmp3;
		if (x0 != 0.0) {
			tmp1 = 1.0 - gam10;
			tmp2 = shape / rate * (1.0 - gam11);
			en1 += x0;
			en2 += x0 * tmp2 / tmp1;
			en3 += x0 * tmp3 / tmp1;
			llf += x0 * Math.log(tmp1) - Numlib.loggamma(x0+1);
		}
		if (fdat.getType(1) == 1) {
			en1 += 1.0;
			en2 += t0;
			en3 += Math.log(t0);
			llf += shape*Math.log(rate) + (shape-1)*Math.log(t0) - rate*t0 
					- Numlib.loggamma(shape);
		}
		for (int j=2; j<=fdat.getNumberOfRecords(); j++) {
			t1 = t0 + fdat.getTime(j);
			x1 = fdat.getNumber(j);
			gam20 = Numlib.q_gamma(shape, rate*t1, a0);
			gam21 = Numlib.q_gamma(shape+1, rate*t1, a1);
			tmp3 = lif.solve(t0, t1);
			tmp4 += tmp3;
			if (x1 != 0.0) {
				tmp1 = gam10 - gam20;
				tmp2 = (shape/rate) * (gam11 - gam21);
				en1 += x1;
				en2 += x1 * tmp2 / tmp1;
				en3 += x1 * tmp3 / tmp1;
				llf += x1 * Math.log(tmp1) - Numlib.loggamma(x1+1);
			}
			if (fdat.getType(j) == 1) {
				en1 += 1.0;
				en2 += t1;
				en3 += Math.log(t1);
				llf += shape*Math.log(rate) + (shape-1)*Math.log(t1) - rate*t1
					- Numlib.loggamma(shape);
			}
			gam10 = gam20;
			gam11 = gam21;
			t0 = t1;
		}
		llf += Math.log(omega) * en1;  // en1 is total number of faults
		en1 += omega * gam10;  // gam10 is the last time
		en2 += omega * (shape/rate) * gam11;  // gam11 is the last time
		en3 += omega * (Numlib.psi(shape) - Math.log(rate) - tmp4);
		llf += - omega * (1.0 - gam10);
		
		// M-step
		omega =  en1;
		shape = findshape2(shape, Math.log(en2/en1)-en3/en1);
		rate = shape * en1 / en2;
		
		srm.setOmega(omega);
		srm.setShape(shape);
		srm.setRate(rate);
		
		return llf;
	}
	
	static double eps = 1.0e-8;
	static double tolz = 1.0e-5;
	static int maxcnt = 1024;
	
	static double findshape2(double init, double v) {
		int cnt;
		double a, b, c;
		a = init/2;
		b = init;
		cnt = 0;
		while (Math.log(b) - Numlib.psi(b) > v 
				&& cnt++ < maxcnt) {
			a = b;
			b *= 2.0;
		}
		while (Math.log(a) - Numlib.psi(a) <= v 
				&& cnt++ < maxcnt) {
			b = a;
			a = a/2.0;
		}
		cnt = 0;
		c = (a+b)/2;
		while (Math.abs(a - b)/a > eps
				&& cnt++ < maxcnt) {
			c = (a+b)/2;
			if (Math.log(c) - Numlib.psi(c) < v) {
				b = c;
			} else {
				a = c;
			}
		}
		return c;
	}
}
