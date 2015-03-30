package org.ariadne.validation.components.schematron;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.ariadne.validation.components.ValidationComponent;
import org.ariadne.validation.components.schematron.validator.Result;
import org.ariadne.validation.components.schematron.validator.Validator;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.w3c.dom.DOMException;

public class SchematronComponent extends ValidationComponent {

	protected Validator validator;

	@Override
	public void init(String name, Hashtable<String,String> table) throws InitialisationException {
		try {
			URL schemaLocation = new URL((String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".schemaLocation"));
			String preprocessorString = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".preprocessorLocation");
			if(preprocessorString == null) {
				preprocessorString = org.ariadne.validation.Validator.getPropertiesManager().getProperty(ValidationConstants.VAL_PROP_PREFIX + "schematron.preprocessorLocation");
			}
			URL preprocessorLocation = new URL(preprocessorString);
			validator = new Validator(new StreamSource(schemaLocation.toString()), new StreamSource(preprocessorLocation.toString()));
//			IOUtilsv2.writeStringToFileInEncodingUTF8("my test", "tempFolder.txt");
//			File file = new File("tempFolder.txt");
//			System.out.println(file.getAbsolutePath());
		} catch (MalformedURLException e) {
			//throw new InitialisationException("Schematron Initialisation error : " + e.getMessage());
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCH_INIT);
		} catch (TransformerConfigurationException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCH_INIT);
		} catch (TransformerException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCH_INIT);
		} catch (ParserConfigurationException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCH_INIT);
		} catch (IOException e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCH_INIT);
		}
	}

	@Override
	public void validate(String metadata) throws ValidationException {

		try {
			metadata = metadata.replaceAll("&", "&amp;");
			StringReader stringReader = new StringReader(metadata);
			StreamSource streamSource = new StreamSource(stringReader);
			// perform validation
			Result result = null;
			if(validator != null) {
				result = validator.validate(streamSource);
				// everything ok?
				if (!result.isBlank()) {
//					System.out.println(result.getResultAsText());
//					String text = result.getMessage();
//					throw new ValidationException("Schematron Validation Exception : " + text);
					ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_SCHEMATRON);
					for(String text : result.getAllAssertStrings()) {
						exc.addException(text);
					}
					throw exc;
					

				}
			}else {
//				throw new ValidationException("Schematron Validator was not correctly instantiated...");
				throw new ValidationException("Schematron Validator was not correctly instantiated...", ValidationConstants.VAL_EXC_TYPE_GENERIC);
			}
		} catch (TransformerConfigurationException e) {
//			throw new ValidationException("Schematron Validation Exception : " + e.getMessage());
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCHEMATRON);
		} catch (DOMException e) {
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCHEMATRON);
		} catch (TransformerException e) {
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCHEMATRON);
		} catch (ParserConfigurationException e) {
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_SCHEMATRON);
		}

	}

}
