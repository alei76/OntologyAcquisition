package ehownet;

import java.util.ArrayList;
import java.util.List;

public class EHowNetNode {
	
	public enum NodeType {WORD, TAXONOMY}
	
	private Integer sid;
	private String nodeName, pos, ehownet;
	private NodeType nodeType;
	private EHowNetNode hypernym; 
	private	List<EHowNetNode> hyponymList;

	public EHowNetNode() {
		sid = null;
		nodeName = new String("");
		pos = null;
		ehownet = new String("");
		nodeType = NodeType.TAXONOMY;
		hypernym = null;
		hyponymList = new ArrayList<EHowNetNode>();		
	}
	
	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType _nodeType) {
		this.nodeType = _nodeType;
	}

	public EHowNetNode getHypernym() {
		return hypernym;
	}

	public void setHypernym(EHowNetNode _hypernym) {
		this.hypernym = _hypernym;
	}

	public List<EHowNetNode> getHyponymList() {
		return hyponymList;
	}

	public void addHyponym(EHowNetNode _hyponym) {
		this.hyponymList.add(_hyponym);
	}
	
	public int getSid() {
		if(sid == null)	return 0;
		return sid;
	}
	
	public void setSid(int _sid) {
		this.sid = _sid;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String _nodeName) {
		this.nodeName = _nodeName;
	}
	
	public String getPos() {
		if(pos == null)	return new String("");
		return pos;
	}
	
	public void setPos(String _pos) {
		this.pos = _pos;
	}
	
	public String getEhownet() {
		return ehownet;
	}
	
	public void setEhownet(String _ehownet) {
		this.ehownet = _ehownet;
	}

	@Override
	public String toString() {
		String output = "";
		if(nodeType == NodeType.TAXONOMY)	output = "taxonomy(" + ehownet + ")";
		else {
			output = "word(" + sid + "," + nodeName + "," + pos + "," + ehownet + ")";  
		}
		return output;
	}
	
}
