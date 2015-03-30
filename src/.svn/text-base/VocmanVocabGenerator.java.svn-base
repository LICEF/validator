import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.xpath.XPath;


public class VocmanVocabGenerator {

	protected static File[] files;
	public static Namespace SCHNS = Namespace.getNamespace("sch","http://purl.oclc.org/dsdl/schematron");
	public static Namespace ZTHESNS = Namespace.getNamespace("zthes","http://zthes.z3950.org/");

	//	private static String baseFolder = "/work/ASPECT/vocab/";
	//	private static String instanceFolder = "iso/MACE/";
	protected static String inputFolder = "/work/projects/ASPECT/vocab-deprecated/";
	protected static String outputFolder = "/work/validationService/resources/schematron/iso/ASPECT/vocab-deprecated/";

	protected static XPath vbePath;
	protected static XPath termXpath;

	static{
		try {
			vbePath = XPath.newInstance("//vocabulary/vbe");
//			termXpath = XPath.newInstance("//Zthes/term");
			termXpath = XPath.newInstance("//zthes:Zthes/zthes:term");
			termXpath.addNamespace(ZTHESNS);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		if(args.length > 0) {
			inputFolder = args[0];
			if(args.length > 1) {
				outputFolder = args[1];
			}
		}
		VocmanVocabGenerator gen = new VocmanVocabGenerator();
		gen.generate();
	}

	public void generate() {
		try {
			readVocabFiles();
			clean();
			
			generateVocabFiles();
			//			generateRules();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void clean() {
		File dir = new File(outputFolder);

		if(dir.exists()) {

			for(File file : dir.listFiles()) {
				file.delete();
			}
			dir.delete();
		}
		dir.mkdirs();
	}

	protected void generateVocabFiles() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();

		//		XPath vbeAuthority = XPath.newInstance("//vocabulary/vbe/authority");
		//		XPath vbeIdentifier = XPath.newInstance("//vocabulary/vbe/vocabIdentifier");
		XPath vocabSourceName = XPath.newInstance("//vocabulary/sourceName");
		//		XPath vocabNsXpath = XPath.newInstance("//vocabulary/vbe/vocabXpath/ns");
		XPath vocabP = XPath.newInstance("//vocabulary/vbe/vocab");
		if(files != null)for(File file : files) {
			String filename = file.getName();
			if(!file.isHidden() && filename.endsWith(".xml")) {
				Document doc = builder.build(file);

				Element sourceName = (Element) vocabSourceName.selectSingleNode(doc.getRootElement());

				List<Element> vbes = vbePath.selectNodes(doc.getRootElement());
				Element vocab = null;
				Element vocabPreEl = (Element)vocabP.selectSingleNode(doc.getRootElement());
				if(vocabPreEl != null) {
					vocab = vocabPreEl;
				}else {
					vocab = new Element("vocab");
					doc.getRootElement().addContent(vocab);
				}
				for(Element vbe : vbes) {
					String vbeAuthorityString = vbe.getChildTextTrim("authority");//((Element)vbeAuthority.selectSingleNode(doc.getRootElement())).getValue();
					String vbeIdentifierString = vbe.getChildTextTrim("vocabIdentifier");//((Element)vbeIdentifier.selectSingleNode(doc.getRootElement())).getValue();
					//					Element vocabXpathEl = vbe.getChild("vocabXpath");//(Element)vocabXpath.selectSingleNode(doc.getRootElement());
					//					if(vocabXpathEl!= null) {
					//						String vocabXpathString = vocabXpathEl.getChildTextTrim("path");
					//						termXpath = XPath.newInstance(vocabXpathString);
					//						List<Element> uriList = vocabXpathEl.getChildren("ns");
					//						String prefix = "";
					//						for(Object uriNode : uriList) {
					//							String uri = ((Element)uriNode).getChildText("uri");
					//							prefix = ((Element)uriNode).getChildText("prefix");
					//							termXpath.addNamespace(prefix, uri);
					//						}
					//					}

					createVocabNode(vocab,vbeAuthorityString,vbeIdentifierString,termXpath, sourceName);
				}
				if(vocab.getChildren().size() > 0) {
					Format prettyFormat = Format.getPrettyFormat();
					prettyFormat.setOmitDeclaration(false);
					prettyFormat.setOmitEncoding(false);
					IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(doc,prettyFormat), outputFolder + File.separator + filename);
				}
			}
		}
	}

	protected Element createVocabNode(Element vocab, String vbeAuthorityString, String vbeIdentifierString, XPath termXpath, Element sourceName) {
		try {
			//http://aspect.vocman.com/vbe/linkeddata/LOM/aggregationLevelValues.zthes
			String vocabUrl = "http://aspect.vocman.com/vbe/linkeddata/"+vbeAuthorityString+"/"+vbeIdentifierString+".zthes";
//			String vocabUrl = "http://aspect.vocman.com/vbe/user/view/"+vbeAuthorityString+"/"+vbeIdentifierString+".zthes_1_1";
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
				Document vocabXml = docBuilder.build(http.getInputStream());
				List<Element> nodes = termXpath.selectNodes(vocabXml);
				if(nodes.size() == 0) {
					System.err.println("Error : No terms found in " + vbeAuthorityString + "." + vbeIdentifierString);
				}
				String elName = vbeIdentifierString;
				if(sourceName != null && sourceName.getTextTrim().equalsIgnoreCase(SchematronGeneratorV2.validationXNONE)) {
					elName = SchematronGeneratorV2.singleValue;
				}

				for(Element node : nodes) {
					String termStatus = node.getChildText("termStatus",ZTHESNS);
					if(termStatus == null || (!termStatus.equalsIgnoreCase("deactivated") && !termStatus.equalsIgnoreCase("deleted"))) {
						Element el = JDomUtils.newElement(elName, vocab,null);
						String childText = node.getChildText("termId",ZTHESNS);
						el.setText(childText);
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

	protected void readVocabFiles() {
		File dir = new File(inputFolder);

		if(dir.exists() && dir.listFiles().length > 0) {

			files = dir.listFiles();
		} else {
			System.err.println(inputFolder + " does not exist or is empty !");
		}
	}
}
