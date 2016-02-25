package phsrm.original;

import phsrm.common.*;

public class EMLogNormalSRM extends EMCommonSRM {

	LogNormalSRM srm;
	double omega;
	double shape;
	double scale;
	int dsize;

	//work
	double en1;
	double en2;
	double en3;

	public EMLogNormalSRM(LogNormalSRM srm) {
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
		double x, t, y;
		double tmp1, tmp2, tmp3;
		double tmp, llf;
		double g00, g01, g02;
		double g10, g11, g12;
		double en1, en2, en3;
		
		// E-step
		t = fdat.getTime(1);
		x = fdat.getNumber(1);
		y = (Math.log(t)-scale)/shape;
		en1 = 0.0;
		en2 = 0.0;
		en3 = 0.0;
		llf = 0.0;
		tmp = Numlib.d_normal(y);
		g00 = Numlib.q_normal(y);
		g01 = shape*tmp + scale*g00;
		g02 = (shape*Math.log(t) + scale*shape)*tmp + (shape*shape + scale*scale)*g00;
		if (x != 0.0) {
			tmp1 = 1.0 - g00;
			tmp2 = scale - g01;
			tmp3 = shape*shape + scale*scale - g02;
			en1 += x;
			en2 += x * tmp2 / tmp1;
			en3 += x * tmp3 / tmp1;
			llf += x * Math.log(tmp1) - Numlib.loggamma(x+1);
		}
		if (fdat.getType(1) == 1) {
			en1 += 1.0;
			en2 += Math.log(t);
			en3 += Math.log(t) * Math.log(t);
			llf += Math.log(srm.getPDF(t));
		}
		for (int j=2; j<=fdat.getNumberOfRecords(); j++) {
			t += fdat.getTime(j);
			x = fdat.getNumber(j);
			y = (Math.log(t)-scale)/shape;
			tmp = Numlib.d_normal(y);
			g10 = Numlib.q_normal(y);
			g11 = shape*tmp + scale*g10;
			g12 = (shape*Math.log(t) + scale*shape)*tmp + (shape*shape + scale*scale)*g10;
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
				en2 += Math.log(t);
				en3 += Math.log(t) * Math.log(t);
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
		scale = en2 / en1;
		shape = Math.sqrt(en3 / en1 - scale*scale);
		srm.setOmega(omega);
		srm.setScale(scale);
		srm.setShape(shape);
		return llf;
	}
}
