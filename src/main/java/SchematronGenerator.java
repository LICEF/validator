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


public class SchematronGenerator {

	private static File[] files;
	private static HashMap<String, String> vocabs = new HashMap<String, String>();
	private static HashMap<String, Vector <String>> contexts = new HashMap<String, Vector <String>>();
	private static HashMap<String, String> vocabNs = new HashMap<String, String>();
	private static HashMap<String, Element> vocabFiles = new HashMap<String, Element>();
	private static HashMap<String, String> namespaces = new HashMap<String, String>();
	public static Namespace SCHNS = Namespace.getNamespace("sch","http://purl.oclc.org/dsdl/schematron");

	//	private static String inputFolder = "/work/validationService/schematron/iso/LOM/vocab/";
	//	private static String outputFile = "/work/validationService/schematron/iso/LOM/check-vocabs.sch";


	private static String inputFolder = "vocab/";
	private static String outputFile = "check-vocabs.sch";
	private static String baseFolder = "/work/validationService/schematron/";

	private static String instanceFolder = "iso/MACE/";
//	private static String instanceFolder = "iso/ASPECT/";
	private static String deployPath = "http://ariadne.cs.kuleuven.be/validation/schematron/";


	public static void main(String[] args) {

		if(args.length > 0) {
			instanceFolder = args[0];
			if(args.length > 1) {
				baseFolder = args[1];
				if(args.length > 2) {
					outputFile = args[2];
					if(args.length > 3) {
						inputFolder = args[3];
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

	//	private static void generateRules() throws JDOMException, IOException {
	//		SAXBuilder builder = new SAXBuilder();
	//		XPath vocabXpath = XPath.newInstance("/vocab");
	//		for(File file : files) {
	//			String xpath = file.getName().substring(0,file.getName().lastIndexOf(".")+1);
	//			xpath.replaceAll(".", "/lom:");
	//			xpath = "/lom:" + xpath;
	//
	//			Document doc = builder.build(file);
	//			List nodes = vocabXpath.selectNodes(doc.getRootElement());
	//			Vector<Element> values = new Vector<Element>();
	//			for(Object node : nodes) {
	//				Element el = (Element)node;
	//				//String[] tuple = {el.getName(),el.getValue()};
	//				values.add(el);
	//			}
	//		}
	//	}
	//
	//	private static void getNameSpaces() throws JDOMException, IOException{
	//		SAXBuilder builder = new SAXBuilder();
	//		XPath nsXpath = XPath.newInstance("/vocabulary/ns");
	//		for(File file : files) {
	//			Document doc = builder.build(file);
	//			List selectNodes = nsUriXpath.selectNodes(doc.getRootElement());
	//			for(Object node : selectNodes) {
	//				String ns = ((Element)node).getValue();
	//				if(!namespaces.containsKey(ns)) {
	//					String prefix = ((Element)nsPrefixXpath.selectNodes(doc.getRootElement())).getValue();
	//					namespaces.put(ns, prefix);
	//				}
	//			}
	//
	//		}
	//	}
	//	
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

	//	private static void generateRules() throws JDOMException, IOException {
	//		Element pattern = new Element("pattern", SCHNS);
	//		
	//		for (String vocabFileName : vocabs.keySet()) {
	//			String vocabXpath = vocabs.get(vocabFileName);
	//			Element rule = JDomUtils.newElement("rule", pattern,SCHNS);
	//			rule.setAttribute("context", vocabXpath);
	//			Element let = JDomUtils.newElement("let", rule,SCHNS);
	//			let.setAttribute("name", "sourceId");
	//			let.setAttribute("value", "lom:source");
	//			Element assertEl = JDomUtils.newElement("assert", rule,SCHNS);
	//			String test = "lom:value = document('http://www.cs.kuleuven.be/~bramv/validation/schematron/iso/LOM/vocab/"+vocabFileName+"')//*[name()= $sourceId]";
	//			assertEl.setAttribute("test", test);
	//			assertEl.setText("(Rule vocab) \"<sch:value-of select=\"lom:source\"/>\"/\"<sch:value-of select=\"lom:value\"/>\" is a not allowed source/value combination in " + vocabXpath);
	//		}
	//		
	//		Element schema = new Element("schema", SCHNS);
	//		for (String uri : namespaces.keySet()) {
	//			String prefix = namespaces.get(uri);
	//			Element ns = JDomUtils.newElement("ns", schema,SCHNS);
	//			ns.setAttribute("prefix", prefix);
	//			ns.setAttribute("uri", uri);
	//		}
	//		Element include = JDomUtils.newElement("include", schema,SCHNS);
	//		include.setAttribute("href", "vocab-rules.sch");
	//
	//		Format prettyFormat = Format.getPrettyFormat();
	//		prettyFormat.setOmitDeclaration(false);
	//		prettyFormat.setOmitEncoding(false);

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
		XPath conditionalXpath = XPath.newInstance("/vocabulary/conditional/xpath");
		XPath conditionalXpathValue = XPath.newInstance("/vocabulary/conditional/value");

		for (String context : contexts.keySet()) {
			Element rule = JDomUtils.newElement("rule", pattern,SCHNS);
			//			HashMap<String, Vector <String>> sources = new HashMap<String, Vector <String>>();
			//			HashMap<String, Vector <String>> values = new HashMap<String, Vector <String>>();
			rule.setAttribute("context", context);
			int i = 0;
			for (String vocabFileName : contexts.get(context)) {
				//				String vocabXpath = vocabs.get(vocabFileName);
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
				letSource.setAttribute("value", sourceXp);		
				letValue.setAttribute("value", valueXp);		

				//				Vector<String> sourcesVector = sources.get(sourceXp);
				//				if(sourcesVector == null) {
				//					sourcesVector = new Vector<String>();
				//					sources.put(sourceXp, sourcesVector);
				//				}
				//				sourcesVector.add(vocabFileName);
				//				
				//				Vector<String> valuesVector = values.get(valueXp);
				//				if(valuesVector == null) {
				//					valuesVector = new Vector<String>();
				//					values.put(valueXp, valuesVector);
				//				}
				//				valuesVector.add(vocabFileName);

				String assertText = "";
				String test = "$valueId"+i+" = document('" + deployPath  + instanceFolder + inputFolder + vocabFileName+"')//*[name()= $sourceId"+i+"]";
				if(sourceXp.equals("validation-X-NONE")) {
					test = "$valueId"+i+" = document('" + deployPath + instanceFolder + inputFolder + vocabFileName+"')//*[name()= 'value']";
				}
				if(sourceXp.equalsIgnoreCase(valueXp) || sourceXp.equals("validation-X-NONE")) {
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

//	private static void generateRules() throws JDOMException, IOException {
//
//		Element schema = new Element("schema", SCHNS);
//		for (String uri : namespaces.keySet()) {
//			String prefix = namespaces.get(uri);
//			Element ns = JDomUtils.newElement("ns", schema,SCHNS);
//			ns.setAttribute("prefix", prefix);
//			ns.setAttribute("uri", uri);
//		}
//
//		Element pattern = JDomUtils.newElement("pattern",schema , SCHNS);
//
//		XPath sourceNameXpath = XPath.newInstance("/vocabulary/sourceName");
//		XPath valueNameXpath = XPath.newInstance("/vocabulary/valueName");
//		XPath conditionalXpath = XPath.newInstance("/vocabulary/conditional/xpath");
//		XPath conditionalXpathValue = XPath.newInstance("/vocabulary/conditional/value");
//
//		for (String vocabFileName : vocabs.keySet()) {
//			String vocabXpath = vocabs.get(vocabFileName);
//			String vocabPrefix = vocabNs.get(vocabFileName);
//			Element vocabFile = vocabFiles.get(vocabFileName);
//			Element rule = JDomUtils.newElement("rule", pattern,SCHNS);
//			rule.setAttribute("context", vocabXpath);
//
//			//source and value
//			Element letSource = JDomUtils.newElement("let", rule,SCHNS);
//			letSource.setAttribute("name", "sourceId");
//			Element letValue = JDomUtils.newElement("let", rule,SCHNS);
//			letValue.setAttribute("name", "valueId");
//			String sourceXp = "source";
//			String valueXp = "value";
//			if(vocabPrefix != null && !vocabPrefix.equalsIgnoreCase("")) {
//				sourceXp = vocabPrefix + ":" + sourceXp;
//				valueXp = vocabPrefix + ":" + valueXp;
//			}
//			Element sourceNameElement = (Element)sourceNameXpath.selectSingleNode(vocabFile);
//			if(sourceNameElement != null) {
//				sourceXp = sourceNameElement.getTextTrim();
//			}
//			Element valueNameElement = (Element)valueNameXpath.selectSingleNode(vocabFile);
//			if(valueNameElement != null) {
//				valueXp = valueNameElement.getTextTrim();
//			}
//			letSource.setAttribute("value", sourceXp);		
//			letValue.setAttribute("value", valueXp);		
//
//			String assertText = "";
//			String test = "$valueId = document('http://www.cs.kuleuven.be/~bramv/validation/schematron/" + instanceFolder + inputFolder + vocabFileName+"')//*[name()= $sourceId]";
//			if(sourceXp.equals("validation-X-NONE")) {
//				test = "$valueId = document('http://www.cs.kuleuven.be/~bramv/validation/schematron/" + instanceFolder + inputFolder + vocabFileName+"')//*[name()= 'value']";
//			}
//			if(sourceXp.equalsIgnoreCase(valueXp) || sourceXp.equals("validation-X-NONE")) {
//				assertText = "(Rule restricted values) \"<sch:value-of select=\"$valueId\"/>\" is a not allowed value of " + valueXp + " in " + vocabXpath;
//			}
//			else {
//				assertText = "(Rule vocab) \"<sch:value-of select=\"$sourceId\"/>\"/\"<sch:value-of select=\"$valueId\"/>\" is a not allowed source/value combination in " + vocabXpath;
//			}
//
//			//conditional
//			Element conditionalXpathElement = (Element)conditionalXpath.selectSingleNode(vocabFile);
//			if(conditionalXpathElement != null) {
//				List<Element> conditionalXpathValueElement = conditionalXpathValue.selectNodes(vocabFile);
//				if(conditionalXpathValueElement != null && conditionalXpathValueElement.size() > 0) {
//					String testPostFix = ") or (" + test + ")";
//					String condXpath = conditionalXpathElement.getTextTrim();
//					String condValue = conditionalXpathValueElement.get(0).getTextTrim();
//					Element letConditional = JDomUtils.newElement("let", rule,SCHNS);
//					letConditional.setAttribute("name", "conditionalField");
//					letConditional.setAttribute("value", condXpath);
//					test = "($conditionalField != '"+condValue +"'";
//					assertText += " when "+condXpath+" is \"<sch:value-of select=\"$conditionalField\"/>\"";
//					
//					if(conditionalXpathValueElement.size() > 1) {
//						for (int i = 1; i < conditionalXpathValueElement.size(); i++) {
//							test = test + " and $conditionalField != '"+ conditionalXpathValueElement.get(i).getTextTrim() +"'";
//						}
//					}
//					
//				}else {
//					System.out.println("/vocabulary/conditional/value element not found in " + vocabFileName);
//				}
//			}
//
//			//assert
//			Element assertEl = JDomUtils.newElement("assert", rule,SCHNS);
//			assertEl.setAttribute("test", test);
//			assertEl.setText(assertText + ".");
//		}
//
//		Format prettyFormat = Format.getPrettyFormat();
//		prettyFormat.setOmitDeclaration(false);
//		prettyFormat.setOmitEncoding(false);
//		IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(new Document(schema),prettyFormat).replaceAll("&lt;", "<").replaceAll("&gt;", ">"),baseFolder + instanceFolder +  outputFile);
//
//		//System.out.println(JDomUtils.parseXml2string(new Document(pattern),prettyFormat));
//		//		IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(new Document(pattern),prettyFormat).replaceAll("&lt;", "<").replaceAll("&gt;", ">"), "/work/validationService/schematron/vocab-rules.sch");
//	}

	private static void readVocabFiles() {
		File dir = new File(baseFolder + instanceFolder + inputFolder);

		if(dir.exists()) {

			files = dir.listFiles();
		}
	}
}
