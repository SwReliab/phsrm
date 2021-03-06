package phsrm.common;


public class KolmogorovSmirnovTest {
	
	static double ksTable[][] = {
		{0.97500, 0.99500}, {0.84189, 0.92929}, {0.70760, 0.82900},
		{0.62394, 0.73424}, {0.56328, 0.66853}, {0.51926, 0.61661},
		{0.48342, 0.57581}, {0.45427, 0.54179}, {0.43001, 0.51332},
		{0.40925, 0.48893}, {0.39122, 0.46770}, {0.37543, 0.44905},
		{0.36143, 0.43247}, {0.34890, 0.41762}, {0.33760, 0.40420},
		{0.32733, 0.39201}, {0.31796, 0.38086}, {0.30936, 0.37062},
		{0.30143, 0.36117}, {0.29408, 0.35241}, {0.28724, 0.34427},
		{0.28087, 0.33666}, {0.27490, 0.32954}, {0.26931, 0.32286},
		{0.26404, 0.31657}, {0.25907, 0.31064}, {0.25438, 0.30502},
		{0.24993, 0.29971}, {0.24571, 0.29466}, {0.24170, 0.28987},
		{0.23788, 0.28530}, {0.23424, 0.28094}, {0.23076, 0.27677},
		{0.22743, 0.27279}, {0.22425, 0.26897}, {0.22119, 0.26532},
		{0.21826, 0.26180}, {0.21544, 0.25843}, {0.21273, 0.25518},
		{0.21012, 0.25205}, {0.20760, 0.24904}, {0.20517, 0.24613},
		{0.20283, 0.24332}, {0.20056, 0.24060}, {0.19837, 0.23798},
		{0.19625, 0.23544}, {0.19420, 0.23298}, {0.19221, 0.23059},
		{0.19028, 0.22828}, {0.18841, 0.22604}, {0.18659, 0.22386},
		{0.18482, 0.22174}, {0.18311, 0.21968}, {0.18144, 0.21768},
		{0.17981, 0.21574}, {0.17823, 0.21384}, {0.17669, 0.21199},
		{0.17519, 0.21019}, {0.17373, 0.20844}, {0.17231, 0.20673},
		{0.17091, 0.20506}, {0.16956, 0.20343}, {0.16823, 0.20184},
		{0.16693, 0.20029}, {0.16567, 0.19877}, {0.16443, 0.19729},
		{0.16322, 0.19584}, {0.16204, 0.19442}, {0.16088, 0.19303},
		{0.15975, 0.19167}, {0.15864, 0.19034}, {0.15755, 0.18903},
		{0.15649, 0.18776}, {0.15544, 0.18650}, {0.15442, 0.18528},
		{0.15342, 0.18408}, {0.15244, 0.18290}, {0.15147, 0.18174},
		{0.15052, 0.18060}, {0.14960, 0.17949}, {0.14868, 0.17840},
		{0.14779, 0.17732}, {0.14691, 0.17627}, {0.14605, 0.17523},
		{0.14520, 0.17421}, {0.14437, 0.17321}, {0.14355, 0.17223},
		{0.14274, 0.17126}, {0.14195, 0.17031}, {0.14117, 0.16938},
		{0.14040, 0.16846}, {0.13965, 0.16755}, {0.13891, 0.16666},
		{0.13818, 0.16579}, {0.13746, 0.16493}, {0.13675, 0.16408},
		{0.13606, 0.16324}, {0.13537, 0.16242}, {0.13469, 0.16161},
		{0.13403, 0.16081}
	};

	public static double getKSDistance(MVFData mvf, GroupData dat) {
		int i;
		int fn = dat.getNumberOfRecords();
		double ks, tmp, me, sum = 0.0;
		double tn = dat.getTotalNumber();

		ks = 0;
		me = mvf.getY(mvf.getSize());
		for(i=1; i<=fn; i++) {
			tmp = mvf.getY(i)/me;
			ks = Math.max(ks, Math.abs(tmp - sum / tn));
			sum += dat.getNumber(i) + dat.getType(i);
			ks = Math.max(ks, Math.abs(tmp - sum / tn));
		}
		return ks;
	}

	public static boolean isKSTest90(MVFData mvf, GroupData dat) {
		double ks = getKSDistance(mvf, dat);
		int fn = dat.getNumberOfRecords();
		if (fn > 100) {
			if (ks < 1.62762 / Math.sqrt(fn)) {
				return true;
			} else {
				return false;
			}
		} else {
			if (ks < ksTable[fn-1][1]) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean isKSTest95(MVFData mvf, GroupData dat) {
		double ks = getKSDistance(mvf, dat);
		int fn = dat.getNumberOfRecords();
		if (fn > 100) {
			if (ks < 1.35810 / Math.sqrt(fn)) {
				return true;
			} else {
				return false;
			}
		} else {
			if (ks < ksTable[fn-1][0]) {
				return true;
			} else {
				return false;
			}
		}
	}
}
