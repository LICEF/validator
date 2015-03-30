import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import org.ariadne.util.JDomUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;


public class IcoperVocabGenerator extends VocmanVocabGenerator {

	public static Namespace SCHNS = Namespace.getNamespace("sch","http://purl.oclc.org/dsdl/schematron");
//	public static Namespace ZTHESNS = Namespace.getNamespace("zthes","http://zthes.z3950.org/");
	
	//	private static String baseFolder = "/work/ASPECT/vocab/";
	//	private static String instanceFolder = "iso/MACE/";
	private static HashMap<String, String> sourceMapping = new HashMap<String, String>();


	public static void main(String[] args) {

		sourceMapping.put("LOM", "LOMv1.0");
		sourceMapping.put("LRE", "LREv3.0");
		sourceMapping.put("ICOPER", "ICOPERv1.0");

		if(args.length > 0) {
			inputFolder = args[0];
			if(args.length > 1) {
				outputFolder = args[1];
			}
		}
		VocmanVocabGenerator gen = new IcoperVocabGenerator();
		gen.generate();
	}

	protected Element createVocabNode(Element vocab, String vbeAuthorityString, String vbeIdentifierString, XPath termXpath, Element sourceName) {
		try {
			String vocabUrl = "http://aspect.vocman.com/vbe/linkeddata/"+vbeAuthorityString+"/"+vbeIdentifierString+".zthes";

			//			String vocabUrl = "file:///work/ASPECT/vocabSourcesVBE/"+vbeAuthorityString+"-"+vbeIdentifierString+".xml";
			try {

				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication ("bramv", "ariadne".toCharArray());
					}
				});

				URL url = new URL(vocabUrl);
				URLConnection http = url.openConnection();

				SAXBuilder docBuilder = new SAXBuilder();
				//			BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
				//			  StringBuffer buffer = new StringBuffer();
				//			  String line;
				//			  while ((line = in.readLine()) != null) {
				//			    buffer.append(line + "\n");
				//			  }	

				XPath thesAuthorityXpath = XPath.newInstance("//thes/thesNote[@label=\"authority\"]");
//				thesAuthorityXpath.addNamespace(ZTHESNS);
				XPath termAuthorityXpath = XPath.newInstance("//term/termNote[@label=\"authority\"]");
//				termAuthorityXpath.addNamespace(ZTHESNS);
				
				Document vocabXml = docBuilder.build(http.getInputStream());

				Element thesAuth = (Element)thesAuthorityXpath.selectSingleNode(vocabXml);

				List<Element> nodes = termXpath.selectNodes(vocabXml); //List of Zthes terms
				if(nodes.size() == 0) {
					System.err.println("Error : No terms found in " + vbeAuthorityString + "." + vbeIdentifierString);
				}

				for(Element node : nodes) {
					String termStatus = node.getChildText("termStatus");
					if(termStatus == null || (!termStatus.equalsIgnoreCase("deactivated") && !termStatus.equalsIgnoreCase("deleted"))) {
						Document termDoc = new Document();
						node.detach();
						termDoc.addContent(node);
						Element termAuth = (Element)termAuthorityXpath.selectSingleNode(termDoc); 
						String source = "LOMv1.0";
						if(termAuth != null) { source = termAuth.getText();}
						else if (thesAuth != null) { source = thesAuth.getText();}

						if(sourceMapping.containsKey(source)) {source = sourceMapping.get(source);}
						if(sourceName != null && sourceName.getTextTrim().equalsIgnoreCase(SchematronGeneratorV2.validationXNONE)) {
							source = SchematronGeneratorV2.singleValue;
						}
						Element el = JDomUtils.newElement(source, vocab,null);
						el.setText(node.getChildText("termId"));
					}
				}
			} catch (FileNotFoundException e) {
				System.err.println("Not Found : " + vocabUrl);
			}
			return vocab;
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage() + " " + vbeAuthorityString + " " + vbeIdentifierString + " " + termXpath);
		} catch (IOException e) {
			System.err.println(e.getMessage() + " " + vbeAuthorityString + " " + vbeIdentifierString + " " + termXpath);
		} catch (JDOMException e) {
			System.err.println(e.getMessage() + " " + vbeAuthorityString + " " + vbeIdentifierString + " " + termXpath);
		}
		return null;
	}
}
