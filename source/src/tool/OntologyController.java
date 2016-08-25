package tool;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ehownet.EHowNetNode;
import ehownet.EHowNetTree;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tool.TextController.Sentence;
import tool.TextController.Word;

public class OntologyController {
	
	private OntologyNode root;
	private TfDf tfdf;
	private EHowNetTree eHowNetTree;
	private Map<String, OntologyNode> word2Node;
	private static int row;
	
	public OntologyController(String _rootConcept, TfDf _tfdf, EHowNetTree _eHowNetTree) {
        root = new OntologyNode(_rootConcept);
        tfdf = _tfdf;
        eHowNetTree = _eHowNetTree;
        word2Node = new HashMap<String, OntologyNode>();
    }
	
	// build the prototype of the ONTOLOGY
	public void build() {
		Set<String> existConcepts = new HashSet<String>();
		Queue<OntologyNode> queue = new LinkedList<OntologyNode>();
		existConcepts.add( root.getConcept() );
		queue.add(root);
		while(queue.isEmpty() == false) {
			OntologyNode curNode = queue.poll();
			word2Node.put(curNode.getConcept(), curNode);
			List<EHowNetNode> results = eHowNetTree.searchWord( curNode.getConcept() );
			for(EHowNetNode r : results) {
				List<EHowNetNode> children = r.getHyponymList();
				for(EHowNetNode c : children) {
					if( tfdf.getDF( c.getNodeName() ) < 2 || existConcepts.contains( c.getNodeName() ) || c.getNodeName().length() < 2 )	continue;
					OntologyNode newNode = new OntologyNode( c.getNodeName() );
					curNode.addCategory(newNode);
					existConcepts.add( c.getNodeName() );
					queue.add(newNode);
				}
			}
			List<Sentence> sentences = tfdf.getSentences( curNode.getConcept() );
			Set<String> newCategories = new HashSet<String>();
			for(Sentence s : sentences) {
				List<Word> words = s.getWords();
				for(int idx = words.size() - 1 ; idx >= 0 ; idx--) {
					Word curWord = words.get(idx);
					if( curWord.getText().equals( curNode.getConcept() ) ) this.checkRules(words, idx, newCategories);
				}
			}
			for(String s : newCategories) {
				if(s.length() < 2 || tfdf.getDF(s) < 2)	continue;
				if(!existConcepts.contains(s) && tfdf.getDF(s) < tfdf.getDF( curNode.getConcept() ) && tfdf.getDF(s) > tfdf.getDF( curNode.getConcept() ) / 5) {
					OntologyNode newNode = new OntologyNode(s);
					curNode.addCategory(newNode);
					existConcepts.add(s);
					queue.add(newNode);
				}
				else	curNode.addAttr(s);
			}
		}
	}
	
