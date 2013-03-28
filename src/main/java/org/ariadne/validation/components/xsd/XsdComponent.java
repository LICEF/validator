package org.ariadne.validation.components.xsd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Validator;

import net.sf.vcard4j.parser.VCardException;

import org.ariadne.validation.components.XpathComponent;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XsdComponent extends XpathComponent {

	protected URL schemaLocation;
	
	protected Vector<XmlSchema> xmlSchemas;

	protected Validator validator;

	protected XsdErrorHandler errorHandler;

	protected XMLOutputter outputter;

	protected SAXParserFactory factory;

	protected DocumentBuilder builder;

	protected SAXParser parser;
	//
	//	private javax.xml.parsers.SAXParser parser2;


//	public XsdComponent(URL schemaLocation) throws InitialisationException {
//		init(schemaLocation);
//	}

	public XsdComponent() {
	}

	protected synchronized void validate(String metadata, String xpath) throws ValidationException {
		//		errorHandler.setExceptions(new XsdValidationException());

		try {
			//version 1
			//			Source source = new StreamSource(new ByteArrayInputStream(metadata.getBytes("UTF-8")));
			//			watch.start();
			//			validator.validate(source);
			//			watch.stopWPrint();
			//version 2
			//			DocumentBuilder builder = factory.newDocumentBuilder();
			//			builder.setErrorHandler(errorHandler); 			

			//			errorHandler = new XsdErrorHandler();
			//			builder.setErrorHandler(errorHandler);
			//			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(metadata.getBytes("UTF-8"));
			//			watch.start();
			//			builder.parse(byteArrayInputStream);
			//			watch.stopWPrint();
			//version 3
			//			  parser.parse(new InputSource(new ByteArrayInputStream(metadata.getBytes("UTF-8"))));
			//version 4

			InputSource source = new InputSource(new ByteArrayInputStream(metadata.getBytes("UTF-8")));
			errorHandler = new XsdErrorHandler();

			parser.parse(source,errorHandler);

			if( errorHandler != null && errorHandler.getExceptions().getAllExceptions().size() > 0) {
//				throw new ValidationException("XSD schema Validation Exception : " + errorHandler.getExceptions().getMessage());
				throw errorHandler.exceptions;
//				ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA);
//				for(String e : errorHandler.getExceptions().getAllExceptions()) {
//					exc.
//				}
//				throw exc;
			}
		}
		catch (SAXException ex) {
//			throw new ValidationException("XSD schema Validation Exception : " + ex.getMessage());
			throw new ValidationException(ex.getMessage(), ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA);

		} catch (IOException e) {
//			throw new ValidationException("XSD schema Validation Exception : " + e.getMessage());
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA);
			//		} catch (ParserConfigurationException e) {
			//			throw new ValidationException("XSD schema Validation Exception : " + e.getMessage());
		}
	}

	//version 1
	//	private void init(URL schemaLocation) throws InitialisationException {
	//		errorHandler = new XsdErrorHandler();
	//		this.schemaLocation = schemaLocation;
	//		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	//		Schema schema;
	//		try {
	//			schema = factory.newSchema(schemaLocation);
	//			validator = schema.newValidator();
	//			validator.setErrorHandler(errorHandler);
	//		} catch (SAXException e) {
	//			throw new InitialisationException("Xsd Initialisation error : " + e.getMessage());
	//		}
	//	}

	//version 2
	//	private void init(URL schemaLocation) throws InitialisationException {
	//
	//		try {
	//			errorHandler = new XsdErrorHandler();
	//			this.schemaLocation = schemaLocation;
	//
	//			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
	//			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	//			
	//			System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
	//			    "org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
	//
	//			factory =  DocumentBuilderFactory.newInstance();
	//			factory.setNamespaceAware(true); 
	//			factory.setValidating(true);
	//			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema" );
	//			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",schemaLocation.toString());     
	//			builder = factory.newDocumentBuilder();	
	//			builder.setErrorHandler(errorHandler); 			
	//		} catch (IllegalArgumentException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (ParserConfigurationException e) {
	//			throw new InitialisationException("Xsd Initialisation error : " + e.getMessage());
	//
	//		}
	//	}

	//	private void init(URL schemaLocation) throws InitialisationException {
	//	
	//			try {
	//				errorHandler = new XsdErrorHandler();
	//				this.schemaLocation = schemaLocation;
	//	
	//				System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
	//				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	//				
	//				System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
	//				    "org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
	//	
	//				factory =  SAXParserFactory.newInstance();
	//				factory.setNamespaceAware(true); 
	//				factory.setValidating(true);
	//				parser = factory.newSAXParser();	
	//				parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");
	//				parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource",schemaLocation.toString());  
	//			} catch (IllegalArgumentException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			} catch (ParserConfigurationException e) {
	//				throw new InitialisationException("Xsd Initialisation error : " + e.getMessage());
	//	
	//			} catch (SAXNotRecognizedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			} catch (SAXNotSupportedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			} catch (SAXException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}


	//version 3
	//	private void init(URL schemaLocation) throws InitialisationException {
	//	    parser = new SAXParser();    
	//		try {
	//			errorHandler = new XsdErrorHandler();
	//			this.schemaLocation = schemaLocation;
	//			
	//			 parser.setFeature("http://xml.org/sax/features/validation",true);
	//			 parser.setFeature("http://apache.org/xml/features/validation/schema",true);
	//			 parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking",
	//			 true);
	//			 parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",   schemaLocation.toString() );
	//
	//			  parser.setErrorHandler(errorHandler); 
	//	
	//		} catch (IllegalArgumentException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (SAXNotRecognizedException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (SAXNotSupportedException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}  
	//	}

	//version 4
	//	private void init(URL schemaLocation) throws InitialisationException {
	//
	//		try {
	//			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	//			
	//			System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema","org.apache.xerces.jaxp.validation.XMLSchemaFactory");
	//			
	//			SchemaFactory factory = SchemaFactory.newInstance(language);
	////			System.setProperty("javax.xml.validation.SchemaFactory","org.apache.xerces.jaxp.validation.XMLSchemaFactory");
	//			Schema schema = factory.newSchema(schemaLocation);
	//			
	//			
	//			validator = schema.newValidator();
	//			validator.setErrorHandler(errorHandler);
	//
	//		} catch (SAXException e) {
	//			throw new InitialisationException(e.getMessage());
	//		}
	//
	//	}

	private void init(String schemaLocations) throws InitialisationException {
		try {
			System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema","org.apache.xerces.jaxp.validation.XMLSchemaFactory");
			System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration","org.apache.xerces.parsers.IntegratedParserConfiguration");
			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;

			//				SchemaFactory factory = SchemaFactory.newInstance(language);
			//				Schema schema = factory.newSchema(schemaLocation);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setValidating(true);
			spf.setNamespaceAware(true);
			spf.setFeature("http://apache.org/xml/features/validation/schema", true);

			//				spf.setSchema(schema);
			parser = spf.newSAXParser();


			XMLReader xmlReader = parser.getXMLReader();
			//			    xmlReader.setFeature("http://apache.org/xml/features/validation/schema",true);
			//			    xmlReader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation","http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lomLoose.xsd http://www.mace-project.eu/xsd/LOM http://www.cs.kuleuven.be/~bramv/validation/xsd/MaceExtend.xsd");
			xmlReader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",schemaLocations);
			try {
				Class poolClass =  
					Class.forName("org.apache.xerces.util.XMLGrammarPoolImpl");

				Object grammarPool = poolClass.newInstance();

				xmlReader.setProperty(
						"http://apache.org/xml/properties/internal/grammar-pool", 
						grammarPool);          
			}
			catch (Exception e) {}
		} catch (SAXNotRecognizedException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA_INIT);
		} catch (SAXNotSupportedException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA_INIT);
		} catch (ParserConfigurationException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA_INIT);
		} catch (SAXException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA_INIT);
		}
	}

	@Override
	public void init(String name, Hashtable<String,String> table) throws InitialisationException {
		super.init(name, table);
//		if(xpath.size() == 0) {
//			try {
////				Namespace lomns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
//				XPath lomxpath = XPath.newInstance("/");
////				lomxpath.addNamespace(lomns);
//				xpath.add(lomxpath);
//			} catch (JDOMException e) {
//				throw new InitialisationException(e.getMessage());
//			}
//		}
		

		String schemaLocations = "";
		
		//Legacy code
		String schemaLocationString = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".schemaLocation");
		if(schemaLocationString != null) {
				schemaLocations+= "http://ltsc.ieee.org/xsd/LOM ";
				schemaLocations+= schemaLocationString;
		}else {
			xmlSchemas = new Vector<XmlSchema>(); 
			String xmlSchemasList = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".xmlSchemas");
			if(xmlSchemasList != null) {
				StringTokenizer xmlSchemaTokens = new StringTokenizer(xmlSchemasList,";");
				while(xmlSchemaTokens.hasMoreTokens()) {
					String xmlSchemaToken = xmlSchemaTokens.nextToken();
					String schemaNamespace = org.ariadne.validation.Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + xmlSchemaToken + ".schemaNamespace");
					if(schemaNamespace == null)schemaNamespace = table.get(ValidationConstants.VAL_PROP_PREFIX + xmlSchemaToken + ".schemaNamespace");
					String schemaLocation = org.ariadne.validation.Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + xmlSchemaToken + ".schemaLocation");
					if(schemaLocation == null)schemaLocation = table.get(ValidationConstants.VAL_PROP_PREFIX + xmlSchemaToken + ".schemaLocation");
					XmlSchema xmlSchema = new XmlSchema(schemaNamespace, schemaLocation);
					xmlSchemas.add(xmlSchema);
					schemaLocations+= schemaNamespace + " ";
					schemaLocations+= schemaLocation + " ";
				}
			}
		}
		init(schemaLocations);
		outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		outputter.setFormat(format);
	}

	@Override
	protected void validateElements(List<Element> elements, String xpath) throws ValidationException {
		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA);
		for(Element xml : elements) {
			try {
				xml.detach();
				String xmlString = outputter.outputString(new Document(xml));	
				if(xmlString == null || xmlString.trim().equals("")) {
//					throw new ValidationException("Node can't be an empty string.");
					throw new ValidationException("Node can't be an empty string.", ValidationConstants.VAL_EXC_TYPE_GENERIC);
				}
				validate(xmlString, xpath);
			} catch (VCardException e) {
				exc.addException("XSD schema Validation Exception : (at " + xpath + ") " + e.getMessage());
			}
		}
		if(exc.getAllExceptions().size() > 0) throw exc;		
	}

}
