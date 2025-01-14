/*
 * Copyright 2019 VicTools.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.victools.jsonschema.generator.impl;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.CustomDefinitionProvider;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.InstanceAttributeOverride;
import com.github.victools.jsonschema.generator.MethodScope;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigPart;
import com.github.victools.jsonschema.generator.TypeAttributeOverride;
import com.github.victools.jsonschema.generator.TypeContext;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of a schema generator's configuration.
 */
public class SchemaGeneratorConfigImpl implements SchemaGeneratorConfig {

    private final ObjectMapper objectMapper;
    private final Map<Option, Boolean> options;
    private final SchemaGeneratorConfigPart<FieldScope> fieldConfigPart;
    private final SchemaGeneratorConfigPart<MethodScope> methodConfigPart;
    private final List<CustomDefinitionProvider> customDefinitions;
    private final List<TypeAttributeOverride> typeAttributeOverrides;

    /**
     * Constructor of a configuration instance.
     *
     * @param objectMapper           supplier for object and array nodes for the JSON structure being generated
     * @param options                specifically configured settings/options (thereby overriding the default enabled/disabled flag)
     * @param fieldConfigPart        configuration part for fields
     * @param methodConfigPart       configuration part for methods
     * @param customDefinitions      custom suppliers for a type's schema definition
     * @param typeAttributeOverrides applicable type attribute overrides
     */
    public SchemaGeneratorConfigImpl(ObjectMapper objectMapper,
                                     Map<Option, Boolean> options,
                                     SchemaGeneratorConfigPart<FieldScope> fieldConfigPart,
                                     SchemaGeneratorConfigPart<MethodScope> methodConfigPart,
                                     List<CustomDefinitionProvider> customDefinitions,
                                     List<TypeAttributeOverride> typeAttributeOverrides) {
        this.objectMapper = objectMapper;
        this.options = options;
        this.fieldConfigPart = fieldConfigPart;
        this.methodConfigPart = methodConfigPart;
        this.customDefinitions = customDefinitions;
        this.typeAttributeOverrides = typeAttributeOverrides;
    }

    /**
     * Whether a given option is currently enabled (either specifically or by default).
     *
     * @param setting generator option to check
     * @return whether the given generator option is enabled
     */
    private boolean isOptionEnabled(Option setting) {
        return this.options.getOrDefault(setting, false);
    }

    @Override
    public boolean shouldCreateDefinitionsForAllObjects() {
        return this.isOptionEnabled(Option.DEFINITIONS_FOR_ALL_OBJECTS);
    }

    @Override
    public boolean shouldIncludeStaticFields() {
        return this.isOptionEnabled(Option.PUBLIC_STATIC_FIELDS) || this.isOptionEnabled(Option.NONPUBLIC_STATIC_FIELDS);
    }

    @Override
    public boolean shouldIncludeStaticMethods() {
        return this.isOptionEnabled(Option.STATIC_METHODS);
    }

