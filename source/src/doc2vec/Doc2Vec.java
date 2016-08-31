package doc2vec;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import ehownet.EHowNetTree;
import tool.OntologyController;
import tool.OntologyNode;
import tool.TextController;
import tool.TextController.Sentence;
import tool.TextController.TextFile;
import tool.TextController.Word;
import tool.TfDf;

public class Doc2Vec {

	private String rootConcept, dirPath, eHowNetPath;
	private int dimension;
	private TextController tc;
	private TfDf tfdf;
	private OntologyController oc;
	private VectorModel model;
	private List<OntologyNode> concepts;
	private Map< String, Set<String> > keywords;
	
	// initialize with root concept, directory, eHowNetpath and vector size 
	public Doc2Vec(String _rootConcept, String _dirPath, String _eHowNetPath, int _dimension) {
		dimension = _dimension;
		rootConcept = _rootConcept; 
		dirPath = _dirPath;
		eHowNetPath = _eHowNetPath;
		if(dirPath.endsWith("\\") == false && dirPath.endsWith("/") == false)	dirPath = dirPath + "/";
	}
	
	// build the model with specific concepts
	public VectorModel build(List<String> _concepts) {
		tc = new TextController( EHowNetTree.getInstance(eHowNetPath) );
		tfdf = new TfDf(tc);
		oc = new OntologyController( rootConcept, tfdf, EHowNetTree.getInstance(eHowNetPath) );
		this.readDir(dirPath);
		tfdf.analyze();
		oc.build();
		concepts = new ArrayList<OntologyNode>();
		for(String s : _concepts) {
			OntologyNode node = oc.getNode(s);
			if(node != null)	concepts.add(node);
		}
		this.extractKeywords();
		model = new VectorModel(concepts);
		for( TextFile f : tc.getTextFiles() )	this.analyzeDoc(f);
		return model;
	}
	
	// build the model
	public VectorModel build() {
		tc = new TextController( EHowNetTree.getInstance(eHowNetPath) );
		tfdf = new TfDf(tc);
		oc = new OntologyController( rootConcept, tfdf, EHowNetTree.getInstance(eHowNetPath) );
		this.readDir(dirPath);
		tfdf.analyze();
		oc.build();
		this.generateConceptViews();
		this.extractKeywords();
		model = new VectorModel(concepts);
		for( TextFile f : tc.getTextFiles() )	this.analyzeDoc(f);
		return model;
	}
	
	// read the file in the directory
	private void readDir(String _dirPath) {
		File folder = new File(_dirPath);
		File[] listOfFiles = folder.listFiles();
		for(File file : listOfFiles)
			if ( file.isFile() )	tc.read( _dirPath + file.getName() );
			else if( file.isDirectory() )	this.readDir( _dirPath + file.getName() + "/" );
	}
	
	// find important concepts
	private void generateConceptViews() {
		concepts = new ArrayList<OntologyNode>();
		Queue<OntologyNode> agenda = new PriorityQueue<OntologyNode>( dimension, new NodeComparator() );
		agenda.add( oc.getRoot() );
		while(true) {
			OntologyNode top = agenda.poll();
			if( top.getCategories().isEmpty() )	break;
			agenda.addAll( top.getCategories() );			
		}
		for(int i = 0 ; i < dimension ; i++) {
			if( agenda.isEmpty() )	break;
			concepts.add( agenda.poll() );
		}
	}
	
	// extract keywords of the concepts
	private void extractKeywords() {
		keywords = new HashMap< String, Set<String> >();
		for(OntologyNode c : concepts) {
			Set<String> wordset = new HashSet<String>();
			Queue<OntologyNode> queue = new LinkedList<OntologyNode>();
			queue.add(c);
			while(queue.isEmpty() == false) {
				OntologyNode curNode = queue.poll();
				wordset.addAll( curNode.getAttr() );
				wordset.add( curNode.getConcept() );
				queue.addAll( curNode.getCategories() );
			}
			keywords.put(c.getConcept(), wordset);
		}
	}
	
	// analyze a document
	private void analyzeDoc(TextFile _file) {
		int dim = concepts.size();
		List<Integer> vecInt = new ArrayList<Integer>();
		for(int i = 0 ; i < dim ; i++)	vecInt.add(0);
		List<Sentence> sentences =  _file.getSentences();
		for(Sentence s : sentences) {
			List<Word> words = s.getWords();
			for(Word w : words)
				for(int i = 0 ; i < dim ; i++)
					if( keywords.get( concepts.get(i).getConcept() ).contains( w.getText() ) )	vecInt.set(i, vecInt.get(i) + 1);
		}
		List<Double> vec = new ArrayList<Double>();
		int sum = 0;
		for(Integer i : vecInt)	sum += i;
		for(Integer i : vecInt) {
			if(sum == 0)	vec.add(0d);
			else	vec.add( (double)i / (double)sum );
		}
		model.addVector(_file.getFilename(), vec);
	}
	
	// comparator for ONTOLOGY node
	private class NodeComparator implements Comparator<OntologyNode>
	{
		
	    @Override
	    public int compare(OntologyNode x, OntologyNode y)
	    {
	    	if(tfdf == null)	return 0;
	    	int xdf = tfdf.getDF( x.getConcept() ), ydf = tfdf.getDF( y.getConcept() );
	        if(xdf < ydf)	return 1;
	        if(xdf > ydf)	return -1;
	        return 0;
	    }
	    
	}
	
}
