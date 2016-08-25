package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ehownet.EHowNetNode;
import ehownet.EHowNetTree;
import tool.OntologyAcquisition;
import tool.OntologyNode;

public class UIPanel extends JPanel implements ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelTopic, labelConcept, labelDir, labelEHowNet, labelWarning, labelInfo;
	private JTextField textConcept, textDir, textEHowNet, textSearch;
	private JButton btnDir, btnEHowNet, btnStart, btnCancel, btnDump, btnSearchNext;
	private JTextArea taInfo;
	private JScrollPane scPane;
	private JTree tree;
	private JCheckBox cbExpand;
	private Timer timer;
	private int searchIdx;
	private OntologyAcquisition oa;
	
	class TTask extends TimerTask {
		
		public void run() {
			if(tree == null) {
				cbExpand.setEnabled(false);
				textSearch.setEnabled(false);
				btnSearchNext.setEnabled(false);
				btnDump.setEnabled(false);
			}
			else {
				cbExpand.setEnabled(true);
				textSearch.setEnabled(true);
				btnSearchNext.setEnabled(true);
				btnDump.setEnabled(true);
			}
			labelWarning.setVisible(false);
			if( labelWarning.getText().startsWith("Process") || labelWarning.getText().startsWith("Keyword") || labelWarning.getText().startsWith("Ontology") )
				labelWarning.setVisible(true);
			else	labelWarning.setText("");
			btnStart.setEnabled(true);
			if( !new File( textDir.getText() ).exists() ) {
				labelWarning.setText("Directory " + textDir.getText()  +" not Found");
				labelWarning.setVisible(true);
				btnStart.setEnabled(false);
			}
			if( !new File( textEHowNet.getText() ).exists() ) {
				labelWarning.setText("Directory " + textEHowNet.getText()  +" not Found");
				labelWarning.setVisible(true);
				btnStart.setEnabled(false);
			}
			if( textConcept.getText().isEmpty() ) {
				labelWarning.setText("Invalid Root Concept");
				labelWarning.setVisible(true);
				btnStart.setEnabled(false);
			}
		}
		
	}
	
	public UIPanel() {
		setLayout(null);
		searchIdx = 0;
		// add to the panel
		add( labelTopic = new JLabel("Ontology Acquisition") );
		add( labelConcept = new JLabel("Root Concept of the Ontology") );
		add( textConcept = new JTextField("", 8) );
		add( labelDir = new JLabel("Directory of the CKIP Documents") );
		add( textDir = new JTextField("docs\\ckip\\", 12) );
		add( btnDir = new JButton("Browse") );
		add( labelEHowNet = new JLabel("Path of the EHowNet File") );
		add( textEHowNet = new JTextField("docs\\ehownet_ontology.txt", 12) );
		add( btnEHowNet = new JButton("Browse") );
		add( btnStart = new JButton("Start") );
		add( btnCancel = new JButton("Cancel") );
		add( btnDump = new JButton("Save to Excel") );
		add( labelInfo = new JLabel("Detail Info") );
		add( taInfo = new JTextArea(6, 20) );
		add( labelWarning = new JLabel("") );
		add( textSearch = new JTextField("", 8) );
		add( btnSearchNext = new JButton("Search Next") );
		add( scPane = new JScrollPane() );
		add( cbExpand = new JCheckBox("Expand All") );
		// add listener
		btnDir.addActionListener(this);
		btnEHowNet.addActionListener(this);
		btnStart.addActionListener(this);
		btnCancel.addActionListener(this);
		btnDump.addActionListener(this);
		btnSearchNext.addActionListener(this);
		cbExpand.addItemListener(this);
		// set found and background/foreground
		textConcept.setFont( new Font("Microsoft JhengHei UI", Font.PLAIN, 16) );
		textDir.setFont( new Font("Tahoma", Font.PLAIN, 18) );
		textEHowNet.setFont( new Font("Tahoma", Font.PLAIN, 18) );
		textSearch.setFont( new Font("Microsoft JhengHei UI", Font.PLAIN, 14) );
		btnStart.setFont( new Font("Pepsi", Font.BOLD, 16) );
		btnCancel.setFont( new Font("Pepsi", Font.BOLD, 16) );
		btnDump.setFont( new Font("Pepsi", Font.BOLD, 16) );
		labelTopic.setForeground(Color.darkGray);
		labelTopic.setFont( new Font("Serif", Font.BOLD, 36) );
		labelConcept.setFont( new Font("Tahoma", Font.BOLD, 18) );
		labelDir.setFont( new Font("Tahoma", Font.BOLD, 18) );
		labelEHowNet.setFont( new Font("Tahoma", Font.BOLD, 18) );
		labelInfo.setFont( new Font("Tahoma", Font.BOLD, 18) );
		labelWarning.setFont( new Font("consolas", Font.PLAIN, 18) );
		labelWarning.setForeground(Color.RED);
		taInfo.setFont( new Font("Microsoft JhengHei UI", Font.PLAIN, 16) );
		cbExpand.setFont( new Font("Tahoma", Font.BOLD, 18) );
		// set editable, wrap
		taInfo.setEditable(false);
		taInfo.setLineWrap(true);
		taInfo.setWrapStyleWord(true);
		// set Bounds
		labelTopic.setBounds(10, 10, labelTopic.getPreferredSize().width, labelTopic.getPreferredSize().height);
		labelConcept.setBounds(10, 80, labelConcept.getPreferredSize().width, labelConcept.getPreferredSize().height);
		textConcept.setBounds(40, 110, textConcept.getPreferredSize().width, textConcept.getPreferredSize().height);
		labelDir.setBounds(10, 150, labelDir.getPreferredSize().width, labelDir.getPreferredSize().height);
		textDir.setBounds(40, 180, textDir.getPreferredSize().width, textDir.getPreferredSize().height);
		btnDir.setBounds(240, 180, btnDir.getPreferredSize().width, btnDir.getPreferredSize().height);
		labelEHowNet.setBounds(10, 220, labelDir.getPreferredSize().width, labelDir.getPreferredSize().height);
		textEHowNet.setBounds(40, 250, textDir.getPreferredSize().width, textDir.getPreferredSize().height);
		btnEHowNet.setBounds(240, 250, btnDir.getPreferredSize().width, btnDir.getPreferredSize().height);
		btnStart.setBounds(15, 300, 90, btnStart.getPreferredSize().height);
		btnDump.setBounds(110, 300, 140, btnDump.getPreferredSize().height);
		btnCancel.setBounds(255, 300, 90, btnCancel.getPreferredSize().height);
		labelInfo.setBounds(10, 370, labelInfo.getPreferredSize().width, labelInfo.getPreferredSize().height);
		taInfo.setBounds(20, 410, taInfo.getPreferredSize().width, taInfo.getPreferredSize().height);
		labelWarning.setBounds(10, 560, 650, 30);
		textSearch.setBounds(560, 20, textSearch.getPreferredSize().width, textSearch.getPreferredSize().height);
		btnSearchNext.setBounds(680, 20, btnSearchNext.getPreferredSize().width, btnSearchNext.getPreferredSize().height);
		cbExpand.setBounds(650, 550, cbExpand.getPreferredSize().width, cbExpand.getPreferredSize().height);
		scPane.setBounds(350, 50, 400, 500);
		// create timer
		timer = new Timer();
		timer.schedule(new TTask(), 500, 1000);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == btnStart) {
			// start the process
			long timeStart = System.currentTimeMillis();
			oa = new OntologyAcquisition( textConcept.getText(), textDir.getText(), textEHowNet.getText() );
			OntologyNode root = oa.start();
			long timeEnd = System.currentTimeMillis();
			labelWarning.setVisible(true);
			labelWarning.setText("Process Completed Successfully in " + (timeEnd - timeStart) + " ms");
			// build the JTree
			DefaultMutableTreeNode uiRoot = new DefaultMutableTreeNode( root.getConcept() );
			Queue<OntologyNode> queue = new LinkedList<OntologyNode>();
			Queue<DefaultMutableTreeNode> queueUI = new LinkedList<DefaultMutableTreeNode>();
			queue.add(root);
			queueUI.add(uiRoot);
			while(queue.isEmpty() == false) {
				OntologyNode curNode = queue.poll();
				DefaultMutableTreeNode curUINode = queueUI.poll();
				DefaultMutableTreeNode attrNode = new DefaultMutableTreeNode("Attributes");
				if(curNode.getAttr().size() > 0)	curUINode.add(attrNode);
				for( String s : curNode.getAttr() )	attrNode.add( new DefaultMutableTreeNode(s) );
				for( OntologyNode n : curNode.getCategories() ) {
					DefaultMutableTreeNode newUINode = new DefaultMutableTreeNode( n.getConcept() );
					curUINode.add(newUINode);
					queue.add(n);
					queueUI.add(newUINode);
				}
			}
			// reset the scroll panel and set bounds
			if(scPane != null)	remove(scPane);
			tree = new JTree(uiRoot);
			tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					String concept = ( (DefaultMutableTreeNode)tree.getLastSelectedPathComponent() ).getUserObject().toString();
					List<EHowNetNode> results = EHowNetTree.getInstance( textEHowNet.getText() ).searchWord(concept);
					String buffer = e.getPath().toString() + "\n";
					buffer += ("TF = " + oa.getTermFreq(concept) + ", ");
					buffer += ("DF = " + oa.getDocFreq(concept) + "\n");
					for(EHowNetNode r : results)	buffer += (r.getEhownet() + " " + r.getPos()  + "\n");
					taInfo.setText(buffer);
				}
			});
			scPane.add(tree);
			scPane = new JScrollPane(tree);
			add(scPane);
			scPane.setBounds(350, 50, 400, 500);
		}
		if(ae.getSource() == btnCancel)	System.exit(0);
		if(ae.getSource() == btnDump) {
			oa.dump();
			labelWarning.setText("Ontology Saved to \"result.xls\"");
		}
		if(ae.getSource() == btnDir) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory( new File(".") );
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				textDir.setText( chooser.getSelectedFile().getAbsolutePath() );
		}
		if(ae.getSource() == btnEHowNet) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory( new File(".") );
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				textEHowNet.setText( chooser.getSelectedFile().getAbsolutePath() );
		}
		if(ae.getSource() == btnSearchNext) {
			labelWarning.setText("");
			List<TreePath> pathList = this.find( textSearch.getText() );
			if( pathList.isEmpty() ) {
				labelWarning.setText("Keyword not Found");
				return;
			}
			searchIdx = ( (searchIdx + 1) >= pathList.size() ? 0 : (searchIdx + 1) );
	        tree.setSelectionPath( pathList.get(searchIdx) );
	        tree.scrollPathToVisible( pathList.get(searchIdx) );
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent ie) {
		if(ie.getStateChange() == ItemEvent.DESELECTED)	this.collapseAllNodes(tree);	
		else	this.expandAllNodes(tree);
	}
	
	// collapse all the nodes
	private void collapseAllNodes(JTree _tree) {
		int rowCount = _tree.getRowCount();
		for(int i = rowCount - 1 ; i > 0 ; i--)	_tree.collapseRow(i);	
	}
	
	// expand all the nodes
	private void expandAllNodes(JTree _tree) {
		int rowCount = _tree.getRowCount(), startIdx = 0;
		while(startIdx < rowCount) {
			_tree.expandRow(startIdx++);
			if(startIdx == rowCount)	rowCount = _tree.getRowCount(); 
		}
	}
	
	// get the path of a specific node
	private List<TreePath> find(String _concept) {
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
	    List<TreePath> pathList = new ArrayList<TreePath>();
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
	    while( e.hasMoreElements() ) {
	        DefaultMutableTreeNode node = e.nextElement();
	        if (node.toString().equalsIgnoreCase(_concept))
	            pathList.add( new TreePath( node.getPath() ) );
	    }
	    return pathList;
	}

}