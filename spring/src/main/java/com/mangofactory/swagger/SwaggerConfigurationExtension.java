package com.mangofactory.swagger;

import com.mangofactory.swagger.filters.Filter;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.Operation;
import com.wordnik.swagger.model.Parameter;
import com.wordnik.swagger.model.ResourceListing;
import com.wordnik.swagger.model.ResponseMessage;

import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.*;

public class SwaggerConfigurationExtension {
    private List<Filter<ResourceListing>> documentationFilters = newArrayList();
    private List<Filter<ApiListing>> endpointFilters = newArrayList();
    private List<Filter<Operation>> operationFilters = newArrayList();
    private List<Filter<Parameter>> parameterFilters = newArrayList();
    private List<Filter<List<ResponseMessage>>> errorFilters = newArrayList();
    private List<Class<?>> ignorableParameterTypes = newArrayList();
    private DocumentationTransformer documentationTransformer;
    private Comparator<ApiListing> endPointComparator;
    private Comparator<Operation> operationComparator;

    public List<Filter<ResourceListing>> getDocumentationFilters() {
        return documentationFilters;
    }

    public void setDocumentationFilters(List<Filter<ResourceListing>> documentationFilters) {
        this.documentationFilters = documentationFilters;
    }

    public List<Filter<ApiListing>> getEndpointFilters() {
        return endpointFilters;
    }

    public void setEndpointFilters(List<Filter<ApiListing>> endpointFilters) {
        this.endpointFilters = endpointFilters;
    }

    public List<Filter<Operation>> getOperationFilters() {
        return operationFilters;
    }

    public void setOperationFilters(List<Filter<Operation>> operationFilters) {
        this.operationFilters = operationFilters;
    }

    public List<Filter<Parameter>> getParameterFilters() {
        return parameterFilters;
    }

    public void setParameterFilters(List<Filter<Parameter>> parameterFilters) {
        this.parameterFilters = parameterFilters;
    }

    public List<Filter<List<ResponseMessage>>> getErrorFilters() {
        return errorFilters;
    }

    public void setErrorFilters(List<Filter<List<ResponseMessage>>> errorFilters) {
        this.errorFilters = errorFilters;
    }

    public List<Class<?>> getIgnorableParameterTypes() {
        return ignorableParameterTypes;
    }

    public void setIgnorableParameterTypes(List<Class<?>> ignorableParameterTypes) {
        this.ignorableParameterTypes = ignorableParameterTypes;
    }

    public DocumentationTransformer getDocumentationTransformer() {
        return documentationTransformer;
    }

    public void setDocumentationTransformer(DocumentationTransformer documentationTransformer) {
        this.documentationTransformer = documentationTransformer;
    }

    public Comparator<ApiListing> getEndPointComparator() {
        return endPointComparator;
    }

    public void setEndPointComparator(Comparator<ApiListing> endPointComparator) {
        this.endPointComparator = endPointComparator;
    }

    public Comparator<Operation> getOperationComparator() {
        return operationComparator;
    }

    public void setOperationComparator(Comparator<Operation> operationComparator) {
        this.operationComparator = operationComparator;
    }
}
