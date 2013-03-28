import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.xpath.XPath;


public class SchematronGeneratorV2 {

	private static File[] files;
	private static HashMap<String, String> vocabs = new HashMap<String, String>();
	private static HashMap<String, Vector <String>> contexts = new HashMap<String, Vector <String>>();
	private static HashMap<String, String> vocabNs = new HashMap<String, String>();
	private static HashMap<String, Element> vocabFiles = new HashMap<String, Element>();
	private static HashMap<String, String> namespaces = new HashMap<String, String>();
	public static Namespace SCHNS = Namespace.getNamespace("sch","http://purl.oclc.org/dsdl/schematron");
	
	public static final String singleValue = "singleValue";
	public static final String validationXNONE = "validation-X-NONE";

	private static String outputFile = "check-vocabs.sch";
	private static String vocabFolder = "vocab/";
	private static String baseFolder = "/work/validationService/resources/schematron/";

	private static String instanceFolder = "iso/MACE/";
	//	private static String instanceFolder = "iso/ASPECT/";
	private static String deployPath = "http://ariadne.cs.kuleuven.be/validation/schematron/";

	public static boolean includedValues = false;


	public static void main(String[] args) {

		if(args.length > 0) {
			instanceFolder = args[0];
			if(args.length > 1) {
				outputFile = args[1];
				if(args.length > 2) {
					vocabFolder = args[2];
					if(args.length > 3) {
						baseFolder = args[3];
					}
				}
			}
		}

		try {
			readVocabFiles();
			getNamespacesAndElements();
			generateRulesContexts();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 1;
	}

	private static void getNamespacesAndElements() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		XPath vocabXpath = XPath.newInstance("/vocabulary/xpath");
		XPath nsXpath = XPath.newInstance("/vocabulary/ns");
		for(File file : files) {
			String filename = file.getName();
			if(filename.endsWith(".xml")) {
				Document doc = builder.build(file);
				vocabFiles.put(filename,doc.getRootElement());
				String xpath = ((Element)vocabXpath.selectSingleNode(doc.getRootElement())).getValue();
				List uriList = nsXpath.selectNodes(doc.getRootElement());
				String prefix = "";
				for(Object uriNode : uriList) {
					String uri = ((Element)uriNode).getChildText("uri");
					prefix = ((Element)uriNode).getChildText("prefix");

					if(!namespaces.containsKey(uri)) {
						namespaces.put(uri, prefix);
					}

					prefix = namespaces.get(uri);

					xpath.replaceAll("/" + prefix + ":", "/" + namespaces.get(uri) + ":");
					//xpath = "/" + xpath;
				}
				vocabNs.put(filename, prefix);//TODO : this can cause troubles for the namespace of value and source !
				vocabs.put(filename, xpath);
				Vector<String> vocabVector = contexts.get(xpath);
				if(vocabVector == null) {
					vocabVector = new Vector<String>();
					contexts.put(xpath, vocabVector);
				}
				vocabVector.add(filename);
			}
		}
	}

