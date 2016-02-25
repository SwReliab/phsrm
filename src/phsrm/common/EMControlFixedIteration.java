package phsrm.common;

public class EMControlFixedIteration extends EMControlWithPrint {
	double likelihoodRatioEPS;
	int nIteration;

	public EMControlFixedIteration() {
		super();
		nIteration = 10;
	}

	public EMControlFixedIteration(int n) {
		super();
		nIteration = n;
	}

	public void runEMRunnableModel(EMRunnable emmodel) {
		status = "START";
		startTime = System.currentTimeMillis();
		count = 0;
		for (count=0; count<nIteration; count++) {
			llf = emmodel.doEMstep();
			if (Double.isNaN(llf)) {
				buf.append("Warnning: llf = " + llf + SRMText.ln);
				return;
			}
		}
		status = "Fixed iteration";
	}
}
