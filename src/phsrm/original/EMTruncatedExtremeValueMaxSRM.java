package phsrm.original;

import phsrm.common.*;

public class EMTruncatedExtremeValueMaxSRM extends EMCommonSRM {

	TruncatedExtremeValueMaxSRM srm;
	double omega;
	double shape;
	double scale;
	int dsize;

	//work
	double en1;
	double en2;
	double en3;

	public EMTruncatedExtremeValueMaxSRM(TruncatedExtremeValueMaxSRM srm) {
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
		double x, t, y, y0;
		double tmp1, tmp2, tmp3;
		double llf;
		double g00, g01, g02;
		double g10, g11, g12;
		double g20, g21, g22;
		double en1, en2, en3;
		
		// E-step
		y0 = Math.exp(scale/shape);
		omega = omega / (1.0 - Math.exp(-y0));

		en1 = 0.0;
		en2 = 0.0;
		en3 = 0.0;
		llf = 0.0;
		g00 = 1.0 - Math.exp(-y0);
		g01 = Math.exp(-scale/shape)*(1.0 - (1.0 + y0)*Math.exp(-y0));
		g02 = 1.0 - Math.exp(-y0) * (1.0 + y0 * Math.log(y0));
		t = fdat.getTime(1);
		x = fdat.getNumber(1);
		y = Math.exp(-(t-scale)/shape);
		g10 = 1.0 - Math.exp(-y);
		g11 = Math.exp(-scale/shape)*(1.0 - (1.0 + y)*Math.exp(-y));
		g12 = 1.0 - Math.exp(-y) * (1.0 + y * Math.log(y));
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
			en2 += Math.exp(-t/shape);
			en3 += (t-scale)/shape * (1.0-y);
			llf += Math.log(srm.getPDF(t));
		}
		for (int j=2; j<=fdat.getNumberOfRecords(); j++) {
			t += fdat.getTime(j);
			x = fdat.getNumber(j);
			y = Math.exp(-(t-scale)/shape);
			g20 = 1.0 - Math.exp(-y);
			g21 = Math.exp(-scale/shape)*(1.0 - (1.0 + y)*Math.exp(-y));
			g22 = 1.0 - Math.exp(-y) * (1.0 + y * Math.log(y));
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
				en2 += Math.exp(-t/shape);
				en3 += (t-scale)/shape * (1.0-y);
				llf += Math.log(srm.getPDF(t));
			}
			g10 = g20;
			g11 = g21;
			g12 = g22;
		}
		llf += (Math.log(omega) + Math.log(g00))* en1;  // en1 is total number of faults
		en1 += omega * (1.0 - g00 + g10);  // g00 is the first, g10 is the last
		en2 += omega * (Math.exp(-scale/shape) - g01 + g11);  // g01 is the first, g11 is the last
		en3 += omega * (1.0 - g02 + g12);  // g02 is the first, g12 is the last
		llf += - omega * (g00 - g10);
		
		// M-step
//			shape = shape + Math.log(en3) - Math.log(en1);
		shape = shape * (en3 / en1);
		scale = -shape * Math.log(en2/en1);
		y0 = Math.exp(scale/shape);
		omega =  en1 * (1.0 - Math.exp(-y0));
		
		srm.setOmega(omega);
		srm.setShape(shape);
		srm.setScale(scale);
		return llf;
	}
}
