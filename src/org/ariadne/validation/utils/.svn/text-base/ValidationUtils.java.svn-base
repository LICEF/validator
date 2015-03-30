package org.ariadne.validation.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ariadne.util.JDomUtils;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.ValidationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class ValidationUtils {

	public static XPath validationTypeXpath = null;

	protected static SAXBuilder builder = null;
	
	protected static ObjectMapper mapper = new ObjectMapper();

	static {
		try {
			validationTypeXpath = XPath.newInstance("//validationType");
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		List<String> dewew = getValidationSchemesSorted();
		System.out.println(dewew);
	}

	public static HashMap<String, String> getValidationSchemes(){
		HashMap<String, String> schemes = new HashMap<String, String>();
		String schemeNames = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_SCHEMES_LIST);
		schemeNames +=  ";" + Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_SCHEMES_LIST_JSON);
		StringTokenizer tokenizer = new StringTokenizer(schemeNames,";");
		while(tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			if(nextToken != null && !nextToken.trim().equals("")) {
				String name = nextToken.trim();
				schemes.put(name, Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + name + ".URI"));
			}
		}
		return schemes;
	}

	public static List<String> getValidationSchemesSorted(){
		HashMap<String, String> schemes = getValidationSchemes();
		String[] array = (String[]) schemes.values().toArray(new String[schemes.values().size()]);
		Arrays.sort(array);
		return Arrays.asList(array);
	}

	public static Document getValidationSchemesAsXml() {
		Document doc = new Document();
		Element rootElement = new Element("validationSchemes");
		doc.setRootElement(rootElement);
		HashMap<String, String> schemes = getValidationSchemes();
		for(String key:schemes.keySet()) {
			JDomUtils.newElement("scheme", rootElement, null).setText(schemes.get(key).trim());
		}
		return doc;
	}
	
	public static String parsePrintJson(String json) throws JsonParseException, JsonMappingException, IOException {
		JsonNode rootNode = mapper.readValue(new StringReader(json), JsonNode.class);
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		return writer.writeValueAsString(rootNode);
	}

	//	public static String formatMetadata(String scheme, String metadata) {
	//		
	//		String type = Validator.getValidator().getSchemes().get(scheme).getType();
	//		if(type.equalsIgnoreCase("json")){
	//			try{
	//				metadata = new JSONObject(metadata).toString(4);
	//			}catch(Exception e){
	//				
	//			}
	//		}else if(type.equalsIgnoreCase("xml")){
	//			XMLOutputter outputter= null;
	//			try{
	//				outputter = new XMLOutputter();
	//				Format format = Format.getPrettyFormat();
	//				format.setOmitDeclaration(true);
	//				outputter.setFormat(format);
	//				metadata = outputter.outputString(xpathNode);
	//			}catch(Exception e){
	////				exception = e.getMessage();
	//			}
	//		}
	//		return metadata;
	//	}

	public static Document collectErrorsAsXml(String errorString) {
		HashMap<String, HashMap<String, Integer>> errors = collectErrors(errorString);
		Document errDoc = new Document();
		Element rootElement = new Element("validationResults");
		errDoc.setRootElement(rootElement);
		if(errorString == null || errorString.trim().equals("")) {
			rootElement.setAttribute(new Attribute("validationSucceeded", "true"));
		}
		else if(errors.isEmpty()) {
			if(errorString.toLowerCase().contains("prolog"))
				errorString = "The metadata was not well-formed. Please put a well-formed XML instance.";
			rootElement.setAttribute(new Attribute("validationSucceeded", "false"));

			JDomUtils.newElement("error", rootElement, null).setText(errorString.trim());
		}else {
			rootElement.setAttribute(new Attribute("validationSucceeded", "false"));
			for(String type:errors.keySet()) {
				Element typeEl = JDomUtils.newElement("validationType", rootElement, null);
				JDomUtils.newElement("name", typeEl, null).setText(type.trim());
				HashMap<String, Integer> errorTypeErrors = errors.get(type);
				for(String error:errorTypeErrors.keySet()) {
					JDomUtils.newElement("error", typeEl, null).setText(error.trim());	
				}

			}
		}
		return errDoc;
	}

	public static Document getExceptionsAsXml(ValidationException exc) {
		Document errDoc = new Document();
		Element rootElement = new Element("validationResults");
		errDoc.setRootElement(rootElement);
		if(exc.getAllExceptions().size() == 0) {
			rootElement.setAttribute(new Attribute("validationSucceeded", "true"));
		}
		//		else if(errors.isEmpty()) {
		//			if(errorString.toLowerCase().contains("prolog"))
		//				errorString = "The metadata was not well-formed. Please put a well-formed XML instance.";
		//			rootElement.setAttribute(new Attribute("validationSucceeded", "false"));
		//			
		//			JDomUtils.newElement("error", rootElement, null).setText(errorString.trim());
		//		}
		else {
			rootElement.setAttribute(new Attribute("validationSucceeded", "false"));

			addExceptions(exc,rootElement);

		}
		return errDoc;
	}

	private static void addExceptions(ValidationException exc, Element rootElement) {
		if(exc.getOwnExceptions().size() > 0) {
			Element typeEl = JDomUtils.newElement("validationType", rootElement, null);
			JDomUtils.newElement("name", typeEl, null).setText(exc.getType());
			for(String error:exc.getOwnExceptions()) {
				JDomUtils.newElement("error", typeEl, null).setText(error.trim());	
			}
		}
		if(exc.getValidationExceptions().size() > 0) {
			for(ValidationException e : exc.getValidationExceptions()) {
				addExceptions(e, rootElement);
			}

		}

	}

	public static String getResultAsHtml(String XmlErrorString) {
		String html = "";
		builder = new SAXBuilder();

		try {
			Document doc = builder.build(new StringReader(XmlErrorString));
			List<Element> nodes = validationTypeXpath.selectNodes(doc);
			for(Element node : nodes) {
				html += "<b>" +node.getChildText("name") + " Validation Exception :</b><br>";
				List<Element> errors = node.getChildren("error");
				for(Element error : errors) {
					html += error.getTextTrim() + "<br>";
				}
				html += "<br>";
			}
			return html;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static String getResultAsHtml(ValidationException exc) {
		String html = "";
		Document doc = getExceptionsAsXml(exc);
		try {
			List<Element> nodes = validationTypeXpath.selectNodes(doc);
			for(Element node : nodes) {
				html += "<b>" +node.getChildText("name") + " Validation Exception :</b><br>";
				List<Element> errors = node.getChildren("error");
				for(Element error : errors) {
					html += error.getTextTrim() + "<br>";
				}
				html += "<br>";
			}
			return html;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static HashMap<String, HashMap<String,Integer>> collectErrors(String errorString) {
		HashMap<String, HashMap<String,Integer>> errors = new HashMap<String, HashMap<String,Integer>>();
		try {

			String[] errorTokens = errorString.split("\\;- ");
			for (String errorToken : errorTokens) {

				Pattern errorPattern = Pattern.compile("(.+?) Validation Exception : (.*)", Pattern.CASE_INSENSITIVE);
				Matcher errorMatcher = errorPattern.matcher(errorToken);
				String errorGroup = "";
				while (errorMatcher.find()) {
					String errorType = errorMatcher.group(1) + " Validation Exception";
					HashMap<String,Integer> errorTypeErrors = errors.get(errorType);
					if (errorTypeErrors == null) {
						errorTypeErrors = new HashMap<String,Integer>();
						errors.put(errorType, errorTypeErrors);
					}
					errorGroup = errorMatcher.group(2);
					String[] singleErrors = errorGroup.split("\\;~ ");
					if(singleErrors.length <= 1) singleErrors = errorGroup.split("\\;~~ "); //@deprecated
					for (String singleError : singleErrors) {
						Integer singleErrorCount = errorTypeErrors.get(singleError);
						if(singleErrorCount == null) {
							errorTypeErrors.put(singleError, new Integer(1));
						}else {
							singleErrorCount++;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

}
