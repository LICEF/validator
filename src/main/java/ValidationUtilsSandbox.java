import java.util.HashMap;

import org.ariadne.validation.utils.ValidationUtils;


public class ValidationUtilsSandbox {
	public static void main(String[] args) {
		testGroupErrors();
	}
	
	public static void testGroupErrors() {
//		String errors = "XSD schema Validation Exception : Error at Line 5: cvc-complex-type.2.4.a: Invalid content was found starting with element 'Identif'. One of '{\"http://www.openarchives.org/OAI/2.0/\":error, \"http://www.openarchives.org/OAI/2.0/\":Identify, \"http://www.openarchives.org/OAI/2.0/\":ListMetadataFormats, \"http://www.openarchives.org/OAI/2.0/\":ListSets, \"http://www.openarchives.org/OAI/2.0/\":GetRecord, \"http://www.openarchives.org/OAI/2.0/\":ListIdentifiers, \"http://www.openarchives.org/OAI/2.0/\":ListRecords}' is expected.;~~ Error at Line 5: cvc-complex-type.2.4.a: Invalid content was found starting with element 'Identif'. One of '{\"http://www.openarchives.org/OAI/2.0/\":error, \"http://www.openarchives.org/OAI/2.0/\":Identify, \"http://www.openarchives.org/OAI/2.0/\":ListMetadataFormats, \"http://www.openarchives.org/OAI/2.0/\":ListSets, \"http://www.openarchives.org/OAI/2.0/\":GetRecord, \"http://www.openarchives.org/OAI/2.0/\":ListIdentifiers, \"http://www.openarchives.org/OAI/2.0/\":ListRecords}' is expected.;- XSD schema Validation Exception : Error at Line 2: cvc-elt.1: Cannot find the declaration of element 'oai-identifier'.;~~ Error at Line 6: cvc-complex-type.2.4.a: Invalid content was found starting with element 'sampleIdentifie'. One of '{\"http://www.openarchives.org/OAI/2.0/oai-identifier\":sampleIdentifier}' is expected.";
		String errorsString = "XML schema Validation Exception : Error at Line 95: cvc-datatype-valid.1.2.3: 'narrative text' is not a valid value of union type 'learningResourceType'.;~~ Error at Line 95: cvc-complex-type.2.2: Element 'value' must have no element [children], and the value must be valid.;- Schematron Validation Exception : (Rule9.2) 9.2 At least one classification with purpose 'discipline' and taxonPath.source 'LRE Thesaurus' should be present;~ (RuleAttribute) The element with name \"typicalAgeRange\" is missing language attribute in string element.;~ (RuleAttribute) language attribute in the element with name \"typicalAgeRange\" must have value.";
		HashMap<String, HashMap<String,Integer>> test = ValidationUtils.collectErrors(errorsString);
		for(String key : test.keySet()) {
			System.out.println(key + " :");
			HashMap<String,Integer> errors = test.get(key);
			for(String errorString : errors.keySet()) {
				System.out.println(errorString);
				//out.println(errorString + " (#: " + errors.get(errorString) + ")<br>");
			}
			System.out.println();
		}	
	}
}
