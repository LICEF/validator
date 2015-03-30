/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.base;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * A validator which takes a {@link Collection} of validators as an argument,
 * and requires all of them to validate in order to report a success
 *
 * <p>It will declare validation a failure on the first failing validator.</p>
 */
public final class MatchAllValidator
    implements Validator
{
    /**
     * The list of validators
     */
    private final List<Validator> validators = new LinkedList<Validator>();

    /**
     * Constructor
     *
     * @param c the collection of validators to use
     */
    public MatchAllValidator(final Collection<Validator> c)
    {
        validators.addAll(c);
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();

        for (final Validator v: validators) {
            report.mergeWith(v.validate(context, instance));
            //Updated by Bramv
//            if (!report.isSuccess())
//                break;
        }

        return report;
    }
}
