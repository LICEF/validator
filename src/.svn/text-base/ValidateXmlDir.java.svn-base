import java.io.File;
import java.io.IOException;

import org.ariadne.util.JDomUtils;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class ValidateXmlDir {


	static String lomxml = "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">" +
	"<general><identifier><catalog>vgrfvr</catalog><entry>vgrfvr</entry></identifier><title><string language=\"en\">test123</string></title></general>" +
	"<lifeCycle> <contribute> <role> <source>LOMv1.0</source> <value>validator</value> </role> " +
	"<entity>BEGIN:vCard" +
	"\nVersion:3.0" +
	"\nFN:Roedy Green" +
	"\nN:Green;Roedy" +
	"\nEND:vCard</entity> </contribute> </lifeCycle>" +
	"<metaMetadata>" +
	"<contribute><date><dateTime>2007-01-01T00:00:00.0Z</dateTime></date>" +
	"<role><source>LOMv1.0</source><value>creator</value></role></contribute>" +
	"</metaMetadata>" +
	"<technical><location></location></technical></lom>";

	private static Validator validator;

	private static String metadataExtention = "xml";

	private static SAXBuilder builder = new SAXBuilder();

	public static void main(String[] args) throws IOException {



		try {

			String folder = "/work/tmp/repo/mdstore/stellarOA/publications/";

			String validationScheme = "http://info.mace-project.eu/validation/MACEv1.0/full";
			Validator.getPropertiesManager().init("ariadneV4.properties");
			initValidator();
			validateFolder(folder, validationScheme);

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

	private static void validateFolder(String folder, String validationScheme) {
		File dir = new File(folder);
		if(dir.exists() && dir.isDirectory()) {

			String[] files = dir.list();

			for(int i = 0; i< files.length; i++) {
				String fileString = files[i];
				String pathname = dir.toString() + File.separator + fileString;
				File file = new File(pathname);
				if(file.isFile() && fileString.substring(fileString.lastIndexOf(".")+1).equalsIgnoreCase(metadataExtention)) {

					try {
						org.jdom.Document jdom = builder.build(new File((String)pathname));
						validator.validateMetadata(JDomUtils.parseXml2stringNoXmlDeclaration(jdom.getRootElement(),null), validationScheme);
						System.out.println("validation OK");
					} catch (ValidationException e) {
						for(String exception : e.getAllExceptions()) {
							System.out.println("ValidationException : ");
							System.out.println(exception);
							System.out.println("At file : " + pathname);
							System.out.println();
						}
					}  catch (JDOMException e) {
						System.out.println("JDOMException : ");
						System.out.println(e);
						System.out.println("At file : " + pathname);
						System.out.println();
					} catch (IOException e) {
						System.out.println("IOException : ");
						System.out.println(e);
						System.out.println("At file : " + pathname);
						System.out.println();
					}
				}
				else {
					if(file.isDirectory()) {
						validateFolder(file.getAbsolutePath(), validationScheme);
					}
				}
			}

		}
	}

	private static Validator initValidator() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InitialisationException {
		validator = Validator.getValidator();
		validator.initFromPropertiesManager();
		return validator;
	}
}
