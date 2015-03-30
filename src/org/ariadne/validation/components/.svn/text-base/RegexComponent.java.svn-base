package org.ariadne.validation.components;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.jdom.Element;

public class RegexComponent extends XpathComponent {
	
	protected Pattern pattern;
	
	protected String patternString;
	
	public RegexComponent(Vector<String> xPaths) {
		super(xPaths);
	}

	public RegexComponent() {
	}
	
	@Override
	public void init(String name, Hashtable<String, String> table) throws InitialisationException {
		super.init(name,table);
		
		try {
			patternString = table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".pattern");
			pattern = Pattern.compile(patternString);
		} catch (Exception e) {
			throw new InitialisationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_REGEX_INIT);
		}
	}

	@Override
	protected void validateElements(List<Element> elements, String xpath) throws ValidationException {
		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_REGEX);
		for(Element el : elements) {
			try {
				String elString = el.getText();
				if(elString == null || elString.trim().equals("")) {
					throw new Exception("This element can't be an empty string.");
				}
				parseRegex(elString);
			} catch (Exception e) {
				exc.addException(ValidationConstants.VAL_EXC_TYPE_REGEX + " Validation Exception : (at " + xpath + ") " + e.getMessage());
			}
		}
		if(exc.getAllExceptions().size() > 0) throw exc;
	}

	private void parseRegex(String elString) throws Exception{
		Matcher m = pattern.matcher(elString);
		if(!m.matches()) throw new Exception("String " + elString + " does not match pattern " + patternString );
	}

}
