import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;


public class SchematronStructureGenerator {

	private static HashMap<String, String> namespaces = new HashMap<String, String>();
	public static Namespace SCHNS = Namespace.getNamespace("sch","http://purl.oclc.org/dsdl/schematron");
	public static Namespace LOMLOMNS = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
	public static Namespace MYMYNS = Namespace.getNamespace("myns","http://ariadne");
	private static HashMap<String, Vector <Element>> addedElements = new HashMap<String, Vector<Element>>();

	//	private static String inputFolder = "/work/validationService/schematron/iso/LOM/vocab/";
	//	private static String outputFile = "/work/validationService/schematron/iso/LOM/check-vocabs.sch";


	private static String inputFile = "NaturalEuropeCoreSchematron.structure.xml";
	private static String outputFile = "CoreSchematron.sch";

	private static String baseFolder = "/work/validationService/resources/schematron/";

	private static String instanceFolder = "iso/NaturalEurope/";


	public static void main(String[] args) {

		if(args.length > 0) {
			instanceFolder = args[0];
			if(args.length > 1) {
				inputFile = args[1];
				if(args.length > 2) {
					outputFile = args[2];
				}
			}
		}

		try {
			generateRules();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateRules() throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(baseFolder + instanceFolder + inputFile));
		Element schema = new Element("schema", SCHNS);
		Element pattern = JDomUtils.newElement("pattern", schema,SCHNS);
		pattern.setAttribute("fpi", "Mandatory elements");

		Element rootElement = doc.getRootElement();
		String rootPrefix = getNamespacePrefix(rootElement);

		boolean doParentTests = true;
		
		String parentTestString = rootElement.getAttributeValue("parentTest",MYMYNS);
		if(parentTestString != null) {
			doParentTests = Boolean.parseBoolean(parentTestString);
		}
		Element rule1 = JDomUtils.newElement("rule", pattern,SCHNS);
		rule1.setAttribute("context", "//" + rootPrefix + ":" + rootElement.getName());
		
		for (int i = 0; i < rootElement.getChildren().size(); i++) {
			Element el = (Element)rootElement.getChildren().get(i);
			String mandatoryValue = el.getAttributeValue("mandatory",MYMYNS);
			if(mandatoryValue != null) {
				if(mandatoryValue.trim().equalsIgnoreCase("true")) {
					addAssert(rule1,el,Integer.toString(i+1),"");
				}else if(mandatoryValue.trim().equalsIgnoreCase("conditional")) {
					String xpath = el.getAttributeValue("xpath",MYMYNS);
					String value = el.getAttributeValue("value",MYMYNS);
					if(xpath != null) {
						addConditionalAssert(rule1,el,Integer.toString(i+1),"",xpath,value);
					}
				}
			}
			if(el.getChildren().size() > 0) addRules(pattern,Integer.toString(i+1),el,"","");
		}
		
		if(doParentTests)addParentTests(schema);
		addAllowedCustomNamespaceElements(schema);
		addNamespaces(schema);
//		addCustomRules(schema);
		
		
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setOmitDeclaration(false);
		prettyFormat.setOmitEncoding(false);
		IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(new Document(schema),prettyFormat).replaceAll("&lt;", "<").replaceAll("&gt;", ">"),baseFolder + instanceFolder + outputFile);
		
	}

	private static void addCustomRules(Element schema) {
		Element pattern = JDomUtils.newElement("pattern", schema,SCHNS);
		pattern.setAttribute("fpi", "allowed langstring translations");
		Element rule = JDomUtils.newElement("rule", pattern,SCHNS);
		rule.setAttribute("context", "lom:string");
		
		Element letLang = JDomUtils.newElement("let", rule,SCHNS);
		letLang.setAttribute("name", "lang");
		letLang.setAttribute("value", "@language");	
		
		Element assert1 = JDomUtils.newElement("assert", rule,SCHNS);
		String testString = "count(../lom:string[@language=$lang]) = 1";
		assert1.setAttribute("test", testString);
		assert1.setText("(Rule Langstring) A Langstring must only contain one string element per language (multiple \"<sch:value-of select=\"$lang\"/>\" detected)");
	}

	private static void addParentTests(Element schema) {
		for (String key : addedElements.keySet()) {
			
			Element pattern = JDomUtils.newElement("pattern", schema,SCHNS);
			pattern.setAttribute("fpi", "Testing parents of "+key+" namespace elements");

			String prefix = namespaces.get(key);
			for (Element el : addedElements.get(key)) {
				
				Element rule = JDomUtils.newElement("rule", pattern,SCHNS);

				rule.setAttribute("context", prefix + ":" + el.getName());
				
				Element assert1 = JDomUtils.newElement("assert", rule,SCHNS);
				String testString = "";
				testString += "parent::" + namespaces.get(el.getParentElement().getNamespaceURI()) + ":" + el.getParentElement().getName();
				assert1.setAttribute("test", testString);
				assert1.setText("(Rule Parent) The <sch:name/> element is only allowed inside the " + el.getParentElement().getName() + " element");
			}

			
		}
	}

	private static void addAllowedCustomNamespaceElements(Element schema) {
		
		for (String key : addedElements.keySet()) {
			
			Element pattern = JDomUtils.newElement("pattern", schema,SCHNS);
			pattern.setAttribute("fpi", "allowed "+key+" namespace elements");
			Element rule1 = JDomUtils.newElement("rule", pattern,SCHNS);
			String prefix = namespaces.get(key);
			rule1.setAttribute("context", prefix + ":*");
			
			Element assert1 = JDomUtils.newElement("assert", rule1,SCHNS);
			String testString = "";
			for (Element el : addedElements.get(key)) {
				if(!testString.equals("")) testString += " or ";
				testString += "self::" + prefix + ":" + el.getName();
			}
			assert1.setAttribute("test", testString);
			assert1.setText("(Rule Extend) The element <sch:name/> is not a valid " + key + " element");
			
		}
	}

	private static void addNamespaces(Element schema) {
		for (String key : namespaces.keySet()) {
			
			Element ns = new Element("ns",SCHNS);
			schema.addContent(0,ns);
			ns.setAttribute("prefix", namespaces.get(key));
			ns.setAttribute("uri", key);
			
		}
	}
	
	private static String getNamespacePrefix(Element element) {
		if(!namespaces.containsKey(element.getNamespaceURI())) {
			namespaces.put(element.getNamespaceURI(),element.getNamespacePrefix());
		}
		if(!element.getNamespaceURI().equals(LOMLOMNS.getURI())) {
			if(!addedElements.containsKey(element.getNamespaceURI())) {
				addedElements.put(element.getNamespaceURI(), new Vector<Element>());
			}
			if(!addedElements.get(element.getNamespaceURI()).contains(element))addedElements.get(element.getNamespaceURI()).add(element);
		}
		return namespaces.get(element.getNamespaceURI());	
	}

	private static void addAssert(Element rule1, Element el, String nbrPrefix, String textPrefix) {
		if (nbrPrefix == null)nbrPrefix = "";
		Element assert1 = JDomUtils.newElement("assert", rule1,SCHNS);
		assert1.setAttribute("test", getNamespacePrefix(el) + ":" + el.getName());
//		String newNbrPrefix = el.getAttributeValue("nbr",MYMYNS);
//		if(nbrPrefix != null && !nbrPrefix.equals("")) newNbrPrefix = nbrPrefix +"."+ newNbrPrefix;
		String newTextPrefix = el.getName();
		if(!textPrefix.trim().equals("")) newTextPrefix = textPrefix + "." + newTextPrefix;
		assert1.setText("(Rule"+nbrPrefix+") "+newTextPrefix+" element is missing");
	}
	
	private static void addConditionalAssert(Element rule1, Element el, String nbrPrefix, String textPrefix, String condXpath, String condValue) {
		if (nbrPrefix == null)nbrPrefix = "";

		String test = getNamespacePrefix(el) + ":" + el.getName();
		
		String newTextPrefix = el.getName();
		if(!textPrefix.trim().equals("")) newTextPrefix = textPrefix + "." + newTextPrefix;
		String assertText = "(Rule"+nbrPrefix+") "+newTextPrefix+" element is missing";
		
		String testPostFix = ") or (" + test + ")";
		Element letConditional = JDomUtils.newElement("let", rule1,SCHNS);
		letConditional.setAttribute("name", "conditionalField");
		letConditional.setAttribute("value", condXpath);
		test = "($conditionalField != '"+condValue +"' or count($conditionalField)=0";
		assertText += ", is mandatory when "+condXpath+" is \"<sch:value-of select=\"$conditionalField\"/>\"";

//		if(conditionalXpathValueElement.size() > 1) {
//			for (int j = 1; j < conditionalXpathValueElement.size(); j++) {
//				test += " and $conditionalField"+i+" != '"+ conditionalXpathValueElement.get(j).getTextTrim() +"'";
//			}
//		}
		test += testPostFix;
		
		Element assert1 = JDomUtils.newElement("assert", rule1,SCHNS);
		assert1.setAttribute("test", test);
		assert1.setText(assertText);
	}

	private static void addRules(Element pattern, String nbrPrefix, Element el, String context, String textPrefix) {
		Element rule = JDomUtils.newElement("rule", pattern,SCHNS);
		String newContext = getNamespacePrefix(el) + ":" + el.getName();
		if(!context.trim().equals("")) newContext = context + "/" + newContext;
		rule.setAttribute("context", newContext);
		for (int i = 0; i < el.getChildren().size(); i++) {
			Element child = (Element) el.getChildren().get(i);
			String newNbrPrefix = Integer.toString(i+1);
			if(nbrPrefix != null && !nbrPrefix.equals("")) newNbrPrefix = nbrPrefix +"."+ newNbrPrefix;
			String newTextPrefix = el.getName();
			if(!textPrefix.trim().equals("")) newTextPrefix = textPrefix + "." + newTextPrefix;
			
			String mandatoryValue = child.getAttributeValue("mandatory",MYMYNS);
			if(mandatoryValue != null) {
				if(mandatoryValue.trim().equalsIgnoreCase("true")) {
					addAssert(rule,child,newNbrPrefix,newTextPrefix);
				}else if(mandatoryValue.trim().equalsIgnoreCase("conditional")) {
					String xpath = child.getAttributeValue("xpath",MYMYNS);
					String value = child.getAttributeValue("value",MYMYNS);
					if(xpath != null) {
						addConditionalAssert(rule,child,newNbrPrefix,newTextPrefix,xpath,value);
					}
				}
			}
			
//			if(child.getAttributeValue("mandatory",MYMYNS) != null) {
//				addAssert(rule,child,newNbrPrefix,newTextPrefix);
//			}
			if(child.getChildren().size() > 0) {
				addRules(pattern,newNbrPrefix,child,newContext,newTextPrefix);
			}else {
				//Do NOT remove ! Needed for getting all custom elements ...
				getNamespacePrefix(child);
			}
		}
		if(rule.getChildren().size() == 0) {
			rule.detach();
		}
	}
}
