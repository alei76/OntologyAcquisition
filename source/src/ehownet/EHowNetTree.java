package ehownet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ehownet.EHowNetNode.NodeType;

public class EHowNetTree {

	private EHowNetNode root;
	private Map< String, List<EHowNetNode> > word2Nodes;
	private static EHowNetTree instance;
	private String filename;
	
	public EHowNetTree(String _filename) {
		filename = _filename;
		root = new EHowNetNode();
		root.setNodeName("TopNode");
		root.setEhownet("TopNode");
		word2Nodes = new HashMap<String, List<EHowNetNode> >();
		this.init(_filename);
	}
	
	// return the instance if it exists
	public static EHowNetTree getInstance(String _filename) {
		if( instance == null || !instance.filename.equals(_filename) )	instance = new EHowNetTree(_filename);
		return instance;
	}
	
	// initialize with filename
	private void init(String _filename) {
		try {
			BufferedReader br = new BufferedReader( new FileReader(_filename) );
			String line = null;
			EHowNetNode curNode = root;
			int curLevel = 0;
			while( ( line = br.readLine() ) != null ) {
				if( !line.startsWith(" ") && !line.startsWith(",") )	continue;
				int newLevel = line.indexOf('|');
				for(int i = 0 ; i <= (curLevel - newLevel) ; i++)	curNode = curNode.getHypernym();
				EHowNetNode newNode = new EHowNetNode();
				newNode.setHypernym(curNode);
				curNode.addHyponym(newNode);
				String[] lineSplit = line.split("\\s+");
				if(line.charAt(0) == ',') {
					newNode.setEhownet(lineSplit[1]);
					if(lineSplit[1].split("\\|").length > 1)	newNode.setNodeName(lineSplit[1].split("\\|")[1]);
					else	newNode.setNodeName(lineSplit[1]);
				}
				else {
					newNode.setNodeType(NodeType.WORD);
					newNode.setSid( Integer.parseInt(lineSplit[2]) );
					newNode.setNodeName(lineSplit[3]);
					if(lineSplit.length > 5) {
						newNode.setPos(lineSplit[4]);
						newNode.setEhownet(lineSplit[5]);
					}
					else if(lineSplit.length > 4)	newNode.setEhownet(lineSplit[4]);
				}
				List<EHowNetNode> nodes = word2Nodes.get( newNode.getNodeName() );
				if(nodes == null)	 nodes = new ArrayList<EHowNetNode>();
				nodes.add(newNode);
				word2Nodes.put(newNode.getNodeName(), nodes);
				curNode = newNode;
				curLevel = newLevel;
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error: File " + _filename + " not found");
			e.printStackTrace();
		}
	}
	
	// search for a specific word
	public List<EHowNetNode> searchWord(String _name) {
		List<EHowNetNode> outputList = word2Nodes.get(_name);
		if(outputList == null)	outputList = new ArrayList<EHowNetNode>();
		return outputList;
	}
	
}
