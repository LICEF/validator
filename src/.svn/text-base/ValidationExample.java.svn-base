import java.io.IOException;

import org.ariadne.util.Stopwatch;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;


public class ValidationExample {
	
	
	static String lomxml = "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">" +
	"<general><identifier><catalog>vgrfvr</catalog><entry>vgrfvr</entry></identifier><title><string language=\"en\">test123</string></title></general>" +
	"<lifeCycle> <contribute> <role> <source>LOMv1.0</source> <value>valdidator</value> </role> " +
	"<entity>BEGIN:vCard" +
	"\nVersion:3.0" +
	"\nFN:Roedy Green" +
	"\nN:Green;Roedy" +
	"\nEND:vCard</entity> </contribute> </lifeCycle>" +
"<metaMetadata>" +
"<contribute><date><dateTime>2007-01-01T00:00:00.0Z</dateTime></date>" +
"<role><source>LOMv1.0</source><value>creeator</value></role></contribute>" +
"</metaMetadata>" +
"<technical><location></location></technical></lom>";
	
	private static Validator validator;
	
	public static void main(String[] args) throws IOException {
		
		try {
			String maceFull = "http://info.mace-project.eu/validation/MACEv1.0/full";
			String meltFull = "http://info.melt-project.eu/validation/MELTv1.0/full";
			Validator.getPropertiesManager().init("ariadneV4.properties");
			initValidator();
			Stopwatch watch = new Stopwatch();
            	try {
            		watch.start();
					validator.validateMetadata(lomxml, meltFull);
					System.out.println("validation OK");
				} catch (ValidationException e) {
//					watch.stopWPrint();
					
//					 System.out.println(JDomUtils.parseXml2string(ValidationUtils.getExceptionsAsXml(e), null));
					
					System.out.println(e.getMessage());
					
//					for(String exception : e.getAllExceptions()) {
//						System.out.println("ValidationException : ");
//						System.out.println(exception);
//						System.out.println();
//					}
				}
				watch.stopWPrint();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitialisationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static Validator initValidator() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InitialisationException {
		validator = Validator.getValidator();
		validator.initFromPropertiesManager();
		return validator;
	}
}
