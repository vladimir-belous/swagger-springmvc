package com.mangofactory.swagger.filters;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.model.ApiListing;

public class AnnotatedEndpointFilter implements Filter<ApiListing> {
    @Override
    public void apply(FilterContext<ApiListing> context) {
        ApiListing doc = context.subject();
        Class<?> controllerClass = context.get("controllerClass");
        doc.description(getDescription(controllerClass));
    }


    private String getDescription(Class<?> controllerClass) {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null) {
            return "";
        }
        return apiAnnotation.description();

    }

}
