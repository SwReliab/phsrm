package phsrm.original;

import phsrm.common.*;

public class EMTruncatedNormalSRM extends EMCommonSRM {

	TruncatedNormalSRM srm;
	double omega;
	double shape;
	double scale;
	int dsize;

	//work
	double en1;
	double en2;
	double en3;

	public EMTruncatedNormalSRM(TruncatedNormalSRM srm) {
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
		double g20, g21, g22;
		double en1, en2, en3;
		
		// E-step
		omega = omega / Numlib.q_normal(-scale/shape);

		en1 = 0.0;
		en2 = 0.0;
		en3 = 0.0;
		llf = 0.0;
		tmp = Numlib.d_normal(-scale/shape);
		g00 = Numlib.q_normal(-scale/shape);
		g01 = shape*tmp + scale*g00;
		g02 = (scale*shape)*tmp + (shape*shape + scale*scale)*g00;
		t = fdat.getTime(1);
		x = fdat.getNumber(1);
		y = (t-scale)/shape;
		tmp = Numlib.d_normal(y);
		g10 = Numlib.q_normal(y);
		g11 = shape*tmp + scale*g10;
		g12 = (shape*t + scale*shape)*tmp + (shape*shape + scale*scale)*g10;
		if (x != 0.0) {
			tmp1 = g00 - g10;
			tmp2 = g01 - g11;
			tmp3 = g02 - g12;
			en1 += x;
			en2 += x * tmp2 / tmp1;
			en3 += x * tmp3 / tmp1;
			llf += x * (Math.log(tmp1) - Math.log(g00)) - Numlib.loggamma(x+1);
		}
		if (fdat.getType(1) == 1) {
			en1 += 1.0;
			en2 += t;
			en3 += t * t;
			llf += Math.log(srm.getPDF(t));
		}
		for (int j=2; j<=fdat.getNumberOfRecords(); j++) {
			t += fdat.getTime(j);
			x = fdat.getNumber(j);
			y = (t-scale)/shape;
			tmp = Numlib.d_normal(y);
			g20 = Numlib.q_normal(y);
			g21 = shape*tmp + scale*g20;
			g22 = (shape*t + scale*shape)*tmp + (shape*shape + scale*scale)*g20;
			if (x != 0.0) {
				tmp1 = g10 - g20;
				tmp2 = g11 - g21;
				tmp3 = g12 - g22;
				en1 += x;
				en2 += x * tmp2 / tmp1;
				en3 += x * tmp3 / tmp1;
				llf += x * (Math.log(tmp1) - Math.log(g00))- Numlib.loggamma(x+1);
			}
			if (fdat.getType(j) == 1) {
				en1 += 1.0;
				en2 += t;
				en3 += t * t;
				llf += Math.log(srm.getPDF(t));
			}
			g10 = g20;
			g11 = g21;
			g12 = g22;
		}
		llf += (Math.log(omega) + Math.log(g00)) * en1;  // en1 is total number of faults
		en1 += omega * (1.0 - g00 + g10);  // g00 is the first, g10 is the last
		en2 += omega * (scale - g01 + g11);  // g01 is the first, g11 is the last
		en3 += omega * (shape*shape + scale*scale - g02 + g12);  // g02 is the first, g12 is the last
		llf += - omega * (g00 - g10);
		
		// M-step
		scale = en2 / en1;
		shape = Math.sqrt(en3 / en1 - scale*scale);
		omega =  en1 * Numlib.q_normal(-scale/shape);
		srm.setOmega(omega);
		srm.setScale(scale);
		srm.setShape(shape);
		return llf;
	}
}
