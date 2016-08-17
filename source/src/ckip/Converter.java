package ckip;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Converter {

	// convert the document to CKIP-tagged document
	public static void toCKIP(String _textFile, String _outputFile) {
		try {
			byte[] encoded = Files.readAllBytes( Paths.get(_textFile) );
			String text = new String(encoded, "UTF-8").replaceAll("\"", "");
//			System.out.println(text);
			Document postDoc = Jsoup.connect("http://sunlight.iis.sinica.edu.tw/cgi-bin/text.cgi")
					.postDataCharset("Big5").data("Submit", "%B0e%A5X").data("query", text).post();
			String id = null;
			for( Element meta : postDoc.select("meta") ) {
//				System.out.println( meta.attr("name") + " " + meta.attr("content") );
				Matcher matcher = Pattern.compile( "pool/[0-9]+\\.html").matcher(meta.attr("content") );
				if( matcher.find() )
					id = matcher.group(0).substring( 5, matcher.group(0).lastIndexOf('.') );
			}
			if(id != null) {
				Response res = Jsoup.connect("http://sunlight.iis.sinica.edu.tw/uwextract/show.php?id=" + id + "&type=tag").execute();
				Elements ele = Jsoup.parse( new String(res.bodyAsBytes(), "Big5") ).select("pre");
				BufferedWriter bw = new BufferedWriter( new FileWriter(_outputFile) );
				String buf = ele.text().replaceAll("-+\\n?", "");
				bw.write(buf);
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
