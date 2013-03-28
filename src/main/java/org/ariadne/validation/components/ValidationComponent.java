package org.ariadne.validation.components;

import java.util.Hashtable;

import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;

public abstract class ValidationComponent {

	public abstract void validate(String metadata) throws ValidationException;

	public abstract void init(String name, Hashtable<String,String> table) throws InitialisationException;
	
}
