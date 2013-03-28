import java.io.FileInputStream;
import java.io.IOException;

import org.ariadne.util.Stopwatch;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.ValidationException;


public class ValidateFrameworkTestPerformance {


	static String lomxml = "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">" +
	"<General><identifier><catalog>vgrfvr</catalog><entry>vgrfvr</entry></identifier><title><string language=\"en\">test123</string></title></General>" +
	"<lifeCycle> <contribute> <role> <source>LOMv1.0</source> <value>validator</value> </role> " +
	"<entity>BEGIN:vCard" +
	"\nVersion:3.0" +
	"\nFN:Roedy Green" +
	"\nN:Green;Roedy" +
	"\nEND:vCard</entity> </contribute> </lifeCycle>" +
	"<metaMetadata>" +
	"<contribute><date><dateTime>2007-01-01T00:00:00.0Z</dateTime></date>" +
	"<role><source>LOMv1.0</source><value>creator</value></role>" +
	"<entity>BEGIN:vCard" +
	"\nVersion:3.0" +
	"\nFN:Roedy Green" +
	"\nN:Green;Roedy" +
	"\nEND:vCard</entity>" +
	"</contribute>" +
	"</metaMetadata>" +
	"<technical><location>http://hiohiohio.com</location></technical></lom>";

	public static void main(String[] args) throws IOException {


		String meltFull = "http://info.melt-project.eu/validation/MELTv1.0/full";
		String lomScheme = "http://ltsc.ieee.org/xsd/LOM/strict";
		String mace = "http://info.mace-project.eu/validation/MACEv1.0/minimal";
		String maceFull = "http://info.mace-project.eu/validation/MACEv1.0/full";
		String oai = "http://www.openarchives.org/OAI/2.0/";
		String test = "http://ltsc.ieee.org/xsd/LOM/looseVocab";
		String burst = "http://www.stellarnet.eu/validation/BuRSTv1.0";
		//			String meltBMBWK = "http://info.melt-project.eu/validation/MELTv1.0/BMBWK";
		//			System.out.println(org.apache.xerces.impl.Version.getVersion());


		/**
		 * Initialize the properties manager.
		 * @param String : the absolute or relative path to the ariadneV4.properties file
		 */
		System.out.println("Start init validation service...");
		Validator.getPropertiesManager().init("ariadneV4.properties");

		Validator validator = Validator.getValidator();
		try {
			/**
			 * Use this call to update the Validation Schemes from the ARIADNE server
			 */
//			Validator.updatePropertiesFileFromRemote();

			/**
			 * Call to initialize the validation schemes
			 */
			validator.initFromPropertiesManager();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Finished init validation service...");
		/**
		 * Reading the metadata from file
		 */
		FileInputStream file = new FileInputStream("burst.xml");
		//			FileInputStream file = new FileInputStream("meltXML.xml");
		//	        FileInputStream file = new FileInputStream("oai.xml");
		byte[] b = new byte[file.available()];
		file.read(b);
		file.close ();
		String lomFromFile = new String (b);

		
		Stopwatch watch = new Stopwatch();
		System.out.println("Starting warming up ...");
		for (int i = 0; i < 100; i++) {
			try {
				/**
				 * Validate the metadata against a validation scheme
				 */
				validator.validateMetadata(lomFromFile, burst);
				
				System.out.println("validation OK");
			} catch (ValidationException e) {

			}
		}
		System.out.println("Done warming up ...");
		
		long total = 0;
		int length = 100;
		for (int i = 0; i < length; i++) {
			try {
				watch.start();
				/**
				 * Validate the metadata against a validation scheme
				 */
				validator.validateMetadata(lomFromFile, burst);
				total += watch.getElapsedTime();
				watch.stop();
				
				System.out.println("validation OK");
			} catch (ValidationException e) {
				total += watch.getElapsedTime();
				watch.stop();
				//					System.out.println(e.getMessage());
				//			/**
				//			 * Retrieve each exception separately
				//			 */
				//			for(String exception : e.getAllExceptions()) {
				//				System.out.println("ValidationException : ");
				//				System.out.println(exception);
				//				System.out.println();
				//			}
				/**
				 * Or get the validation exceptions back as an xml file
				 */

			}
		}
		System.out.println("validation OK");
		stopWPrint(total / length);
	}
	
	public static void stopWPrint(long diff) {
		int mins = (int)Math.floor(diff/60000.0);
		float d = (float) (diff/1000.0);
		float e = (float) (mins*60.0);
		float secs = d - e; 
		System.out.println("average : " + mins + " m " + secs + " s");
	}
}
