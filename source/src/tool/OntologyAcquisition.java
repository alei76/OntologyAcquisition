package tool;

import java.io.File;

import ehownet.EHowNetTree;

public class OntologyAcquisition {
	
	private String rootConcept, dirPath, eHowNetPath;
	private TextController tc;
	private TfDf tfdf;
	private OntologyController oc;
	
	public OntologyAcquisition(String _rootConcept, String _dirPath, String _eHowNetPath) {
		rootConcept = _rootConcept; 
		dirPath = _dirPath;
		eHowNetPath = _eHowNetPath;
		if(dirPath.endsWith("\\") == false && dirPath.endsWith("/") == false)	dirPath = dirPath + "/";
	}
	
	// set root concept
	public void setRootConcept(String _rootConcept) {
		rootConcept = _rootConcept;
	}
	
	// set directory path
	public void setDirPath(String _dirPath) {
		dirPath = _dirPath;
		if(dirPath.endsWith("\\") == false && dirPath.endsWith("/") == false)	dirPath = dirPath + "/";
	}
	
	// set the path of the EHOWNET
	public void setEHowNetPath(String _path) {
		eHowNetPath = _path;
	}
	
	// start and return the root of the ONOLOGY
	public OntologyNode start() {
		tc = new TextController( EHowNetTree.getInstance(eHowNetPath) );
		tfdf = new TfDf(tc);
		oc = new OntologyController( rootConcept, tfdf, EHowNetTree.getInstance(eHowNetPath) );
		this.readDir(dirPath);
		tfdf.analyze();
		oc.build();
		return oc.getRoot();
	}
	
	// search for a specific concept
	public OntologyNode searchConcept(String _concept) {
		if(oc == null)	return null;
		return oc.getNode(_concept);
	}
	
	// dump the ONTOLOGY to result.xls
	public void dump() {
		if(oc != null)	oc.printAll();
	}
	
	// get the frequency of the text
	public int getTermFreq(String _text) {
		if(tfdf == null)	return 0;
		return tfdf.getTF(_text);
	}
	
	// get the IDF value of the text
	public int getDocFreq(String _text) {
		if(tfdf == null)	return 0;
		return tfdf.getDF(_text);
	}

	// read the file in the directory
	private void readDir(String _dirPath) {
		File folder = new File(_dirPath);
		File[] listOfFiles = folder.listFiles();
		for(File file : listOfFiles)
			if ( file.isFile() )	tc.read( _dirPath + file.getName() );
			else if( file.isDirectory() )	this.readDir( _dirPath + file.getName() + "/" );
	}
}
