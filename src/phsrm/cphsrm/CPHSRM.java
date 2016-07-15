package phsrm.cphsrm;

import phsrm.common.*;

final public class CPHSRM extends NHPPSoftwareReliabilityModels {

	CanonicalPHParameters cph;
	CanonicalUniformization cuni;
	
	double[] tmp;

	public CPHSRM() {
		cph = new CanonicalPHParameters();
		cuni = new CanonicalUniformization(1.0e-8, 1.01);
	}
	
	public CPHSRM(int m) {
		cph = new CanonicalPHParameters();
		cuni = new CanonicalUniformization(1.0e-8, 1.01);
		setPhaseSize(m);
	}
	
	public String getModelString() {
		return "" + cph.getPhaseSize() + "-CPHSRM";
	}

	public void setPhaseSize(int m) {
		cph.setPhaseSize(m);
		cuni.setPhaseSize(m);
		tmp = new double [m];
	}
	
	public void setParameters(double omega, double[] initv, double[] rate) {
		this.omega = omega;
		cph.setInitialVector(initv);
		cph.setRateParameter(rate);
		cph.transformCanonicalForm();
		cuni.setRate(cph.getRateParameter());
	}
	
	public CanonicalPHParameters getCPHParameter() {
		return cph;
	}
	
	public CanonicalUniformization getCPHSolver() {
		return cuni;
	}
	
	public String getParameterString() {
		String buf ="omega: " + SRMText.decimalE8.format(omega) + SRMText.ln
			+ cph.getParameterString();
		return buf;
	}
	
	public MVFData getMeanValueFunction(double[] intervals, double t) {
		double sum, cur, geta;
		MVFData res = new MVFData();
		System.arraycopy(cph.getInitialVector(), 0, tmp, 0, cph.getPhaseSize());
		if (t == 0.0) {
			cur = 0.0;
			geta = 0.0;
		} else {
			cur = t;
			cuni.doForward(t, tmp);
			geta = 1.0;
			for (int j=0; j<cph.getPhaseSize(); j++) {
				geta -= tmp[j];
			}
		}
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			cuni.doForward(intervals[i], tmp);
			sum = 1.0;
			for (int j=0; j<cph.getPhaseSize(); j++) {
				sum -= tmp[j];
			}
			res.addDataRecord(cur, omega * (sum - geta));
		}
		return res;
	}
	
	public MVFData getReliabilityFunction(double s, double[] intervals) {
		double nsum, fsum, cur;
		System.arraycopy(cph.getInitialVector(), 0, tmp, 0, cph.getPhaseSize());
		cuni.doForward(s, tmp);
		fsum = 0.0;
		for (int j=0; j<cph.getPhaseSize(); j++) {
			fsum += tmp[j];
		}
		cur = s;
		MVFData res = new MVFData(s, 1.0);
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			cuni.doForward(intervals[i], tmp);
			nsum = fsum;
			for (int j=0; j<cph.getPhaseSize(); j++) {
				nsum -= tmp[j];
			}
			res.addDataRecord(cur, Math.exp(-omega*nsum));
		}
		return res;
	}

	public MVFData getFailureRate(double[] intervals) {
		int ndim = cph.getPhaseSize();
		double dsum, cur;
		MVFData res = new MVFData(0, cph.getInitialVector()[ndim-1] * cph.getRateParameter()[ndim-1]);
		System.arraycopy(cph.getInitialVector(), 0, tmp, 0, ndim);
		cur = 0.0;
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			cuni.doForward(intervals[i], tmp);
			dsum = 0.0;
			for (int j=0; j<ndim; j++) {
				dsum += tmp[j];
			}
			res.addDataRecord(cur, tmp[ndim-1]*cph.getRateParameter()[ndim-1] / dsum);
		}
		return res;
	}
	
	// information criteria
	
	public int getNumberOfParameters() {
		return 2 * cph.getPhaseSize();
//		int m = 0;
//		for (int i=0; i<cph.getPhaseSize(); i++) {
//			if (cph.getInitialVector()[i] > 1.0e-6) {
//				m++;
//			}
//		}
//		m++;
//		for (int i=0; i<cph.getPhaseSize()-1; i++) {
//			if ((cph.getRateParameter()[i+1] - cph.getRateParameter()[i])/cph.getRateParameter()[i] > 1.0e-6) {
//				m++;
//			}
//		}
//		return m;
	}

	public double getLogLikelihood() {
		return emcph.getEMControl().getLLF();
	}

	public double getAIC() {
		return ((-2.0) * emcph.getEMControl().getLLF() + 2.0 * getNumberOfParameters());
	}

	public double getBIC() {
		int fn = emcph.getGourpData().getNumberOfRecords();
		return ((-2.0) * emcph.getEMControl().getLLF() + Math.log(fn) * getNumberOfParameters());
	}

	EMCPHSRM emcph;
	
	public void doEstimation(GroupData dat) {
		findInitialParameters(dat);
		emcph = new EMCPHSRM();
		emcph.setCPHSRM(this);
		emcph.setGroupData(dat);
		emcph.doEstimation();
	}
	
	public void doEstimation(GroupData dat, EMControl em) {
		findInitialParameters(dat);
		emcph = new EMCPHSRM(em);
		emcph.setCPHSRM(this);
		emcph.setGroupData(dat);
		emcph.doEstimation();
	}
	
	public EMControl getEMControl() {
		return emcph.getEMControl();
	}
	
	// simple estimation
	public void setInitialParameters(GroupData dat) {
		setPowerStructuredParameter(dat, 2);
	}
	
	public void findInitialParameters(GroupData dat) {
		emcph = new EMCPHSRM(new EMControlFixedIteration(5));
		emcph.setCPHSRM(this);
		emcph.setGroupData(dat);

		int method = 1;
		double maxp;
		double[] p = {2.0, 4.0, 8.0, 16.0, 32.0, 64.0};
		maxp = p[0];
		setPowerStructuredParameter(dat, p[0]);
		emcph.doEstimation();
		double maxllf = getLogLikelihood();
		for (int i=1; i<p.length; i++) {
			setPowerStructuredParameter(dat, p[i]);
			emcph.doEstimation();
			if (maxllf < getLogLikelihood()) {
				maxp = p[i];
				method = 1;
			}
		}
		for (int i=0; i<p.length; i++) {
			setLinearStructuredParameter(dat, p[i]);
			emcph.doEstimation();
			if (maxllf < getLogLikelihood()) {
				maxp = p[i];
				method = 2;
			}
		}
		if (method == 1) {
			setPowerStructuredParameter(dat, maxp);
		} else {
			setLinearStructuredParameter(dat, maxp);			
		}
	}

	public void setPowerStructuredParameter(GroupData dat, double shape) {
		this.omega = dat.getTotalNumber();
		cph.setPowerStructuredParameter(dat.getTotalTime(), shape);
		cph.transformCanonicalForm();
		cuni.setRate(cph.getRateParameter());
	}

	public void setLinearStructuredParameter(GroupData dat, double shape) {
		this.omega = dat.getTotalNumber();
		cph.setLinearStructuredParameter(dat.getMedianTime(), shape);
		cph.transformCanonicalForm();
		cuni.setRate(cph.getRateParameter());
	}
}
