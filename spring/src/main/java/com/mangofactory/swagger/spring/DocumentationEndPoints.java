package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.wordnik.swagger.model.ResourceListing;

public class DocumentationEndPoints {
    public static ControllerDocumentation asDocumentation(ResourceListing parent, String resourcePath,
                                                          DocumentationSchemaProvider schemaProvider) {
        return new ControllerDocumentation(parent.apiVersion(), parent.swaggerVersion(),
                resourcePath, schemaProvider);
    }
}
