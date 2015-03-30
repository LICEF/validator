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

package org.eel.kitchen.jsonschema.main;

import static org.eel.kitchen.jsonschema.main.ValidationFeature.FAIL_FAST;
import static org.eel.kitchen.jsonschema.main.ValidationFeature.SKIP_SCHEMACHECK;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eel.kitchen.jsonschema.bundle.CustomValidatorBundle;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.factories.FullValidatorFactory;
import org.eel.kitchen.jsonschema.factories.NoSyntaxValidatorFactory;
import org.eel.kitchen.jsonschema.factories.ValidatorFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.jsonschema.uri.URIHandlerFactory;
import org.eel.kitchen.util.NodeType;
import org.eel.kitchen.util.SchemaVersion;

public final class ValidationConfig
{
    private final Lock factoryLock = new ReentrantLock();

    private boolean factoriesBuilt = false;

    private SchemaVersion defaultVersion = SchemaVersion.DRAFT_V3;

    private final Map<SchemaVersion, ValidatorBundle> bundles
        = new EnumMap<SchemaVersion, ValidatorBundle>(SchemaVersion.class);

    private final EnumSet<ValidationFeature> features
        = EnumSet.noneOf(ValidationFeature.class);

    private final URIHandlerFactory handlerFactory
        = new URIHandlerFactory();

    private final Map<SchemaVersion, ValidatorFactory> factories
        = new EnumMap<SchemaVersion, ValidatorFactory>(SchemaVersion.class);

    private ReportFactory reports;

    public ValidationConfig()
    {
        ValidatorBundle bundle;

        for (final SchemaVersion version: SchemaVersion.values()) {
            bundle = new CustomValidatorBundle(version.getBundle());
            bundles.put(version, bundle);
        }
    }

    public SchemaVersion getDefaultVersion()
    {
        return defaultVersion;
    }

    public void setDefaultVersion(final SchemaVersion defaultVersion)
    {
        this.defaultVersion = defaultVersion;
    }

    public Map<SchemaVersion, ValidatorBundle> getBundles()
    {
        return Collections.unmodifiableMap(bundles);
    }

    public void registerValidator(final String keyword,
        final SyntaxValidator sv, final KeywordValidator kv,
        final NodeType... types)
    {
        registerValidator(defaultVersion, keyword, sv, kv, types);
    }

    public void registerValidator(final SchemaVersion version,
        final String keyword, final SyntaxValidator sv,
        final KeywordValidator kv, final NodeType... types)
    {
        bundles.get(version).registerValidator(keyword, sv, kv, types);
    }

    public void unregisterValidator(final String keyword)
    {
        unregisterValidator(defaultVersion, keyword);
    }

    public void unregisterValidator(final SchemaVersion version,
        final String keyword)
    {
        bundles.get(version).unregisterValidator(keyword);
    }

    public boolean enable(final ValidationFeature feature)
    {
        return features.add(feature);
    }

    public boolean disable(final ValidationFeature feature)
    {
        return features.remove(feature);
    }

    public EnumSet<ValidationFeature> getFeatures()
    {
        return EnumSet.copyOf(features);
    }

    /**
     * Register a new {@link URIHandler} for a given scheme
     *
     * @param scheme the scheme
     * @param handler the handler
     * @throws IllegalArgumentException the provided scheme is null
     *
     * @see URIHandlerFactory#registerHandler(String, URIHandler)
     */
    public void registerURIHandler(final String scheme,
        final URIHandler handler)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        handlerFactory.registerHandler(scheme, handler);
    }

    /**
     * Unregister the handler for a given scheme
     *
     * @param scheme the victim
     * @throws IllegalArgumentException the provided scheme is null
     *
     * @see URIHandlerFactory#unregisterHandler(String)
     */
    public void unregisterURIHandler(final String scheme)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        handlerFactory.unregisterHandler(scheme);
    }

    public URIHandlerFactory getHandlerFactory()
    {
        return handlerFactory;
    }

    public void buildFactories()
    {
        factoryLock.lock();

        try {
            if (factoriesBuilt)
                return;

            reports = new ReportFactory(features.contains(FAIL_FAST));

            ValidatorFactory factory;
            ValidatorBundle bundle;

            for (final SchemaVersion version: SchemaVersion.values()) {
                bundle = bundles.get(version);
                factory = features.contains(SKIP_SCHEMACHECK)
                    ? new NoSyntaxValidatorFactory(bundle)
                    : new FullValidatorFactory(bundle);
                factories.put(version, factory);
            }

            factoriesBuilt = true;
        } finally {
            factoryLock.unlock();
        }
    }

    public ValidatorFactory getFactory(final SchemaVersion version)
    {
        return factories.get(version);
    }

    public ValidationReport getReport(final String prefix)
    {
        return reports.create(prefix);
    }
}
