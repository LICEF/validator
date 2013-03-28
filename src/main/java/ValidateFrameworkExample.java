import java.io.IOException;

import org.ariadne.util.JDomUtils;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationUtils;


public class ValidateFrameworkExample {


    //static String lomxml = "<LOM xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">" +
    //"<General><identifier><catalog>vgrfvr</catalog><entry>vgrfvr</entry></identifier><title><string language=\"en\">test123</string></title></General>" +
    //"<lifeCycle> <contribute> <role> <source>LOMv1.0</source> <value>validator</value> </role> " +
    //"<entity>BEGIN:vCard" +
    //"\nVersion:3.0" +
    //"\nFN:Roedy Green" +
    //"\nN:Green;Roedy" +
    //"\nEND:vCard</entity> </contribute> </lifeCycle>" +
    //"<metaMetadata>" +
    //"<contribute><date><dateTime>2007-01-01T00:00:00.0Z</dateTime></date>" +
    //"<role><source>LOMv1.0</source><value>creator</value></role></contribute>" +
    //"</metaMetadata>" +
    //"<technical><location></location></technical></LOM>";

    static String lomxml = 
        "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchemainstance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd\">" +
"<general><identifier>" +
"<catalog>CANAL_SAVOIR</catalog>" +
"<test>whatever</test>" +
"<entry>oai:canalsavoir.ca:615146061</entry>" +
"</identifier>" +
"</general></lom>";

    public static void main(String[] args) throws IOException {

System.out.println( "start" );        
        //String profileUri = "http://info.mace-project.eu/validation/MACEv4.4"; // MaceFull
        //String profileUri = "http://ltsc.ieee.org/xsd/LOM/strict";
        String profileUri = "http://ltsc.ieee.org/xsd/LOM/loose";

        /**
         * Initialize the properties manager.
         * @param String : the absolute or relative path to the ariadneV4.properties file
         */
        Validator.getPropertiesManager().init("ariadneV4.properties");
System.out.println( "init done" );        

        Validator validator = Validator.getValidator();
System.out.println( "validator="+validator );        
        try {
            /**
             * Use this call to update the Validation Schemes from the ARIADNE server
             */
            Validator.updatePropertiesFileFromRemote();
System.out.println( "props updated" );            

            /**
             * Call to initialize the validation schemes
             */
            validator.initFromPropertiesManager();
System.out.println( "validator inited" );            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        /**
         * Retrieve the available validations schemes
         */
        
        System.out.println(ValidationUtils.getValidationSchemes());
        
        try {
            /**
             * Validate the metadata against a validation scheme
             */
            validator.validateMetadata(lomxml, profileUri);
            System.out.println("validation OK");
        } catch (ValidationException e) {
            /**
             * Retrieve each exception separately
             */
            for(String exception : e.getAllExceptions()) {
                System.out.println("ValidationException : ");
                System.out.println(exception);
                System.out.println();
            }
            /**
             * Or get the validation exceptions back as an xml file
             */
            System.out.println(JDomUtils.parseXml2string(ValidationUtils.collectErrorsAsXml(e.getMessage()),null));
            
        }
    }
}

