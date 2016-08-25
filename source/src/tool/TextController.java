package tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ehownet.EHowNetNode;
import ehownet.EHowNetTree;

public class TextController {
	
	public class Word {
		
		private String text;
		private String tag;
		
		public Word() {
			text = new String("");
			tag = new String("");
		}
		
		public Word(String _text, String _tag) {
			text = _text;
			tag = _tag;
		}
		
		public String getText()	{
			return text;
		}
		
		public String getTag() {
			return tag;
		}
		
	}
	
	public class Sentence {
		
		private ArrayList<Word> words;
		
		public Sentence() {
			words = new ArrayList<Word>();
		}
		
		public Boolean isEmpty() {
			return words.isEmpty();
		}
		
		public Boolean add(Word _word) {
			return words.add(_word);
		}
		
		public List<Word> getWords() {
			return words;
		}
		
	}
	
	public class TextFile {
		
		private ArrayList<Sentence> sentences;
		private String filename; 
		
		public TextFile() {
			sentences = new ArrayList<Sentence>();
			filename = new String("");
		}
		
		public TextFile(String _filename) {
			sentences = new ArrayList<Sentence>();
			filename = _filename;
		}
		
		public List<Sentence> getSentences() {
			return sentences;
		}
		
		public String getFilename() {
			return filename;
		}
		
	}
	
	private ArrayList<TextFile> textFiles;
	private EHowNetTree tree;
	
	public TextController(EHowNetTree _tree) {
		textFiles = new ArrayList<TextFile>();
		tree = _tree;
	}
	
	// initialize the text controller
	public void init() {
		textFiles.clear();
	}
	
	// read from a CKIP documents
	public void read(String _filename) {
		try {
			BufferedReader br = new BufferedReader( new FileReader(_filename) );
			TextFile curTextFile = new TextFile( new File(_filename).getCanonicalPath() );
			Sentence curSentence = new Sentence();
			String line = null;
			while( ( line = br.readLine() ) != null ) {
				String[] lineSplit = line.split("　");
				for(String s : lineSplit) {
					s = s.replaceAll("[()]", " ");
					String[] sSplit = s.split(" ");
					if( sSplit.length < 2 || sSplit[1].endsWith("CATEGORY") )	continue;
					List<EHowNetNode> results = tree.searchWord(sSplit[0]);
					for(EHowNetNode r : results)
						if(r.getPos().startsWith(sSplit[1]) && r.getHypernym().getHyponymList().size() < 20)
							sSplit[0] = r.getHypernym().getNodeName();
					curSentence.add( new Word(sSplit[0], sSplit[1]) );
				}
				if(curSentence.isEmpty() == false)	curTextFile.sentences.add(curSentence);
				curSentence = new Sentence();
			}
			br.close();
			if(curTextFile.sentences.isEmpty() == false)	textFiles.add(curTextFile);
		} catch (IOException e) {
			System.err.println("Error: File " + _filename + " not found");
			e.printStackTrace();
		}
	}
	
	// return the text files of the text controller
	public ArrayList<TextFile> getTextFiles() {
		return textFiles;
	}

	// return the words of the text controller
	public HashSet<String> getTexts() {
		HashSet<String> texts = new HashSet<String>();
		for(TextFile f : textFiles)
			for(Sentence s : f.sentences)
				for(Word w : s.words)	texts.add(w.text);
		return texts;
	}
	
	// return the number of the text files
	public int getNumOfFiles() {
		return textFiles.size();
	}
	
	// dump the text controller to standard out
	public void printAll() {
		int numWords = 0, numSentences = 0;
		for(TextFile f : textFiles) {
			System.out.println(f.filename);
			for(Sentence s : f.sentences) {
				for(Word w : s.words)	System.out.print(w.text + "(" + w.tag + ") ");
				numWords += s.words.size();
				System.out.println("");
			}
			numSentences += f.sentences.size();
			System.out.println("");
		}
		System.out.println("Total Sentences: " + numSentences + ", Total Words: " + numWords);
	}
	
}
