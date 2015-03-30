package org.ariadne.validation.components.xsd;

import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.exception.XsdValidationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XsdErrorHandler extends DefaultHandler {
	
	protected XsdValidationException exceptions = new XsdValidationException();

	public ValidationException getExceptions() {
		return exceptions;
	}

	public void setExceptions(XsdValidationException exceptions) {
		this.exceptions = exceptions;
	}

	public void error(SAXParseException exception) throws SAXException {
		getExceptions().addException(exceptionToString("Error",exception));
//		System.out.println(exception.getMessage());
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		getExceptions().addException(exceptionToString("Fatal Error",exception));
//		throw new SAXException(getExceptions().getMessage());
	}

	public void warning(SAXParseException exception) throws SAXException {
		getExceptions().addException(exceptionToString("Warning",exception));
	}
	
    private String exceptionToString(String type,SAXParseException e) {
        return type+" at Line "+e.getLineNumber()+": "+e.getMessage();
      }

}
