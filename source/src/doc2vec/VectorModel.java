package doc2vec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tool.OntologyNode;

public class VectorModel {

	private List<String> features;
	private Map< String, List<Double> > vectors;
	
	public VectorModel(List<OntologyNode> _concepts) {
		features = new ArrayList<String>();
		for(OntologyNode n : _concepts)	features.add( n.getConcept() );
		vectors = new HashMap< String, List<Double> >();
	}
	
	protected void addVector(String _filename, List<Double> _vector) {
		vectors.put(_filename, _vector);
	}
	
	// return a list of strings, denoting the features, an empty list will be returned if no features
	public List<String> getFeatures() {
		return features;
	}
	
	// return the dimension of the vector
	public int getDimension() {
		return features.size();
	}
	
	// get all the document vectors, where key = filename and value = vector, return an empty map if there's no result
	public Map< String, List<Double> > getDocVectors() {
		return vectors;
	}
	
	// get the vector of a specific document, return an empty list if the file does not exist
	public List<Double> getDocVector(String _filename) {
		List<Double> vector = null;
		try {
			vector = vectors.get( new File(_filename).getCanonicalPath() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(vector == null)	return new ArrayList<Double>();
		return vector;
	}
	
}