	private static void generateRulesContexts() throws JDOMException, IOException {

		Element schema = new Element("schema", SCHNS);
		for (String uri : namespaces.keySet()) {
			String prefix = namespaces.get(uri);
			Element ns = JDomUtils.newElement("ns", schema,SCHNS);
			ns.setAttribute("prefix", prefix);
			ns.setAttribute("uri", uri);
		}

		Element pattern = JDomUtils.newElement("pattern",schema , SCHNS);

		XPath sourceNameXpath = XPath.newInstance("/vocabulary/sourceName");
		XPath valueNameXpath = XPath.newInstance("/vocabulary/valueName");
		XPath vocabXpath = XPath.newInstance("//vocab");
		XPath conditionalXpath = XPath.newInstance("/vocabulary/conditional/xpath");
		XPath conditionalXpathValue = XPath.newInstance("/vocabulary/conditional/value");

		for (String context : contexts.keySet()) {
			Element rule = JDomUtils.newElement("rule", pattern,SCHNS);
			rule.setAttribute("context", context);
			int i = 0;
			for (String vocabFileName : contexts.get(context)) {
				String vocabPrefix = vocabNs.get(vocabFileName);
				Element vocabFile = vocabFiles.get(vocabFileName);

				//source and value
				Element letSource = JDomUtils.newElement("let", rule,SCHNS);
				letSource.setAttribute("name", "sourceId"+i);
				Element letValue = JDomUtils.newElement("let", rule,SCHNS);
				letValue.setAttribute("name", "valueId"+i);
				String sourceXp = "source";
				String valueXp = "value";
				if(vocabPrefix != null && !vocabPrefix.equalsIgnoreCase("")) {
					sourceXp = vocabPrefix + ":" + sourceXp;
					valueXp = vocabPrefix + ":" + valueXp;
				}
				Element sourceNameElement = (Element)sourceNameXpath.selectSingleNode(vocabFile);
				if(sourceNameElement != null) {
					sourceXp = sourceNameElement.getTextTrim();
				}
				Element valueNameElement = (Element)valueNameXpath.selectSingleNode(vocabFile);
				if(valueNameElement != null) {
					valueXp = valueNameElement.getTextTrim();
				}
				if(sourceXp.equals(validationXNONE)) {
					letSource.setAttribute("value", singleValue);	
				}else {
					letSource.setAttribute("value", sourceXp);
				}
				letValue.setAttribute("value", valueXp);

				Element letContents = JDomUtils.newElement("let", rule,SCHNS);
				letContents.setAttribute("name", "contents"+i);
				//<sch:let name="contents" value="concat($sourceId0,$valueId0)"/>
				//				if(sourceXp.equals("validation-X-NONE")) {
				//					letContents.setAttribute("value", "concat('value|',$valueId"+i+")");
				//				}else {
				letContents.setAttribute("value", "concat(concat($sourceId"+i+",'|'),$valueId"+i+")");

				String assertText = "";



				String test = "";
				if(includedValues) {
					for (Object vocabEl : ((Element)vocabXpath.selectSingleNode(vocabFile)).getChildren()) {
						Element myVocabEl = (Element)vocabEl;
						if (!test.equalsIgnoreCase("")) test += " or ";
						test += "$contents"+i+ " = '" + myVocabEl.getName() + "|" + myVocabEl.getText() + "'";
					}
				}else {
					if(sourceXp.equals(validationXNONE)) {
						test = "$valueId"+i+" = document('" + deployPath  + instanceFolder + vocabFolder + vocabFileName+"')//*[name()= '"+singleValue+"']";
					}else {
						test = "$valueId"+i+" = document('" + deployPath  + instanceFolder + vocabFolder + vocabFileName+"')//*[name()= $sourceId"+i+"]";	
					}
					
				}

				if(sourceXp.equalsIgnoreCase(valueXp) || sourceXp.equals(validationXNONE)) {
					assertText = "(Rule restricted values) \"<sch:value-of select=\"$valueId"+i+"\"/>\" is a not allowed value of " + valueXp + " in " + context;
				}
				else {
					assertText = "(Rule vocab) \"<sch:value-of select=\"$sourceId"+i+"\"/>\"/\"<sch:value-of select=\"$valueId"+i+"\"/>\" is a not allowed source/value combination in " + context;
				}

				//conditional
				Element conditionalXpathElement = (Element)conditionalXpath.selectSingleNode(vocabFile);
				if(conditionalXpathElement != null) {
					List<Element> conditionalXpathValueElement = conditionalXpathValue.selectNodes(vocabFile);
					if(conditionalXpathValueElement != null && conditionalXpathValueElement.size() > 0) {
						String testPostFix = ") or (" + test + ")";
						String condXpath = conditionalXpathElement.getTextTrim();
						String condValue = conditionalXpathValueElement.get(0).getTextTrim();
						Element letConditional = JDomUtils.newElement("let", rule,SCHNS);
						letConditional.setAttribute("name", "conditionalField"+i+"");
						letConditional.setAttribute("value", condXpath);
						test = "($conditionalField"+i+" != '"+condValue +"' or count($conditionalField"+i+")=0";
						assertText += " when "+condXpath+" is \"<sch:value-of select=\"$conditionalField"+i+"\"/>\"";

						if(conditionalXpathValueElement.size() > 1) {
							for (int j = 1; j < conditionalXpathValueElement.size(); j++) {
								test += " and $conditionalField"+i+" != '"+ conditionalXpathValueElement.get(j).getTextTrim() +"'";
							}
						}
						test += testPostFix;
					}else {
						System.out.println("/vocabulary/conditional/value element not found in " + vocabFileName);
					}
				}

				//assert
				Element assertEl = JDomUtils.newElement("assert", rule,SCHNS);
				assertEl.setAttribute("test", test);
				assertEl.setText(assertText + ".");
				//				assertEl.setText(assertText + ". Valid values can be found here : " + "http://www.cs.kuleuven.be/~bramv/validation/schematron/" + instanceFolder + inputFolder + vocabFileName );
				i++;
			}
		}
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setOmitDeclaration(false);
		prettyFormat.setOmitEncoding(false);
		IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(new Document(schema),prettyFormat).replaceAll("&lt;", "<").replaceAll("&gt;", ">"),baseFolder + instanceFolder +  outputFile);

		//System.out.println(JDomUtils.parseXml2string(new Document(pattern),prettyFormat));
		//		IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(new Document(pattern),prettyFormat).replaceAll("&lt;", "<").replaceAll("&gt;", ">"), "/work/validationService/schematron/vocab-rules.sch");

	}

	private static void readVocabFiles() {
		File dir = new File(baseFolder + instanceFolder + vocabFolder);
		System.out.println(dir.getAbsolutePath());
		if(dir.exists()) {

			files = dir.listFiles();
		}
	}
}
