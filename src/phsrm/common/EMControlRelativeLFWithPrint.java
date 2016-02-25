package phsrm.common;

final public class EMControlRelativeLFWithPrint extends EMControlWithPrint {

	double likelihoodRatioEPS;

	public EMControlRelativeLFWithPrint() {
		super();
		setRelativeLikelihoodRatioEPS(1.0e-7);
	}

	public EMControlRelativeLFWithPrint(double eps) {
		super();
		setRelativeLikelihoodRatioEPS(eps);
	}

	public void setRelativeLikelihoodRatioEPS(double eps) {
		likelihoodRatioEPS = eps;
	}

	public void runEMRunnableModel(EMRunnable emmodel) {
		double previousLLF;
		status = "START";
		startTime = System.currentTimeMillis();
		count = 0;
		llf = 0.0;
		while (true) {
			previousLLF = llf;
			llf = emmodel.doEMstep();
			if (Double.isNaN(llf)) {
				buf.append("Warnning: llf = " + llf + SRMText.ln);
				return;
			}
			count += 1;
			if (count % logSegment == 0 || count <= logMin) {
				buf.append("---------------------" + SRMText.ln);
				buf.append("Iteration      = " + count + SRMText.ln);
				buf.append("Log-Likelihood = " + llf
						+ " (rerror = " + Math.abs((llf - previousLLF)/previousLLF) + ")" + SRMText.ln);
				buf.append(emmodel.getParameterString());
			}
			if (count >= maxIteration) {
				status = "Max iteration";
				stopTime = System.currentTimeMillis();
				return;
			}
			if (count > 1 && llf - previousLLF < 0.0) {
				buf.append("Warnning: count = " + count + SRMText.ln);
			}
			if (count > 1 && Math.abs((llf - previousLLF)/previousLLF) < likelihoodRatioEPS) {
				status = "Convergence";
				stopTime = System.currentTimeMillis();
				buf.append(getResultString());
				buf.append("Log-Likelihood = " + llf + " "
						+ "(rerror = " + Math.abs((llf - previousLLF)/previousLLF) + ", "
						+ "aerror = " + Math.abs(llf - previousLLF) + ")" + SRMText.ln);
				buf.append(emmodel.getParameterString());
				return;
			}
		}
	}
}
