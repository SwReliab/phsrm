package phsrm.original;

import phsrm.common.*;

abstract public class CommonSRM extends NHPPSoftwareReliabilityModels {

	EMCommonSRM emsrm;

	abstract double getPDF(double t);
	abstract double getCDF(double t);
	
	// mean, reli, frate
	final public MVFData getMeanValueFunction(double[] intervals, double t) {
		double cur, geta;
		MVFData res = new MVFData();
		if (t == 0.0) {
			cur = 0.0;
			geta = 0.0;
		} else {
			cur = t;
			geta = getCDF(cur);
		}
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			res.addDataRecord(cur, omega * (getCDF(cur) - geta));
		}
		return res;
	}
	
	final public MVFData getReliabilityFunction(double s, double[] intervals) {
		double cur;
		cur = s;
		MVFData res = new MVFData(s, 1.0);
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			res.addDataRecord(cur, Math.exp(-omega*(getCDF(cur) - getCDF(s))));
		}
		return res;
	}

	final public MVFData getFailureRate(double[] intervals) {
		double cur;
		MVFData res = new MVFData(0, getPDF(0));
		cur = 0.0;
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			res.addDataRecord(cur, getPDF(cur) / (1.0 - getCDF(cur)));
		}
		return res;
	}

	final public double getLogLikelihood() {
		return getEMControl().getLLF();
	}

	final public double getAIC() {
		return ((-2.0) * getEMControl().getLLF() + 2.0 * getNumberOfParameters());
	}

	final public double getBIC() {
		int fn = emsrm.getGroupData().getNumberOfRecords();
		return ((-2.0) * getEMControl().getLLF() + Math.log(fn) * getNumberOfParameters());
	}

	final public EMControl getEMControl() {
		return emsrm.getEMControl();
	}

	final public void doEstimation(GroupData dat) {
		emsrm.setGroupData(dat);
		emsrm.doEstimation();
	}
	
	final public void doEstimation(GroupData dat, EMControl em) {
		emsrm.setGroupData(dat);
		emsrm.setEMControl(em);
		emsrm.doEstimation();
	}

	abstract public String getParameterString();
}
