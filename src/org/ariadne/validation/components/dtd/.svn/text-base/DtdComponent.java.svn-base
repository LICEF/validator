package org.ariadne.validation.components.dtd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sf.vcard4j.parser.VCardException;

import org.ariadne.validation.components.XpathComponent;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class DtdComponent extends XpathComponent {
	
	protected XMLOutputter outputter;
	protected String xmljoined;
	
	@Override
	public void validate(String metadata) throws ValidationException {
		//System.out.println("METADATA::"+metadata);
		try {			
			InputStream stream;
			stream = new ByteArrayInputStream(metadata.getBytes("UTF-8"));
			
			DtdEcho dtdValidator = new DtdEcho();
			dtdValidator.validate(stream);
		} catch (Exception e) {
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_GENERIC);
		}	
	}
	
	@Override
	protected void validateElements(List<Element> elements, String xpath) throws ValidationException {
		//System.out.println("Xpath:"+xpath);
	}
	/*@Override
	protected void validateElements(List<Element> elements, String xpath) throws ValidationException {
		//init()
		outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		outputter.setFormat(format);
		xmljoined = "";
		
		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA);
		for(Element xml : elements) {
			try {
				//TODO: empty file validation				
				xml.detach();
				String xmlString = outputter.outputString(new Document(xml));	
				if(xmlString == null || xmlString.trim().equals("")) {
//					throw new ValidationException("Node can't be an empty string.");
					throw new ValidationException("Node can't be an empty string.", ValidationConstants.VAL_EXC_TYPE_GENERIC);
				}
				xmljoined = xmljoined.concat(xmlString);
			} catch (VCardException e) {
				exc.addException("DTD Validation Exception : (at " + xpath + ") " + e.getMessage());
			}
		}
		
		//validation	
		try {
			InputStream stream;
			stream = new ByteArrayInputStream(xmljoined.getBytes("UTF-8"));
			DtdEcho echo = new DtdEcho();
			echo.validate(stream);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
		
		if(exc.getAllExceptions().size() > 0) throw exc;		
	}*/

}