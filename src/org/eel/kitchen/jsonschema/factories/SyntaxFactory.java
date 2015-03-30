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

package org.eel.kitchen.jsonschema.factories;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.MatchAllValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;

/**
 * Factory providing syntax checking validators for a schema
 *
 * <p>Syntax validators are used to validate the schema itself. In doing so,
 * they ensure that keyword validators always have correct data to deal with.
 * </p>
 *
 * <p>Note that unknown keywords to this factory trigger a validation
 * <b>failure</b>. Therefore, it is important that <b>all</b> keywords be
 * registered (even if knowingly ignored). This is on purpose.</p>
 */
public final class SyntaxFactory
{
    /**
     * Map pairing a schema keyword with its corresponding syntax validator
     */
    private final Map<String, SyntaxValidator> validators;

    /**
     * The set of ignored keywords for this factory
     *
     * <p>Note that an ignored keyword is <b>not</b> the same as an unknown
     * keyword, it simply means that if the keyword is found in the schema,
     * it is assumed to always be valid.</p>
     */
    private final Set<String> ignoredKeywords;

    /**
     * Constructor
     *
     * @param bundle the validator bundle to use
     */
    public SyntaxFactory(final ValidatorBundle bundle)
    {
        validators = new HashMap<String, SyntaxValidator>(bundle
            .syntaxValidators());

        ignoredKeywords = new HashSet<String>(bundle.ignoredSyntaxValidators());
    }

    /**
     * Get the syntax validator for a given context
     *
     * <p>As the summary mentions, an unknown keyword to this factory will
     * trigger a failure by returning an {@link AlwaysFalseValidator}.</p>
     *
     * @param context the validation context
     * @return the matching validator
     * @throws JsonValidationFailureException if reporting is set to throw
     * this exception instead of collecting messages
     */
    public Validator getValidator(final ValidationContext context)
        throws JsonValidationFailureException
    {
        final JsonNode schema = context.getSchema();
        final ValidationReport report = context.createReport(" [schema]");

        final Set<String> fields
            = CollectionUtils.toSet(schema.getFieldNames());

        fields.removeAll(ignoredKeywords);

        final Set<String> keywords = new HashSet<String>(validators.keySet());

        if (!keywords.containsAll(fields)) {
            fields.removeAll(keywords);
            for (final String field: fields)
                report.fail("unknown keyword " + field);
            return new AlwaysFalseValidator(report);
        }

        if (fields.isEmpty())
            return new AlwaysTrueValidator();

        final Collection<Validator> collection = getValidators(fields);

        return collection.size() == 1 ? collection.iterator().next()
            : new MatchAllValidator(collection);
    }

    /**
     * Return a list of validators for a schema node
     *
     * @param fields the list of keywords
     * @return the list of validators
     */
    private Collection<Validator> getValidators(final Set<String> fields)
    {
        final Set<Validator> ret = new HashSet<Validator>();

        for (final String field: fields)
            ret.add(validators.get(field));

        return ret;
    }
}
