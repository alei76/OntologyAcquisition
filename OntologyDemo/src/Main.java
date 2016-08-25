import java.util.List;
import java.util.Map;

import ckip.Converter;
import doc2vec.Doc2Vec;
import doc2vec.VectorModel;
import ehownet.EHowNetNode;
import ehownet.EHowNetNode.NodeType;
import ehownet.EHowNetTree;
import tool.OntologyAcquisition;
import tool.OntologyNode;
import ui.UIFrame;

public class Main {
	
	public static void main(String[] args) {
		/*  
		 * EHowNet API
		 */
		// get a tree instance
		EHowNetTree tree = EHowNetTree.getInstance("./docs/ehownet_ontology.txt");
		// get the nodes with keyword (return an empty list if there's no result)
		List<EHowNetNode> results = tree.searchWord("法院");
		// test with a node
		EHowNetNode result = results.get(0);
		if(result.getNodeType() == NodeType.WORD) {
			System.out.println( "sid = " + result.getSid() );
			System.out.println( "nodeName = " + result.getNodeName() );
			System.out.println( "pos = " + result.getPos() );
			System.out.println( "ehownet = " + result.getEhownet() );
		}
		else if(result.getNodeType() == NodeType.TAXONOMY) {
			System.out.println( "nodeName = " + result.getNodeName() );
			System.out.println( "ehownet = " + result.getEhownet() );
		}
		// get the parent of the node (return NULL when the node is the root)
		EHowNetNode parent = result.getHypernym();
		// get the children of the parent (return an empty list if the node is a leaf)
		List<EHowNetNode> children = parent.getHyponymList();
		System.out.println(children + "\n");
		/* 
		 * CKIP API
		 */
		Converter.toCKIP("ckip_input.txt", "ckip_output.txt");
		/*  
		 * ONTOLOGY API
		 */
		// get an instance with root concept, CKIP directory and EHowNet
		OntologyAcquisition oa = new OntologyAcquisition("課綱", "./docs/ckip", "./docs/ehownet_ontology.txt");
		// start to build ONTOLOGY and return the root
		OntologyNode root = oa.start();
		System.out.println( root.getConcept() + "'s Term Frequency = " + oa.getTermFreq("課綱") );
		// get the node with specific concept (return NULL if there's no result)
		OntologyNode concept = oa.searchConcept("機構");
		// get the parent of the node (return NULL when the node is the root)
		OntologyNode parentConcept = concept.getHypernym();
		System.out.println( "機構's Parent Concept = " + parentConcept.getConcept() );
		// get the attributes of the concept (return an empty list if the node has no attributes)
		for( String s : concept.getAttr() )	System.out.print(s + " ");
		System.out.println();
		// get the sub concepts of the concept (return an empty list the node is a leaf)
		List<OntologyNode> subConcepts = concept.getCategories();
		System.out.println(subConcepts + "\n");
		// dump the ONTOLOGY into a new sheet in result.xls
		oa.dump();
		/*
		 * Document to Vector via ONTOLOGY
		 */
		// get an instance with root concept, CKIP directory, EHowNet and dimension of the output vector
		Doc2Vec d2v = new Doc2Vec("課綱", "./docs/ckip", "./docs/ehownet_ontology.txt", 5);
		// build the model
		VectorModel model = d2v.build();
		// check the features and valid dimension
		for( String feature : model.getFeatures() )	System.out.print(feature + " ");
		System.out.println( "\nValid Dimension = " + model.getDimension() );
		// get the map containing all the documents vectors (key = absolute path, value = list of Doubles)
		Map< String, List<Double> > vectors = model.getDocVectors();
		System.out.println( vectors.size() );
		// get the vector of a specific document (both path and absolute path are acceptable) 
		List<Double> vector = model.getDocVector("docs/ckip/97815.txt");
		System.out.println(vector);
		/*
		 * User Interface of the ONTOLOGY
		 */
		new UIFrame();
	}

}
