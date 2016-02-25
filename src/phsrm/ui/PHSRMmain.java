package phsrm.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import phsrm.common.NHPPSoftwareReliabilityModels;
import phsrm.cphsrm.CPHSRM;
import phsrm.hersrm.HyperErlangSRM;
import phsrm.original.CommonSRM;

public class PHSRMmain implements TreeSelectionListener {
	JFrame mainFrame;
	JSplitPane splitpane;
	JToolBar toolbar;
	
	LogPanel logbuf;

	JTree tree;
	SRMMainPanel srm;
	
	Hashtable<String,Integer> cphsrmNames;
	Hashtable<String,Integer> hysrmNames;
	Hashtable<String,Integer> csrmNames;
	
	CPHSRM[] cphsrms;
	HyperErlangSRM[] hersrms;
	CommonSRM[] csrms;
	
	String datalabel = "S/W Failure Data";
	String estlabel0 = "Models";
	String estlabel1 = "CPHSRM";
	String estlabel2 = "HErSRM";
	String estlabel3 = "Parametric SRMs";
	
	public PHSRMmain(int ncph, int nher) {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(10, 10, 1210, 550);
		mainFrame.setTitle("PHSRM: Phase-Type Software Reliability Model");

		splitpane = new JSplitPane();
		mainFrame.getContentPane().add(splitpane, BorderLayout.CENTER);

		// panel configuration
		DefaultMutableTreeNode sreptRoot = new DefaultMutableTreeNode("PHSRM");
		DefaultMutableTreeNode models = new DefaultMutableTreeNode(estlabel0);
		DefaultMutableTreeNode faultData = new DefaultMutableTreeNode(datalabel);
//		DefaultMutableTreeNode nhpp0 = new DefaultMutableTreeNode(estlabel0);
		DefaultMutableTreeNode nhpp1 = new DefaultMutableTreeNode(estlabel1);
		DefaultMutableTreeNode nhpp2 = new DefaultMutableTreeNode(estlabel2);
		DefaultMutableTreeNode nhpp3 = new DefaultMutableTreeNode(estlabel3);
		
//		models.add(nhpp0);
		models.add(nhpp3);
		models.add(nhpp1);
		models.add(nhpp2);
		
		sreptRoot.add(models);
		sreptRoot.add(faultData);
		
		tree = new JTree(sreptRoot);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
		render.setLeafIcon(new ImageIcon());
		render.setOpenIcon(new ImageIcon());
		render.setClosedIcon(new ImageIcon());
		tree.setCellRenderer(render);
		
		JScrollPane sptree = new JScrollPane();
		sptree.getViewport().setView(tree);
		sptree.setPreferredSize(tree.getPreferredSize());
		splitpane.setLeftComponent(sptree);
		
		JSplitPane splitpane2 = new JSplitPane();
		splitpane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitpane.setRightComponent(splitpane2);
		
		logbuf = new LogPanel();
		logbuf.setPreferredSize(new Dimension(800,10));
		splitpane2.setBottomComponent(logbuf);
		
		srm = new SRMMainPanel(logbuf, ncph, nher);
		splitpane2.setTopComponent(srm);
//		mainFrame.getContentPane().add(srm, BorderLayout.CENTER);
		
		cphsrms = srm.getCPHSRM();
		cphsrmNames = new Hashtable<String,Integer>();
		for (int i=0; i<cphsrms.length; i++) {
			cphsrmNames.put(cphsrms[i].getModelString(), i);
			nhpp1.add(new DefaultMutableTreeNode(cphsrms[i].getModelString()));
		}
		
		hersrms = srm.getHErSRM();
		hysrmNames = new Hashtable<String,Integer>();
		for (int i=0; i<hersrms.length; i++) {
			hysrmNames.put(hersrms[i].getModelString(), i);
			nhpp2.add(new DefaultMutableTreeNode(hersrms[i].getModelString()));
		}
		
		csrms = srm.getCommonSRM();
		csrmNames = new Hashtable<String,Integer>();
		for (int i=0; i<csrms.length; i++) {
			csrmNames.put(csrms[i].getModelString(), i);
			nhpp3.add(new DefaultMutableTreeNode(csrms[i].getModelString()));
		}
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(new JLabel(" "));
		mainFrame.add(toolbar, BorderLayout.SOUTH);

		mainFrame.setVisible(true);
	}
	
    public static void main (String args[]) {
    	PHSRMmain srept;
    	if (args.length == 2) {
        	srept = new PHSRMmain (Integer.parseInt(args[0])-1, Integer.parseInt(args[1])-1);
    	} else {
        	srept = new PHSRMmain (9, 9);	    		
    	}
    }
    
    public void valueChanged(TreeSelectionEvent e) {
		Object[] tmp = tree.getSelectionPath().getPath();
		String selected = tmp[tmp.length-1].toString();
		if (selected.equals(datalabel)) {
			srm.changePanel("DataPanel");
		} else if (selected.equals(estlabel1)) {
			srm.changePanel("EstimationResultPanel1");
		} else if (selected.equals(estlabel2)) {
			srm.changePanel("EstimationResultPanel2");
		} else if (selected.equals(estlabel0)) {
		srm.changePanel("EstimationResultPanel0");
		} else if (selected.equals(estlabel3)) {
			srm.changePanel("EstimationResultPanel3");
		}
		if (cphsrmNames.containsKey(selected)) {
			srm.changePanel("CPHSRMPanel");
			srm.changeCPHSRMModel(cphsrmNames.get(selected));
		}
		if (hysrmNames.containsKey(selected)) {
			srm.changePanel("HErSRMPanel");
			srm.changeHErSRMModel(hysrmNames.get(selected));
		}
		if (csrmNames.containsKey(selected)) {
			srm.changePanel("CommonSRMPanel");
			srm.changeCommonSRMModel(csrmNames.get(selected));
		}
	}
}
