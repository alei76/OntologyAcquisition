package tool;

import java.util.ArrayList;
import java.util.List;

public class OntologyNode {
	
    private String concept;
    private List<OntologyNode> categories;
    private List<String> attr;
    private OntologyNode hypernym;
    
    public OntologyNode(String _concept) {
    	concept = _concept;
    	categories = new ArrayList<OntologyNode>();
    	attr = new ArrayList<String>();
    	hypernym = null;
    }
    
    public void addCategory(OntologyNode _node) {
    	categories.add(_node);
    	_node.hypernym = this;
    }
    
    public void addAttr(String _attr) {
    	attr.add(_attr);
    }
    
    public String getConcept() {
    	return concept;
    }
    
    public List<OntologyNode> getCategories() {
    	return categories;
    }
    
    public List<String> getAttr() {
    	return attr;
    }
    
    public OntologyNode getHypernym() {
    	return hypernym;
    }
    
    @Override
	public String toString() {
		String output = concept + " ( ";
		for(String s : attr)	output += (s + " ");
		output += ")";
		return output;
	}
    
}