package com.mangofactory.swagger;

import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.Model;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.Operation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import scala.collection.JavaConversions;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Models.Fn.*;

@XmlRootElement
public class ControllerDocumentation {

    private final List<ApiDescription> endpoints = newArrayList();
    private final Map<String, Model> modelMap = newHashMap();
    private DocumentationSchemaProvider schemaProvider;
    private HashMap<String, com.wordnik.swagger.model.Model> models;


    //Used by JAXB
    ControllerDocumentation() {
    }

    public ControllerDocumentation(String apiVersion, String swaggerVersion,
                                   String resourceUri, DocumentationSchemaProvider schemaProvider) {

//        super(apiVersion, swaggerVersion, basePath, resourceUri);
        this.schemaProvider = schemaProvider;
    }


    public void addEndpoint(ApiDescription endpoint) {
        endpoints.add(endpoint);
    }

    public void putModel(String name, Model model) {
        modelMap.put(name, model);
    }

    public Boolean matchesName(String name) {
        String nameWithForwardSlash = (name.startsWith("/")) ? name : "/" + name;
        String nameWithoutForwardSlash = (name.startsWith("/")) ? name.substring(1) : name;

        return getResourcePath().equals(nameWithoutForwardSlash) ||
                       getResourcePath().equals(nameWithForwardSlash);
    }

    public List<Operation> getEndPoint(String requestUri, RequestMethod method) {
        List<Operation> operations = newArrayList();
        for (ApiDescription endPoint : endpoints) {
            if (StringUtils.equals(endPoint.path(), requestUri)) {
                for (Operation operation : JavaConversions.asJavaList(endPoint.operations())) {
                    if (operation.method().equals(method.name())) {
                        operations.add(operation);
                    }
                }
            }
        }
        return operations;
    }

    public List<ApiDescription> getApis() {
        return endpoints;
    }

    public HashMap<String, com.wordnik.swagger.model.Model> getModels() {
        if (models == null) {
            models = newHashMap();
            for (Model model: modelMap.values()) {
                models.putAll(modelToSchema(schemaProvider).apply(model));
            }
            //Free-up the memory of the types
            modelMap.clear();
        }
        return models;
    }
}
