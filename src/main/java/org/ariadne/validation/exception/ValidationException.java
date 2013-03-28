package org.ariadne.validation.exception;

import java.util.Vector;

public class ValidationException extends Exception {

	protected Vector<ValidationException> exc = new Vector<ValidationException>();
	protected Vector<String> exceptions;
	protected String type;

	public String getType() {
		return type;
	}

	public Vector<ValidationException> getValidationExceptions() {
		return exc;
	}

	public ValidationException(String type) {
		this.type = type;
		exceptions = new Vector<String>();
	} 

	public ValidationException(String message, String type) {
		exceptions = new Vector<String>();
		exceptions.add(message);
		this.type = type;
	}

	public String getMessage() {
		String msg = "";
		Vector<String> exc = getOwnExceptions();
		int i = 0;
		if(exc.size() > 0) msg += this.getType() + " Validation Exception : ";
		for(i = 0; i < exc.size(); i++) {
			if(i > 0)msg +=";~ ";
			msg += exc.elementAt(i);
		}
		for(ValidationException val : getValidationExceptions()) {
			if(i > 0)msg +=";- ";
			msg +=val.getMessage();
			i++;
		}

		if(msg.equals("")) {
			return super.getMessage();
		}
		else
			return msg;
	}


	public Vector<String> getOwnExceptions() {
		return exceptions;
	}

	public Vector<String> getAllExceptions() {
		Vector<String> exc = new Vector<String>();
		exc.addAll(exceptions);
		for(ValidationException val : getValidationExceptions()) {
			exc.addAll(val.getAllExceptions());
		}
		return exc;
	}

	public void addException(String exception) {
		getOwnExceptions().add(exception);
	}
}
