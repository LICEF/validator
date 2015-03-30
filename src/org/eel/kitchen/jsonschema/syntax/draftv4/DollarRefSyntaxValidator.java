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

package org.eel.kitchen.jsonschema.syntax.draftv4;

import java.net.URI;
import java.net.URISyntaxException;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

/**
 * Syntax validator for {@code $ref}
 *
 * <p>Note that we go a little further than what the spec says,
 * but this is logical: {@code $ref} should be by itself,
 * there is just no point in it being accompanied by other keywords</p>
 */
public final class DollarRefSyntaxValidator
    extends SyntaxValidator
{
    private static final DollarRefSyntaxValidator instance
        = new DollarRefSyntaxValidator();

    private DollarRefSyntaxValidator()
    {
        super("$ref", NodeType.STRING);
    }

    public static DollarRefSyntaxValidator getInstance()
    {
        return instance;
    }

    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        try {
            new URI(schema.get(keyword).getTextValue());
        } catch (URISyntaxException ignored) {
            report.fail("not a valid URI");
        }

        if (schema.size() > 1)
            report.fail("$ref should be by itself");
    }
}
