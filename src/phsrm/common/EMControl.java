package phsrm.common;

abstract public class EMControl {

	String status;
	long startTime;
	long stopTime;
	
	double llf;
	int count;
	int maxIteration;

	public EMControl() {
		status = "Idling";
		maxIteration = 10000;
		llf = 0.0;
	}
	
	public void setMaxIteration(int m) {
		maxIteration = m;
	}
	
	public double getLLF() {
		return llf;
	}
	
	public String getStatus() {
		return status;
	}
	
	public abstract void runEMRunnableModel(EMRunnable emmodel);
	
	public String getResultString() {
		String str = "Status: " + status + SRMText.ln
			+ "Iterations = " + count + SRMText.ln
			+ "Computation time = " + (stopTime - startTime) / 1000.0 + " (sec)" + SRMText.ln;
		return str;
	}

}
