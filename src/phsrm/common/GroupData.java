package phsrm.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GroupData {
	int length;
	double cur;
	ArrayList<Double> time;
	ArrayList<Double> num;
	ArrayList<Integer> type;
	
	public GroupData() {
		length = 0;
		time = new ArrayList<Double>();
		num = new ArrayList<Double>();
		type = new ArrayList<Integer>();
	};

	public int readDataFromFile(String filename) {
		double t, x;
		int b;
		try {
			FileReader in = new FileReader(filename);
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "\t");
				t = Double.parseDouble(st.nextToken());
				x = Double.parseDouble(st.nextToken());
				b = Integer.parseInt(st.nextToken());
				addIntervalRecord(t, x, b);
			}
			br.close();
			in.close();
			return 0;
		} catch (IOException e) {
			System.out.println(e);
			return 1;
		}
	}

	public void addIntervalRecord(double t, double x, int b) {
		length += 1;
		time.add(new Double(t));
		num.add(new Double(x));
		type.add(new Integer(b));
	}

	public double getTime(int i) {
		return time.get(i-1).doubleValue();
	}

	public double getNumber(int i) {
		return num.get(i-1).doubleValue();
	}

	public int getType(int i) {
		return type.get(i-1).intValue();
	}

	public int getNumberOfRecords() {
		return length;
	}

	public String getDataString() {
		String sp = " ";

		StringBuffer buf = new StringBuffer();
		buf.append("Time" + sp + "Number" + sp + "Type" + SRMText.ln);
		for (int i=1; i<=length; i++) {
			buf.append(getTime(i) + sp + getNumber(i) + sp + getType(i) + SRMText.ln);
		}
		return buf.toString();
	}
	
	public double[] getTimeArray() {
		double[] res = new double [length];
		for (int i=0; i<length; i++) {
			res[i] = getTime(i+1);
		}
		return res;
	}
	
	public MVFData getMeanValueFunction() {
		MVFData res = new MVFData();
		double cur = 0;
		double cumsum = 0;
		for (int i=1; i<=length; i++) {
			cur += getTime(i);
			cumsum += getNumber(i) + getType(i);
			res.addDataRecord(cur, cumsum);
		}
		return res;
	}

	public double getTotalTime() {
		double sum = 0.0;
		for (int i=1; i<=length; i++) {
			sum += getTime(i);
		}
		return sum;
	}

	public double getTotalNumber() {
		double sum = 0.0;
		for (int i=1; i<=length; i++) {
			sum += getNumber(i) + getType(i);
		}
		return sum;
	}

	public double getMedianTime() {
		double sum = 0.0;
		for (int i=1; i<=length/2+1; i++) {
			sum += getTime(i);
		}
		return sum;
	}
}
