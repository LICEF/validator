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

package org.eel.kitchen.jsonschema.syntax.draftv3;

import java.util.Map;
import java.util.SortedMap;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

public final class PropertiesSyntaxValidator
    extends SyntaxValidator
{
    private static final PropertiesSyntaxValidator instance
        = new PropertiesSyntaxValidator();

    private PropertiesSyntaxValidator()
    {
        super("properties", NodeType.OBJECT);
    }

    public static PropertiesSyntaxValidator getInstance()
    {
        return instance;
    }

    /**
     * Check two things:
     * <ul>
     *     <li>that all values are potential schemas, ie objects;</li>
     *     <li>that, if a {@code required} attribute is found,
     *     it is a boolean.</li>
     * </ul>
     */
    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        final JsonNode node = schema.get(keyword);

        final SortedMap<String, JsonNode> fields
            = CollectionUtils.toSortedMap(node.getFields());

        String field;
        JsonNode element;
        NodeType type;

        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            field = entry.getKey();
            element = entry.getValue();
            if (!element.isObject()) {
                report.fail(String.format("field \"%s\": value has "
                    + "wrong type %s (expected a schema)", field,
                    NodeType.getNodeType(element)));
                continue;
            }
            if (!element.has("required"))
                continue;
            type = NodeType.getNodeType(element.get("required"));
            if (type == NodeType.BOOLEAN)
                continue;
            report.fail(String.format("field \"%s\": attribute "
                + "\"required\" of enclosed schema has wrong type %s "
                + "(expected a boolean)", field, type));
        }
    }
}
