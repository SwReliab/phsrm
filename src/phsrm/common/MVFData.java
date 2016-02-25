package phsrm.common;

import java.util.ArrayList;

public class MVFData {
	
	int size;
	ArrayList<Double> x;
	ArrayList<Double> y;

	public MVFData() {
		size = 0;
		x = new ArrayList<Double>();
		y = new ArrayList<Double>();
		x.add(new Double(0.0));
		y.add(new Double(0.0));
	}
	
	public MVFData(double t, double v) {
		size = 0;
		x = new ArrayList<Double>();
		y = new ArrayList<Double>();
		x.add(new Double(t));
		y.add(new Double(v));
	}

	public void addDataRecord(double t, double v) {
		size += 1;
		x.add(new Double(t));
		y.add(new Double(v));
	}
	
	public int getSize() {
		return size;
	}

	public double getX(int i) {
		return x.get(i).doubleValue();
	}

	public double getY(int i) {
		return y.get(i).doubleValue();
	}

	public String getMVFString() {
		String sp = " ";
		StringBuffer buf = new StringBuffer();
		buf.append("# Time" + sp + "Number" + SRMText.ln);
		for (int i=1; i<=size; i++) {
			buf.append(getX(i) + sp + getY(i) + SRMText.ln);
		}
		return buf.toString();
	}

}
