package org.ariadne.validation.exception;

import org.ariadne.validation.utils.ValidationConstants;

public class InitialisationException extends ValidationException {

	public InitialisationException() {
		super(ValidationConstants.VAL_EXC_TYPE_GENERIC);
	} 
	
	public InitialisationException(String message, String type) {
		super(message, type);
	}
	
}
