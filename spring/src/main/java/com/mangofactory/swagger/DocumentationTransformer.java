package com.mangofactory.swagger;

import com.google.common.annotations.VisibleForTesting;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import scala.collection.JavaConversions;

import java.util.Collections;
import java.util.Comparator;

public abstract class DocumentationTransformer {
    private Comparator<ApiDescription> endPointComparator;
    private Comparator<Operation> operationComparator;

    @Autowired
    public DocumentationTransformer(EndpointComparator endPointComparator, OperationComparator operationComparator) {
        this.endPointComparator = endPointComparator;
        this.operationComparator = operationComparator;
    }

    /***
     * This is an extensibility point that allows post-processing of the generated documentation
     * Overriding this method allows a documentation object elements to be sorted based on customer requirements
     * @param transformed After a documentation is transformed the new documentation model can be sorted
     * @return returns a document with APIs sorted based on provided comparator. It also sorts operations based on
     * provided comparator.
     *
     * The simplest form of extensibility is providing an Comparator&lt;DocumentationEndpoint&gt; and/or
     * a Comparator&lt;Operation&gt; via the swagger configuration extensions
     */
    public ApiListing applySorting(ApiListing transformed) {
        if (endPointComparator != null && transformed.apis() != null) {
            Collections.sort(JavaConversions.asJavaList(transformed.apis()), endPointComparator);
            for (ApiDescription endpoint : JavaConversions.asJavaList(transformed.apis())) {
                if (operationComparator != null && endpoint.operations() != null) {
                    Collections.sort(JavaConversions.asJavaList(endpoint.operations()), operationComparator);
                }
            }
        }
        return transformed;
    }

    /***
     * This is an extensibility point that allows post-processing of the generated documentation
     * Overriding this method allows a documentation object to be transformed
     * @param documentation The input is the default documentation that is generated
     * @return transformed documentation object
     * For e.g. this extensibility can be used to group operations in a different way rather than the default
     * controller based grouping.
     */
    public abstract ApiListing applyTransformation(ApiListing documentation);

    @VisibleForTesting
    void setEndPointComparator(Comparator<ApiDescription> endPointComparator) {
        this.endPointComparator = endPointComparator;
    }

    @VisibleForTesting
    void setOperationComparator(Comparator<Operation> operationComparator) {
        this.operationComparator = operationComparator;
    }
}
