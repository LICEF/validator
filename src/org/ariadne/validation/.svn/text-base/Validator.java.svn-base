package org.ariadne.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ariadne.config.PropertiesManager;
import org.ariadne.validation.components.ValidationComponent;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class Validator {

	protected HashMap<String, ValidationScheme> schemes; 

	protected static Validator validator = null;

	protected static PropertiesManager manager = null;

	public static Validator getValidator() {
		if(validator == null) {
			validator = new Validator();
		}
		return validator;
	}

	public static void setPropertiesManager(PropertiesManager m) {
		manager = m;
	}

	public static PropertiesManager getPropertiesManager() {
		if(manager == null) {
			manager = PropertiesManager.getInstance();
		}
		return manager;
	}


	protected Validator() {
		schemes = new HashMap<String, ValidationScheme>();

		//		try {
		//			
		//			Vector<ValidationComponent> meltComponents = new Vector<ValidationComponent>();
		//			String meltLoose = "http://info.melt-project.eu/validation/MELTv1.0/loose";
		//			
		//			
		//			String lre = "http://fire.eun.org/xsd/lre/lre.xsd";
		//			URL lreXsd = new URL(lre);
		//			meltComponents.add(new XsdComponent(lreXsd));
		//			
		//			String lom = "http://ltsc.ieee.org/xsd/lomv1.0/lomLoose.xsd";
		//			URL lomXsd = new URL(lom);
		//			meltComponents.add(new XsdComponent(lomXsd));
		//			
		//			String vcardXpath = "/lom:lom/lom:lifeCycle/lom:contribute/lom:entity";
		//			Vector<String> vcards = new Vector<String>();
		//			vcards.add(vcardXpath);
		//			
		//			meltComponents.add(new VcardComponent(vcards));
		//			
		//			schemes.put(meltLoose, meltComponents);
		//			
		//			
		//			
		//			
		//			
		//		} catch (MalformedURLException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

	}

	public void initFromPropertiesManager() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InitialisationException {
		String schemes = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_SCHEMES_LIST);
		String schemesJson = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_SCHEMES_LIST_JSON);
		if(schemesJson != null) {
			schemes = schemes.trim();
			schemes += ";" + schemesJson.trim();
		}
		StringTokenizer tokenizer = new StringTokenizer(schemes,";");
		InitialisationException exp = new InitialisationException();
		while(tokenizer.hasMoreTokens()) {
			int expSize = exp.getAllExceptions().size();
			String scheme = tokenizer.nextToken();
			String schemeUri = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + scheme + ".URI");
			if(schemeUri == null) throw new InitialisationException("schemeUri of scheme " + scheme + " not found","Schemes Initialisation");
			String type = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + scheme + ".type");
			String topNode = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + scheme + ".topNode");
			String topNodeNs = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + scheme + ".topNode.ns");
			String topNodeNsPrefix = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + scheme + ".topNode.nsPrefix");
			
			Vector<ValidationComponent> components = new Vector<ValidationComponent>();
			String componentList = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + scheme + ".ValidationComponents");
			StringTokenizer tokenizer2 = new StringTokenizer(componentList,";");
			while(tokenizer2.hasMoreTokens()) {
				String componentName = tokenizer2.nextToken();
				Hashtable table = Validator.getPropertiesManager().getPropertyStartingWith(ValidationConstants.VAL_PROP_PREFIX + componentName);
				String name = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + componentName + ".componentType");
				if(name == null) throw new InitialisationException("componentType of component " + componentName + " not found","Schemes Initialisation");
				try {
					Class componentclass = Class.forName(name.trim());
					ValidationComponent component = (ValidationComponent)componentclass.newInstance();
					component.init(componentName, table);
					components.add(component);
				} catch (InitialisationException e) {
					exp.addException("Error initializing "+ name + " : InitialisationException : " + e.getMessage());
				} catch (ClassNotFoundException e) {
					exp.addException("ClassNotFoundException : " + e.getMessage());
				}

			}
			if(expSize == exp.getAllExceptions().size()) {
				if(type != null && !type.trim().equals("")) {
					addScheme(schemeUri, components,type, topNode, topNodeNs, topNodeNsPrefix);
				}else {
					addScheme(schemeUri, components, topNode, topNodeNs, topNodeNsPrefix);
				}
			}
		}
		if(exp.getAllExceptions().size() > 0) {
			throw exp;
		}
	}

	public static void updatePropertiesFileFromRemote() throws InitialisationException {

		// get online validation properties from remote location
		String locFromPropString;
		URL url;
		HttpURLConnection http;
		BufferedReader in = null;
		try {
			locFromPropString = Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_REMOTE_PROPERTIESFILE_LOCATION);
			url = new URL(locFromPropString);
			http = (HttpURLConnection) url.openConnection();
			InputStreamReader remoteStream = new InputStreamReader(http.getInputStream());
			in = new BufferedReader(remoteStream);
		} catch (Exception e) {
			try {
				locFromPropString = ValidationConstants.VAL_DEFAULT_REMOTE_PROPERTIESFILE;
				url = new URL(locFromPropString);
				http = (HttpURLConnection) url.openConnection();
				InputStreamReader remoteStream = new InputStreamReader(http.getInputStream());
				in = new BufferedReader(remoteStream);
			} catch (IOException e2) {
				//NOOP
			}
		}

		if(in != null) {

			try {
				// copy them to a new local file
				String propFilePath = Validator.getPropertiesManager().getPropertiesFile().getAbsolutePath();
				File newFile = new File(propFilePath + ".new");
				if (newFile.exists()) newFile.delete();
				RandomAccessFile wraf = new RandomAccessFile(propFilePath + ".new", "rw");
				String line = "";
				line = in.readLine();
				if (line != null) {
					line = line.trim();
					wraf.writeBytes(line);
				}
				do {
					line = in.readLine();
					if (line != null) {
						line = line.trim();
						wraf.writeBytes("\n" + line);
					}

				} while (line != null);
				in.close();

				RandomAccessFile rrafProp = new RandomAccessFile(Validator.getPropertiesManager().getPropertiesFile(), "r");
				int i = 0;
				int lastPosition = -1;
				String prefix = ValidationConstants.VAL_PROP_PREFIX.replaceAll("\\.","");;

				do {
					line = rrafProp.readLine();
					if (line != null) {
						i++;
						if (getType(line).equals(prefix)) lastPosition = i;
					}
				} while (line != null);
				rrafProp.seek(0); //put cursor at beginning of file
				i = 0;
				do {
					line = rrafProp.readLine();
					if (line != null) {
						line = line.trim();
						i++;
						if(i > lastPosition ||
								(!getType(line).equals("comment") 
										&& !getType(line).equals(prefix))
										&& !getType(line).equals("")) {
							wraf.writeBytes("\n" + line);
						}
					}
				} while (line != null);
				//if (lastPosition == -1) wraf.writeBytes(key + " = " + value + "\n");
				wraf.close();

				newFile = new File(Validator.getPropertiesManager().getPropertiesFile() + ".new");
				File oldFile = Validator.getPropertiesManager().getPropertiesFile();
				if (newFile.exists()) {
					oldFile.delete();
					newFile.renameTo(oldFile);
				}

				Validator.getPropertiesManager().init(oldFile);
			} catch (FileNotFoundException e) {
				throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_GENERIC);
			} catch (IOException e) {
				throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_GENERIC);
			}
		}


	}

	/**
	 * 
	 * @param key
	 * @return returns "comment" for comments, and the "upper" name of a property otherwise
	 */
	private static String getType(String key) {
		if (key.trim().startsWith("#")) return "comment";
		int indexToekenning = key.indexOf("=");
		int indexPunt = key.indexOf(".");
		if (indexPunt == -1 && indexToekenning != -1) return key.substring(0, indexToekenning).trim();
		int index = Math.max(indexToekenning, indexPunt);
		if (index == -1) return key;
		return key.substring(0, indexPunt);
	}

	public void addScheme(String URI, Vector<ValidationComponent> components) {

		addScheme(URI, components, "xml", null, null, null);
	}
	
	public void addScheme(String URI, Vector<ValidationComponent> components,String topNode,String topNodeNs,String topNodeNsPrefix) {

		addScheme(URI, components, "xml", topNode, topNodeNs, topNodeNsPrefix);
	}
	
	public void addScheme(String uri, Vector<ValidationComponent> components, String type) {
		addScheme(uri, components, type, null, null, null);
	}

	public void addScheme(String uri, Vector<ValidationComponent> components, String type,String topNode,String topNodeNs,String topNodeNsPrefix) {
		ValidationScheme scheme = new ValidationScheme();
		scheme.setComponents(components);
		scheme.setType(type);
		scheme.setUri(uri);
		scheme.setTopNode(topNode);
		scheme.setTopNodeNs(topNodeNs);
		scheme.setTopNodeNsPrefix(topNodeNsPrefix);
		schemes.put(uri, scheme);
	}

	public boolean canValidateScheme(String scheme) {
		ValidationScheme validationScheme = schemes.get(scheme);
		if(validationScheme == null) return false;
		else return true;
		//		Vector<ValidationComponent> components = validationScheme.getComponents();
		//		if (components != null) return true;
		//		else return false;
	}

	public void validateMetadata(String metadata, String scheme) throws ValidationException, InitialisationException {
		try {

			ValidationScheme validationScheme = schemes.get(scheme);
			preprocessType(validationScheme, metadata);

			Vector<ValidationComponent> components = validationScheme.getComponents();
			if (components != null) {
				validateComponents(metadata,components);
			}
			else {
				throw new InitialisationException("No validation scheme found with URI \"" + scheme + "\" !", ValidationConstants.VAL_EXC_TYPE_GENERIC);
			}
		} catch(JDOMException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		} catch (IOException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		}
	}

	private void preprocessType(ValidationScheme validationScheme, String metadata) throws JDOMException, IOException {
		String type = validationScheme.getType();
		preprocessType(type,metadata);
	}

	private void preprocessType(String type, String metadata) throws JDOMException, JsonParseException, JsonMappingException, IOException {
		if(type.equalsIgnoreCase("xml")) {
			SAXBuilder builder = new SAXBuilder();
			builder.build(new StringReader(metadata));
		} else if(type.equalsIgnoreCase("json")) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(new StringReader(metadata), JsonNode.class);
		}
	}

	protected void validateComponents(String metadata,Vector<ValidationComponent> components) throws ValidationException {

		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_GENERIC);

		for(ValidationComponent component : components) {
			try {
				component.validate(metadata);
			} catch (ValidationException e) {
				exc.getValidationExceptions().add(e);
			}
		}
		if(exc.getAllExceptions().size() > 0 || exc.getValidationExceptions().size() > 0) {
			throw exc;
		}

	}

	public void validateMetadataJIT(String metadata, String metadataType, String componentType, String schemaLocation) throws ValidationException, InitialisationException {
		try {
			String componentName = "tempComponent";

			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(ValidationConstants.VAL_PROP_PREFIX + componentName + ".schemaLocation", schemaLocation);

			if(componentType.equalsIgnoreCase("org.ariadne.validation.components.xsd.XsdComponent")) {
				properties.remove(ValidationConstants.VAL_PROP_PREFIX + componentName + ".schemaLocation");
				
				properties.put(ValidationConstants.VAL_PROP_PREFIX + componentName + ".xmlSchemas","tempSchema");
				properties.put(ValidationConstants.VAL_PROP_PREFIX + "tempSchema" + ".schemaLocation",schemaLocation);
				
				SAXBuilder builder = new SAXBuilder();
				Document schema = builder.build(new URL(schemaLocation));
				XPath xpath = XPath.newInstance("//xsd:schema/@targetNamespace");
				xpath.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
				String targetNamespace = ""; 
				Attribute attr = (Attribute)xpath.selectSingleNode(schema);
				if(attr != null) targetNamespace = attr.getValue();
				properties.put(ValidationConstants.VAL_PROP_PREFIX + "tempSchema" + ".schemaNamespace",targetNamespace);
			}
			
			Class componentclass = Class.forName(componentType.trim());
			ValidationComponent component = (ValidationComponent)componentclass.newInstance();
			component.init(componentName, properties);

			preprocessType(metadataType, metadata);

			Vector<ValidationComponent> components = new Vector<ValidationComponent>();
			components.add(component);
			validateComponents(metadata,components);
			
		} catch(JDOMException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		} catch (IOException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		} catch (ClassNotFoundException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		} catch (InstantiationException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		} catch (IllegalAccessException e) {
			throw new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
		}
	}

	public HashMap<String, ValidationScheme> getSchemes() {
		return schemes;
	}

}

