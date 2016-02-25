
import phsrm.common.*;
import phsrm.cphsrm.*;
import phsrm.hersrm.*;
import phsrm.original.*;

public class testMain {
    public static void main (String args[]) {
		GroupData gdat = new GroupData();
		GroupData cgdat = new GroupData();

		String gdatname = args[0];
		String cgdatname = args[1];

//		gdat.readDataFromFile("/Users/okamu/Work.localized/Journals.localized/z-IEEE_TSE_phase/comp/data/time70_ss1c.txt");
//		cgdat.readDataFromFile("/Users/okamu/Work.localized/Journals.localized/z-IEEE_TSE_phase/comp/data/timeCC70_ss1c.txt");

		gdat.readDataFromFile(gdatname);
		cgdat.readDataFromFile(cgdatname);

		double llf, aic, bic, mse;

		CPHSRM cphsrm;
		for (int i=2; i<=10; i++) {
			cphsrm = new CPHSRM(i);
			cphsrm.setInitialParameters(gdat);
			EMControlRelativeLF hem = new EMControlRelativeLF();
			cphsrm.doEstimation(gdat, hem);
			llf = cphsrm.getLogLikelihood();
			aic = cphsrm.getAIC();
			bic = cphsrm.getBIC();
			mse = MeanSquareErrors.getMSE(
					cphsrm.getMeanValueFunction(cgdat.getTimeArray(), gdat.getTotalTime()), 
					cgdat);
			String str = cphsrm.getModelString() + " "
						+ "LLF = " + llf + " "
						+ "AIC = " + aic + " "
						+ "BIC = " + bic + " "
						+ "pMSE = " + mse + "\n";
			System.out.println(str);
		}

		HyperErlangSRM hysrm;
		for (int i=2; i<=10; i++) {
			hysrm = new HyperErlangSRM(i);
			hysrm.setInitialParameters(gdat);
			EMControlRelativeLF hem = new EMControlRelativeLF();
			hysrm.doEstimation(gdat, hem);
			llf = hysrm.getLogLikelihood();
			aic = hysrm.getAIC();
			bic = hysrm.getBIC();
			mse = MeanSquareErrors.getMSE(
					hysrm.getMeanValueFunction(cgdat.getTimeArray(), gdat.getTotalTime()), 
					cgdat);
			String str = hysrm.getModelString() + " "
						+ "LLF = " + llf + " "
						+ "AIC = " + aic + " "
						+ "BIC = " + bic + " "
						+ "pMSE = " + mse + "\n";
			System.out.println(str);
		}
		
		CommonSRM[] csrm = new CommonSRM[11];

		csrm[0] = new ExponentialSRM ();
		csrm[1] = new GammaSRM ();
		csrm[2] = new ParetoSRM ();
		csrm[3] = new TruncatedNormalSRM ();
		csrm[4] = new LogNormalSRM ();
		csrm[5] = new TruncatedLogisticSRM ();
		csrm[6] = new LogLogisticSRM ();
		csrm[7] = new TruncatedExtremeValueMaxSRM ();
		csrm[8] = new LogExtremeValueMaxSRM ();
		csrm[9] = new TruncatedExtremeValueMinSRM ();
		csrm[10] = new LogExtremeValueMinSRM ();
		
		for (int i=0; i<csrm.length; i++) {
			csrm[i].setInitialParameters(gdat);
			EMControlRelativeLF hem = new EMControlRelativeLF();
			csrm[i].doEstimation(gdat, hem);
			llf = csrm[i].getLogLikelihood();
			aic = csrm[i].getAIC();
			bic = csrm[i].getBIC();
			mse = MeanSquareErrors.getMSE(
					csrm[i].getMeanValueFunction(cgdat.getTimeArray(), gdat.getTotalTime()), 
					cgdat);
			String str = csrm[i].getModelString() + " "
						+ "LLF = " + llf + " "
						+ "AIC = " + aic + " "
						+ "BIC = " + bic + " "
						+ "pMSE = " + mse + "\n";
			System.out.println(str);
		}
    }
}
