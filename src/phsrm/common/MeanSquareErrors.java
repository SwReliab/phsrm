package phsrm.common;


public class MeanSquareErrors {
	
	public static double getMSE(MVFData mvf, GroupData dat) {
		int i;
		int fn = dat.getNumberOfRecords();
		double err, tmp, sum = 0.0;

		err = 0.0;
		for(i=1; i<=fn; i++) {
			tmp = mvf.getY(i);
			sum += dat.getNumber(i) + dat.getType(i);
			err += (tmp - sum) * (tmp - sum);
		}
		return err;
	}
}
