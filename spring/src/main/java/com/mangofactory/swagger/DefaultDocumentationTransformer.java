package com.mangofactory.swagger;

import com.wordnik.swagger.model.ApiListing;


public class DefaultDocumentationTransformer extends DocumentationTransformer {

    public DefaultDocumentationTransformer(EndpointComparator endPointComparator,
                                           OperationComparator operationComparator) {
        super(endPointComparator, operationComparator);
    }

    @Override
    public ApiListing applyTransformation(ApiListing documentation) {
       return documentation;
    }
}
