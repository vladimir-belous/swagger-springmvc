package com.mangofactory.swagger.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.AnnotatedEndpointFilter;
import com.mangofactory.swagger.filters.AnnotatedErrorsFilter;
import com.mangofactory.swagger.filters.AnnotatedOperationFilter;
import com.mangofactory.swagger.filters.AnnotatedParameterFilter;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.models.WildcardType;
import com.mangofactory.swagger.spring.filters.ApplicationFilter;
import com.mangofactory.swagger.spring.filters.EndPointFilter;
import com.mangofactory.swagger.spring.filters.ErrorsFilter;
import com.mangofactory.swagger.spring.filters.OperationFilter;
import com.mangofactory.swagger.spring.filters.ParameterFilter;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.Operation;
import com.wordnik.swagger.core.Parameter;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.AlternateTypeProcessingRule.*;
import static com.mangofactory.swagger.models.IgnorableTypeRule.*;

public class DefaultConfigurationModule {
    public SwaggerConfiguration apply(SwaggerConfiguration configuration) {
        Filter<Documentation> applicationDocumentationFilter = new ApplicationFilter();
        configuration.getDocumentationFilters().add(applicationDocumentationFilter);

        Filter<DocumentationEndPoint> endPointFilter = new EndPointFilter();
        Filter<DocumentationEndPoint> annotationEndpointFilter = new AnnotatedEndpointFilter();
        configuration.getEndpointFilters().addAll(newArrayList(endPointFilter, annotationEndpointFilter));

        Filter<Operation> operationFilter = new OperationFilter();
        Filter<Operation> annotatedOperationFilter = new AnnotatedOperationFilter();
        configuration.getOperationFilters().addAll(newArrayList(operationFilter, annotatedOperationFilter));

        Filter<Parameter> parameterFilter = new ParameterFilter();
        Filter<Parameter> annotatedParameterFilter = new AnnotatedParameterFilter();
        configuration.getParameterFilters().addAll(newArrayList(parameterFilter, annotatedParameterFilter));

        Filter<List<DocumentationError>> errorFilter = new ErrorsFilter();
        Filter<List<DocumentationError>> annotatedErrorFilter = new AnnotatedErrorsFilter();
        configuration.getErrorFilters().addAll(newArrayList(errorFilter, annotatedErrorFilter));

        TypeResolver typeResolver = configuration.getTypeResolver();
        configuration.getTypeProcessingRules()
                .addAll(newArrayList(ignorable(ModelMap.class),
                        ignorable(ServletContext.class),
                        ignorable(HttpServletRequest.class),
                        ignorable(HttpServletResponse.class),
                        alternate(typeResolver.resolve(Map.class),
                                typeResolver.resolve(Object.class)),
                        alternate(typeResolver.resolve(Map.class, String.class, Object.class),
                                typeResolver.resolve(Object.class)),
                        alternate(typeResolver.resolve(Map.class, Object.class, Object.class),
                                typeResolver.resolve(Object.class)),
                        alternate(typeResolver.resolve(Map.class, String.class, String.class),
                                typeResolver.resolve(Object.class)),
                        hashmapAlternate(WildcardType.class, WildcardType.class)
                ));

        return configuration;
    }

}
