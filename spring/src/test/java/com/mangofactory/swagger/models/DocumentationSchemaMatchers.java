package com.mangofactory.swagger.models;

import com.wordnik.swagger.model.Model;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

public class DocumentationSchemaMatchers {
    public static Matcher<Model> hasProperty(final String name, final String typeName) {
        String description = String.format("Schema should have a property %s of type %s", name, typeName);
        return new CustomTypeSafeMatcher<Model>(description) {
            @Override
            protected boolean matchesSafely(Model documentationSchema) {
                return documentationSchema.properties().containsKey(name) &&
                        documentationSchema.properties().get(name).getType().equals(typeName);
            }
        };
    }
}
