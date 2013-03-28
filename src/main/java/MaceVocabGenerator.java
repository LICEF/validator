import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.xpath.XPath;


public class MaceVocabGenerator {

	private static File[] files;
	private static HashMap<String, String> vocabs = new HashMap<String, String>();
	private static HashMap<String, String> namespaces = new HashMap<String, String>();
	public static Namespace SCHNS = Namespace.getNamespace("sch","http://purl.oclc.org/dsdl/schematron");

	//	private static String baseFolder = "/work/ASPECT/vocab/";
	//	private static String instanceFolder = "iso/MACE/";
	private static String inputFolder = "/work/projects/MACE/vocab/";
	private static String outputFolder = "/work/validationService/schematron/iso/MACE/vocab/";
	private static String vocabUrl = "file:///work/projects-archive/MACE/vocabSources/MACE-classification.xml";
	public static void main(String[] args) {

		if(args.length > 0) {
			inputFolder = args[0];
			if(args.length > 1) {
				outputFolder = args[1];
				if(args.length > 2) {
					vocabUrl = args[2];
				}
			}
		}
		
		try {
			readVocabFiles();
			//deleteFiles();
			generateVocabFiles();
			//			generateRules();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 1;
	}

	private static void deleteFiles() {
		File dir = new File(outputFolder);

		if(dir.exists()) {

			for(File file : dir.listFiles()) {
				file.delete();
			}
		}
		
	}

	private static void generateVocabFiles() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		XPath vocabXpath = XPath.newInstance("//vocabulary/sourceVocab/vocabXpath/path");
		XPath vocabNsXpath = XPath.newInstance("//vocabulary/sourceVocab/vocabXpath/ns");
		XPath vocabP = XPath.newInstance("//vocabulary/sourceVocab/vocab");
		if(files != null)for(File file : files) {
			String filename = file.getName();
			if(filename.endsWith(".xml")) {
				Document doc = builder.build(file);

				Element vocabPathEl = (Element)vocabXpath.selectSingleNode(doc.getRootElement());
				String vocabXpathString = "";
				XPath termXpath = null;
				if(vocabPathEl!= null) {
					vocabXpathString = vocabPathEl.getValue();
					termXpath = XPath.newInstance(vocabXpathString);
					List uriList = vocabNsXpath.selectNodes(doc.getRootElement());
					String prefix = "";
					for(Object uriNode : uriList) {
						String uri = ((Element)uriNode).getChildText("uri");
						prefix = ((Element)uriNode).getChildText("prefix");
						termXpath.addNamespace(prefix, uri);
					}
				}
				Element vocab = createVocabNode(termXpath);
				if(vocab != null || vocabP.selectSingleNode(doc.getRootElement()) != null) {
					if(vocab != null)doc.getRootElement().addContent(vocab);
					Format prettyFormat = Format.getPrettyFormat();
					prettyFormat.setOmitDeclaration(false);
					prettyFormat.setOmitEncoding(false);
					IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(doc,prettyFormat), outputFolder + File.separator + filename);
				}
			}
		}
	}

	private static Element createVocabNode(XPath termXpath) {
		try {
			Element vocab = null;
						
			try {
				
				vocab = new Element("vocab");
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
					System.out.println("Error : No terms found in " + termXpath);
					vocab = null;
				}
				for(Object node : nodes) {
					String text = null;
					try {
						Element nodeEl = (Element)node;
						text = nodeEl.getText();
					} catch (Exception e) {
						Attribute nodeEl = (Attribute)node;
						text = nodeEl.getValue();
					}
					if(text != null) {
						Element el = JDomUtils.newElement(text, vocab,null);
						el.setText(text);
					}
				}
			} catch (FileNotFoundException e) {
				System.err.println("Not Found : " + vocabUrl);
			}
			return vocab;
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage() + termXpath);
		} catch (IOException e) {
			System.err.println(e.getMessage() + termXpath);
		} catch (JDOMException e) {
			System.err.println(e.getMessage() + termXpath);
		}
		return null;
	}

	private static void readVocabFiles() {
		File dir = new File(inputFolder);

		if(dir.exists() && dir.listFiles().length > 0) {

			files = dir.listFiles();
		} else {
			System.err.println(inputFolder + " does not exist or is empty !");
		}
	}
}
