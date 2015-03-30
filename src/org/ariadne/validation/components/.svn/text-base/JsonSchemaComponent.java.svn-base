package org.ariadne.validation.components;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationConfig;
import org.eel.kitchen.jsonschema.main.ValidationReport;

public class JsonSchemaComponent extends ValidationComponent {

	protected JsonValidator jsonSchemaValidator;
	protected ObjectMapper mapper;

	@Override
	public void init(String name, Hashtable<String, String> table) throws InitialisationException {
		try {
			mapper = new ObjectMapper();

			//			String jsonSchemaString = IOUtilsv2.readStringFromFile(new File("card-schema.json"), "UTF-8");

			//			jsonSchemaValidator = schemaProvider.getSchema(new ByteArrayInputStream(jsonSchemaString.getBytes("UTF-8")) );
			URL schemaLocation = new URL((String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".schemaLocation"));
			jsonSchemaValidator = new JsonValidator(new ValidationConfig(),mapper.readTree(schemaLocation)); //The validator
		} catch (Exception e) {
			throw new InitialisationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_JSON_SCHEMA_INIT);
		}


	}

	@Override
	public void validate(String metadata) throws ValidationException {

		if(jsonSchemaValidator != null) {
			JsonNode jsonNode = null;
			try {
				jsonNode = mapper.readTree(metadata);
				ValidationReport report = jsonSchemaValidator.validate(jsonNode);
				List<String> errors = report.getMessages();
				if(errors.size() > 0) {
					ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_JSON_SCHEMA);
					for(String text : errors) {
						exc.addException(text);
					}
					throw exc;
				}
			} catch (JsonProcessingException e) {
				ValidationException exc = new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
				throw exc;
			} catch (IOException e) {
				ValidationException exc = new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
				throw exc;
			} catch (JsonValidationFailureException e) {
				ValidationException exc = new ValidationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_GENERIC);
				throw exc;
			}
		}
		else {
			throw new ValidationException("JSON Validator was not correctly instantiated...", ValidationConstants.VAL_EXC_TYPE_GENERIC);
		}
	}
}