	// check the extraction rule
	private void checkRules(List<Word> _words, int _idx, Set<String> _newCategories) {
		if( _words.get(_idx).getTag().equals("Na") ) {
			if(_idx > 1) {
				String tags = _words.get(_idx - 2).getTag() + "+" + _words.get(_idx - 1).getTag();
				if( tags.equals("Nc+Nc") || tags.equals("Na+Na") || tags.equals("VH+Na") )
					_newCategories.add( new String( _words.get(_idx - 2).getText() + _words.get(_idx - 1).getText() ) );
			}
			else if(_idx > 0) {
				String tags = _words.get(_idx - 1).getTag();
				if( new String("ANaNbNcNcdVH").contains(tags) )
					_newCategories.add( new String( _words.get(_idx - 1).getText() ) );
			}
			if(_idx < _words.size() - 2) {
				String tags = _words.get(_idx + 1).getTag() + "+" + _words.get(_idx + 2).getTag();
				if( tags.equals("DE+Na") )
					_newCategories.add( new String( _words.get(_idx + 2).getText() ) );
			}
			else if(_idx < _words.size() - 1) {
				String tags = _words.get(_idx + 1).getTag();
				if( tags.equals("Na") || tags.equals("Nc") )
					_newCategories.add( new String( _words.get(_idx + 1).getText() ) );
			}
		}
		else if ( _words.get(_idx).getTag().equals("Nc") ) {
			if(_idx > 2) {
				String tags = _words.get(_idx - 3).getTag() + "+" + _words.get(_idx - 2).getTag() + "+" + _words.get(_idx - 1).getTag();
				if( tags.equals("Nc+Nc+Na") || tags.equals("Nc+Nc+VC") )
					_newCategories.add( new String( _words.get(_idx - 3).getText() + _words.get(_idx - 2).getText() + _words.get(_idx - 1).getText() ) );
			}
			else if(_idx > 1) {
				String tags = _words.get(_idx - 2).getTag() + "+" + _words.get(_idx - 1).getTag();
				if( tags.equals("Na+Nb") || tags.equals("Nb+Na") || tags.equals("Nb+VH") || tags.equals("Nc+A") || tags.equals("Nc+FW") || tags.equals("Nc+Na") || tags.equals("Nc+Nb") || tags.equals("Nc+VC") )
					_newCategories.add( new String( _words.get(_idx - 2).getText() + _words.get(_idx - 1).getText() ) );
			}
			else if(_idx > 0) {
				String tags = _words.get(_idx - 1).getTag();
				if( new String("ANaNbNcNcdVH").contains(tags) )
					_newCategories.add( new String( _words.get(_idx - 1).getText() ) );
			}
			if(_idx < _words.size() - 2) {
				String tags = _words.get(_idx + 1).getTag() + "+" + _words.get(_idx + 2).getTag();
				if( tags.equals("Nc+Nc") )
					_newCategories.add( new String( _words.get(_idx + 1).getText() + _words.get(_idx + 2).getText() ) );
				else if( tags.equals("DE+Na") )
					_newCategories.add( new String( _words.get(_idx + 2).getText() ) );
			}
			else if(_idx < _words.size() - 1) {
				String tags = _words.get(_idx + 1).getTag();
				if( tags.equals("Na") || tags.equals("Nc") )
					_newCategories.add( new String( _words.get(_idx + 1).getText() ) );
			}
		}
	}
	
	// get the root of the ONTOLOGY
	public OntologyNode getRoot() {
		return root;
	}
	
	// search for a specific concept
	public OntologyNode getNode(String _concept) {
		return word2Node.get(_concept);
	}
	
	// dump the ONTOLOGY controller to excel
	public void printAll() {
		try {
			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale( new Locale("zh", "TW") );
			WritableWorkbook workbook = null;
			Workbook oldbook = new File("result.xls").exists()? Workbook.getWorkbook( new File("result.xls") ) : null;
			if(oldbook == null)	workbook = Workbook.createWorkbook(new File("result.xls") , wbSettings);
			else	workbook = Workbook.createWorkbook(new File("result2.xls"), oldbook);
			WritableSheet sheet = workbook.createSheet( root.getConcept(), workbook.getNumberOfSheets() );
			WritableCellFormat format = new WritableCellFormat( new WritableFont(WritableFont.createFont("Microsoft JhengHei UI"), 10) );
			row = 0;
			this.printNode(root, 0, sheet, format);
			workbook.write();
		    workbook.close();
		    File file = new File("result2.xls");
		    if( file.exists() ) {
		    	oldbook.close();
		    	new File("result.xls").delete();
		    	file.renameTo( new File("result.xls") );
		    }
		} catch (Exception e) {
			System.out.println("Error: Writing to Excel");
		}
	}
	
	// print a concept
	private void printNode(OntologyNode _node, int _column, WritableSheet _sheet, WritableCellFormat _format) throws RowsExceededException, WriteException {
		_sheet.addCell( new Label(_column, row, _node.getConcept(), _format) );
		String path = new String( _node.getConcept() );
		OntologyNode parent = _node.getHypernym();
		while(parent != null) {
			path = parent.getConcept() + "," + path;
			parent = parent.getHypernym();
		}
		_sheet.addCell( new Label(_column + 1, row, path, _format) );
		String attrs = new String("");
		for( String s : _node.getAttr() )	attrs = attrs + s + " ";
		_sheet.addCell( new Label(_column + 2, row, attrs, _format) );
		row++;
		for( OntologyNode n : _node.getCategories() )	this.printNode(n, _column + 1, _sheet, _format);
	}

}
