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

package com.github.victools.jsonschema.generator;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Entry point for customising a JSON Schema being generated for a certain kind of reference/context.
 *
 * @param <M> type of the reference/context to modify
 */
public interface InstanceAttributeOverride<M extends MemberScope<?, ?>> {

    /**
     * Add/remove attributes on the given JSON Schema node – this is specifically intended for attributes relating to a particular instance.
     * <br>
     * E.g. "{@value SchemaConstants#TAG_DESCRIPTION}", "{@value SchemaConstants#TAG_MINIMUM}", "{@value SchemaConstants#TAG_MAXIMUM_EXCLUSIVE}"
     *
     * @param jsonSchemaAttributesNode node to modify
     * @param member reference/context to which the collected attributes in the JSON Schema node are referring
     */
    void overrideInstanceAttributes(ObjectNode jsonSchemaAttributesNode, M member);
}
