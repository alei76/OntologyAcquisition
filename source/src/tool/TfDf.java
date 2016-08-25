package tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tool.TextController.Sentence;
import tool.TextController.TextFile;
import tool.TextController.Word;

public class TfDf {
	
	private class TextData {
		
		private int tf;
		private int df;
		private ArrayList<Sentence> sentences;
		
		private TextData() {
			tf = 0;
			df = 0;
			sentences = new ArrayList<Sentence>();
		}
		
	}
	
	private TextController tc;
	private HashMap<String, TextData> data;
	
	public TfDf(TextController _tc) {
		tc = _tc;
		data = new HashMap<String, TextData>();
	}
	
	// start to retrieve the info about TF/IDF
	public void analyze() {
		ArrayList<TextFile> files = tc.getTextFiles();
		for(TextFile f : files) {
			HashSet<String> fileSet = new HashSet<String>(); 
			List<Sentence> sentences = f.getSentences();
			for(Sentence s : sentences) {
				List<Word> words = s.getWords();
				for(Word w : words) {
					TextData td = data.get( w.getText() );
					if(td == null)	td = new TextData();
					td.tf += 1;
					if(fileSet.contains( w.getText() ) == false) {
						fileSet.add( w.getText() );
						td.df += 1;
					}
					if(td.sentences.contains(s) == false)	td.sentences.add(s);
					data.put(w.getText(), td);
				}
			}
		}
	}
	
	// return the TF value of the text
	public int getTF(String _text) {
		TextData td = data.get(_text);
		return (td == null ? 0 : td.tf);
	}
	
	// return the DF value of the text
	public int getDF(String _text) {
		TextData td = data.get(_text);
		return (td == null ? 0 : td.df);
	}
	
	// return the sentences containing the text
	public ArrayList<Sentence> getSentences(String _text) {
		TextData td = data.get(_text);
		return (td == null ? new ArrayList<Sentence>() : td.sentences);
	}
	
}
