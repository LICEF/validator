package org.ariadne.validation.exception;

import org.ariadne.validation.utils.ValidationConstants;

public class XsdValidationException extends ValidationException {

	
	public XsdValidationException() {
		super(ValidationConstants.VAL_EXC_TYPE_XML_SCHEMA);
	}

//	public String getMessage() {
//		String msg = "";
//		Vector<String> exc = getAllExceptions();
//		for(int i = 0; i < getAllExceptions().size(); i++) {
//			if(i > 0)msg +=";~~ ";
//			msg += exc.elementAt(i);
//		}
//		if(msg.equals("")) {
//			return super.getMessage();
//		}
//		else
//			return msg;
//	}

}
