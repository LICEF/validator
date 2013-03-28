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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeComponent extends XpathComponent {
	
	protected DateTimeFormatter fmt;
	
	protected Integer minYear;
	
	protected Integer maxYear;
	
	protected String pattern;
	
	public static void main(String[] args) {

		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		
		Pattern p = Pattern.compile("([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})(\\-(0[1-9]|1[0-2])(\\-(0[1-9]|[1-2][0-9]|3[0-1])(T([0-1][0-9]|2[0-3])(:[0-5][0-9](:[0-5][0-9](\\.[0-9]{1,}(Z|((\\+|\\-)([0-1][0-9]|2[0-3]):[0-5][0-9]))?)?)?)?)?)?)?");
		
		String datetime = "";
//		try {
//			datetime = "2009-08-22T10:30:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "209-08-22T10:30:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-8-22T10:30:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-108-22T10:30:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-122T10:30:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T110:30:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:310:02+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:012+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:2+01:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:02+001:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:02+1:00";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:02+01:100";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:02+01:000";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
//		try {
//			datetime = "2009-08-22T10:30:02+01:0";
//			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//			DateTime d = fmt.parseDateTime(datetime);
//			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
//		}catch(Exception e) {
//			System.out.println(e.getMessage() + " : " + datetime);
//		}
		try {
			datetime = "2009-08-22T10:3:02+01:00";
			fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
			DateTime d = fmt.parseDateTime(datetime);
			System.out.println(d);
			if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());

			String patternString = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}.*"; 
			Pattern pattern = Pattern.compile(patternString);
			Matcher m = pattern.matcher(datetime);
			if(!m.matches()) throw new Exception("DateTime " + datetime + " does not match pattern " + patternString );
		}catch(Exception e) {
			System.out.println(e.getMessage() + " : " + datetime);
		}
		


	}


	public DateTimeComponent(Vector<String> xPaths) {
		super(xPaths);
	}

	public DateTimeComponent() {
	}

	
	@Override
	public void init(String name, Hashtable<String, String> table) throws InitialisationException {
		super.init(name,table);
		
		try {
			minYear = Integer.parseInt(table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".minyear"));
		} catch (Exception e) {
			// NOOP
		}
		try {
			maxYear = Integer.parseInt(table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".maxyear"));
		} catch (Exception e) {
			// NOOP
		}
		
		try {
			pattern = table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".pattern");
			fmt = DateTimeFormat.forPattern(pattern);
		} catch (Exception e) {
			throw new InitialisationException(e.getMessage(),ValidationConstants.VAL_EXC_TYPE_DATETIME_INIT);
		}
	}

	@Override
	protected void validateElements(List<Element> elements, String xpath) throws ValidationException {
		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_DATETIME);
		for(Element datetime : elements) {
			try {
				String datetimeString = datetime.getText();
				if(datetimeString == null || datetimeString.trim().equals("")) {
					throw new Exception("DateTime can't be an empty string.");
				}
				parseDateTime(datetimeString);
			} catch (Exception e) {
				exc.addException(ValidationConstants.VAL_EXC_TYPE_DATETIME + " Validation Exception : (at " + xpath + ") " + e.getMessage());
			}
		}
		if(exc.getAllExceptions().size() > 0) throw exc;
	}

	private void parseDateTime(String datetimeString) throws Exception{
		DateTime d = fmt.parseDateTime(datetimeString);
		if(d.getYear() > 2012 || d.getYear() < 1000) throw new Exception("year must be between 1000 & 2012, was denoted " + d.getYear());
	}

}
