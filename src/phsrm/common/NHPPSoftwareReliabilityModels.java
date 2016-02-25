package phsrm.common;

abstract public class NHPPSoftwareReliabilityModels {

	protected double omega;
		
	abstract public String getModelString();

	final public void setOmega(double omega) {
		this.omega = omega;
	}
		
	final public double getTotal() {
		return omega;
	}
				
	// mean, reli, frate
	abstract public MVFData getMeanValueFunction(double[] intervals, double t);
	public MVFData getMeanValueFunction(double[] intervals) {
		return getMeanValueFunction(intervals, 0.0);
	}
	abstract public MVFData getReliabilityFunction(double s, double[] intervals);
	abstract public MVFData getFailureRate(double[] intervals);

	abstract public int getNumberOfParameters();
	abstract public double getLogLikelihood();
	abstract public double getAIC();
	abstract public double getBIC();

	abstract public void doEstimation(GroupData dat);
	abstract public EMControl getEMControl();
	abstract public void setInitialParameters(GroupData dat);
}
