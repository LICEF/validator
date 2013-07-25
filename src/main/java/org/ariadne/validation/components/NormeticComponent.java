package org.ariadne.validation.components;

import java.util.Hashtable;

import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;

import ca.licef.validator.NormeticValidator;
import ca.licef.validator.ValidationIssue;
import ca.licef.validator.ValidationReport;

public class NormeticComponent extends ValidationComponent {

    public NormeticComponent() {
    }

    public void validate( String metadata ) throws ValidationException {
        ValidationReport report = null;
        try {
            validator = new NormeticValidator();
            validator.setSchematronValidationLessNoisy( true );
            report = validator.validate( metadata );
        }
        catch( Exception e ) {
            throw( new ValidationException( "Problem when running NormeticValidator: " + e, ValidationConstants.VAL_EXC_TYPE_GENERIC ) );
        }

        int issueCount = report.getIssueCount();
        if( issueCount > 0 ) {
            ValidationException exc = null;
            for( ValidationIssue issue : report.getIssues() ) {
                // Consider only errors.  Warnings and Unknown are ignored. - FB
                if( ValidationIssue.Severity.ERROR.equals( issue.getSeverity() ) || 
                    ValidationIssue.Severity.FATAL_ERROR.equals( issue.getSeverity() ) ) {
                    if( exc == null ) 
                        exc = new ValidationException( ValidationConstants.VAL_EXC_TYPE_NORMETIC );
                    exc.addException( issue.toString() );
                }
            }
            if( exc != null )
                throw( exc );
        }
    }

    public void init( String name, Hashtable<String,String> table ) throws InitialisationException {
    }

    private static NormeticValidator validator = null;

}
