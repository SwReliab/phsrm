package phsrm.original;

import phsrm.common.*;

public class EMParetoSRM extends EMCommonSRM {

	ParetoSRM srm;
	double omega;
	double shape;
	double scale;
	int dsize;

	//work
	double en1;
	double en2;
	double en3;

	public EMParetoSRM(ParetoSRM srm) {
		this.srm = srm;
	}

	public void doEstimation() {
		omega = srm.getTotal();
		scale = srm.getScale();
		shape = srm.getShape();
		dsize = fdat.getNumberOfRecords();
		em.runEMRunnableModel(this);
	}

	public String getParameterString() {
		return srm.getParameterString();
	}

	public double doEMstep() {
		double x, t;
//		double y;
		double tmp1, tmp2, tmp3;
		double llf;
		double g00, g01, g02;
		double g10, g11, g12;
		double en1, en2, en3;
		
		// E-step
		t = fdat.getTime(1);
		x = fdat.getNumber(1);
//		y = Math.exp((Math.log(t)-scale)/shape);
		en1 = 0.0;
		en2 = 0.0;
		en3 = 0.0;
		llf = 0.0;
		g00 = 1.0 - srm.getCDF(t);
		g01 = shape / (scale + t) * g00;
		g02 = (Numlib.psi(shape) - Math.log(scale + t)) * g00;
		if (x != 0.0) {
			tmp1 = 1.0 - g00;
			tmp2 = shape/scale - g01;
			tmp3 = Numlib.psi(shape) - Math.log(scale) - g02;
			en1 += x;
			en2 += x * tmp2 / tmp1;
			en3 += x * tmp3 / tmp1;
			llf += x * Math.log(tmp1) - Numlib.loggamma(x+1);
		}
		if (fdat.getType(1) == 1) {
			en1 += 1.0;
			en2 += (shape+1.0)/(scale+t);
			en3 += Numlib.psi(shape+1.0) - Math.log(scale + t);
			llf += Math.log(srm.getPDF(t));
		}
		for (int j=2; j<=fdat.getNumberOfRecords(); j++) {
			t += fdat.getTime(j);
			x = fdat.getNumber(j);
//			y = Math.exp((Math.log(t)-scale)/shape);
			g10 = 1.0 - srm.getCDF(t);
			g11 = shape / (scale + t) * g10;
			g12 = (Numlib.psi(shape) - Math.log(scale + t)) * g10;
			if (x != 0.0) {
				tmp1 = g00 - g10;
				tmp2 = g01 - g11;
				tmp3 = g02 - g12;
				en1 += x;
				en2 += x * tmp2 / tmp1;
				en3 += x * tmp3 / tmp1;
				llf += x * Math.log(tmp1) - Numlib.loggamma(x+1);
			}
			if (fdat.getType(j) == 1) {
				en1 += 1.0;
				en2 += (shape+1.0)/(scale+t);
				en3 += Numlib.psi(shape+1.0) - Math.log(scale + t);
				llf += Math.log(srm.getPDF(t));
			}
			g00 = g10;
			g01 = g11;
			g02 = g12;
		}
		llf += Math.log(omega) * en1;  // en1 is total number of faults
		en1 += omega * g00;  // g00 is the last time
		en2 += omega * g01;  // g01 is the last time
		en3 += omega * g02;  // g02 is the last time
		llf += - omega * (1.0 - g00);
		
		// M-step
		omega =  en1;
		shape = findshape(shape, Math.log(en2/en1)-en3/en1);
		scale = shape*en1/en2;
		srm.setOmega(omega);
		srm.setShape(shape);
		srm.setScale(scale);
		return llf;
	}
	
	static int const_tolc = 5;
	static double const_tole = 1.0e-8;
	static int const_maxcount = 50;

	static double findshape(double a, double c) {
		int count, good;
		double fx, gx, nx;
		count = 0;
		good = 0;
		while(good < const_tolc) {
			fx = Math.log(a) - Numlib.psi(a) - c;
			gx = 1.0/a - Numlib.polygamma(1,a);
			nx = a - fx/gx;
			if (nx <= 0.0) {
				return a;
			}
			if (Math.abs(fx) < const_tole) {
				good ++;
			} else {
				good = 0;
			}
			a = nx;
			count++;
			if (count > const_maxcount) {
				return a;
			}
		}
		return a;
	}
}