    @Override
    public boolean shouldIncludeSchemaVersionIndicator() {
        return this.isOptionEnabled(Option.SCHEMA_VERSION_INDICATOR);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public ObjectNode createObjectNode() {
        return this.getObjectMapper().createObjectNode();
    }

    @Override
    public ArrayNode createArrayNode() {
        return this.getObjectMapper().createArrayNode();
    }

    @Override
    public CustomDefinition getCustomDefinition(ResolvedType javaType, TypeContext context) {
        CustomDefinition result = this.customDefinitions.stream()
                .map(provider -> provider.provideCustomSchemaDefinition(javaType, context))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        return result;
    }

    @Override
    public List<TypeAttributeOverride> getTypeAttributeOverrides() {
        return Collections.unmodifiableList(this.typeAttributeOverrides);
    }

    @Override
    public List<InstanceAttributeOverride<FieldScope>> getFieldAttributeOverrides() {
        return this.fieldConfigPart.getInstanceAttributeOverrides();
    }

    @Override
    public List<InstanceAttributeOverride<MethodScope>> getMethodAttributeOverrides() {
        return this.methodConfigPart.getInstanceAttributeOverrides();
    }

    @Override
    public boolean isNullable(FieldScope field) {
        return Optional.ofNullable(this.fieldConfigPart.isNullable(field))
                .orElseGet(() -> this.isOptionEnabled(Option.NULLABLE_FIELDS_BY_DEFAULT));
    }

    @Override
    public boolean isNullable(MethodScope method) {
        return Optional.ofNullable(this.methodConfigPart.isNullable(method))
                .orElseGet(() -> this.isOptionEnabled(Option.NULLABLE_METHOD_RETURN_VALUES_BY_DEFAULT));
    }

    @Override
    public boolean shouldIgnore(FieldScope field) {
        return this.fieldConfigPart.shouldIgnore(field);
    }

    @Override
    public boolean shouldIgnore(MethodScope method) {
        return this.methodConfigPart.shouldIgnore(method);
    }

    @Override
    public ResolvedType resolveTargetTypeOverride(FieldScope field) {
        return this.fieldConfigPart.resolveTargetTypeOverride(field);
    }

    @Override
    public ResolvedType resolveTargetTypeOverride(MethodScope method) {
        return this.methodConfigPart.resolveTargetTypeOverride(method);
    }

    @Override
    public String resolvePropertyNameOverride(FieldScope field) {
        return this.fieldConfigPart.resolvePropertyNameOverride(field);
    }

    @Override
    public String resolvePropertyNameOverride(MethodScope method) {
        return this.methodConfigPart.resolvePropertyNameOverride(method);
    }

    @Override
    public String resolveTitle(FieldScope field) {
        return this.fieldConfigPart.resolveTitle(field);
    }

    @Override
    public String resolveTitle(MethodScope method) {
        return this.methodConfigPart.resolveTitle(method);
    }

    @Override
    public String resolveDescription(FieldScope field) {
        return this.fieldConfigPart.resolveDescription(field);
    }

    @Override
    public String resolveDescription(MethodScope method) {
        return this.methodConfigPart.resolveDescription(method);
    }

    @Override
    public String resolveDefault(MethodScope method) {
        return this.methodConfigPart.resolveDefault(method);
    }

    @Override
    public String resolveDefault(FieldScope field) {
        return this.fieldConfigPart.resolveDefault(field);
    }

    @Override
    public Collection<?> resolveEnum(FieldScope field) {
        return this.fieldConfigPart.resolveEnum(field);
    }

    @Override
    public Collection<?> resolveEnum(MethodScope method) {
        return this.methodConfigPart.resolveEnum(method);
    }

    @Override
    public Integer resolveStringMinLength(FieldScope field) {
        return this.fieldConfigPart.resolveStringMinLength(field);
    }

    @Override
    public Integer resolveStringMinLength(MethodScope method) {
        return this.methodConfigPart.resolveStringMinLength(method);
    }

    @Override
    public Integer resolveStringMaxLength(FieldScope field) {
        return this.fieldConfigPart.resolveStringMaxLength(field);
    }

    @Override
    public Integer resolveStringMaxLength(MethodScope method) {
        return this.methodConfigPart.resolveStringMaxLength(method);
    }

    @Override
    public String resolveStringFormat(FieldScope field) {
        return this.fieldConfigPart.resolveStringFormat(field);
    }

    @Override
    public String resolveStringFormat(MethodScope method) {
        return this.methodConfigPart.resolveStringFormat(method);
    }

    @Override
    public BigDecimal resolveNumberInclusiveMinimum(FieldScope field) {
        return this.fieldConfigPart.resolveNumberInclusiveMinimum(field);
    }

    @Override
    public BigDecimal resolveNumberInclusiveMinimum(MethodScope method) {
        return this.methodConfigPart.resolveNumberInclusiveMinimum(method);
    }

    @Override
    public BigDecimal resolveNumberExclusiveMinimum(FieldScope field) {
        return this.fieldConfigPart.resolveNumberExclusiveMinimum(field);
    }

    @Override
    public BigDecimal resolveNumberExclusiveMinimum(MethodScope method) {
        return this.methodConfigPart.resolveNumberExclusiveMinimum(method);
    }

    @Override
    public BigDecimal resolveNumberInclusiveMaximum(FieldScope field) {
        return this.fieldConfigPart.resolveNumberInclusiveMaximum(field);
    }

    @Override
    public BigDecimal resolveNumberInclusiveMaximum(MethodScope method) {
        return this.methodConfigPart.resolveNumberInclusiveMaximum(method);
    }

    @Override
    public BigDecimal resolveNumberExclusiveMaximum(FieldScope field) {
        return this.fieldConfigPart.resolveNumberExclusiveMaximum(field);
    }

    @Override
    public BigDecimal resolveNumberExclusiveMaximum(MethodScope method) {
        return this.methodConfigPart.resolveNumberExclusiveMaximum(method);
    }

    @Override
    public BigDecimal resolveNumberMultipleOf(FieldScope field) {
        return this.fieldConfigPart.resolveNumberMultipleOf(field);
    }

    @Override
    public BigDecimal resolveNumberMultipleOf(MethodScope method) {
        return this.methodConfigPart.resolveNumberMultipleOf(method);
    }

    @Override
    public Integer resolveArrayMinItems(FieldScope field) {
        return this.fieldConfigPart.resolveArrayMinItems(field);
    }

    @Override
    public Integer resolveArrayMinItems(MethodScope method) {
        return this.methodConfigPart.resolveArrayMinItems(method);
    }

    @Override
    public Integer resolveArrayMaxItems(FieldScope field) {
        return this.fieldConfigPart.resolveArrayMaxItems(field);
    }

    @Override
    public Integer resolveArrayMaxItems(MethodScope method) {
        return this.methodConfigPart.resolveArrayMaxItems(method);
    }

    @Override
    public Boolean resolveArrayUniqueItems(FieldScope field) {
        return this.fieldConfigPart.resolveArrayUniqueItems(field);
    }

    @Override
    public Boolean resolveArrayUniqueItems(MethodScope method) {
        return this.methodConfigPart.resolveArrayUniqueItems(method);
    }

    @Override
    public boolean isRequired(FieldScope field) {
        return this.fieldConfigPart.isRequired(field);
    }

    @Override
    public boolean isRequired(MethodScope method) {
        return this.methodConfigPart.isRequired(method);
    }

    @Override
    public Map<String, String> resolveMetadata(FieldScope field) {
        return this.fieldConfigPart.resolveMetadata(field);
    }

    @Override
    public Map<String, String> resolveMetadata(MethodScope method) {
        return this.methodConfigPart.resolveMetadata(method);
    }
}
