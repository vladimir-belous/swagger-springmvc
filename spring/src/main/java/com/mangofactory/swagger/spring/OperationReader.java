package com.mangofactory.swagger.spring;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.filters.Filters;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.Operation;
import com.wordnik.swagger.core.Parameter;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class OperationReader {
    private final SwaggerConfiguration configuration;

    public OperationReader(SwaggerConfiguration configuration) {
        this.configuration = configuration;
    }

    Operation readOperation(ControllerDocumentation controllerDocumentation, HandlerMethod handlerMethod,
                                         ParamsRequestCondition paramsCondition, RequestMethod requestMethod) {
        Operation operation = new Operation(requestMethod.name(), "", "");
        FilterContext<Operation> operationContext = new FilterContext<Operation>(operation);
        operationContext.put("handlerMethod", handlerMethod);
        operationContext.put("controllerDocumentation", controllerDocumentation);
        operationContext.put("swaggerConfiguration", configuration);
        Filters.Fn.applyFilters(configuration.getOperationFilters(), operationContext);
        List<ResolvedType> resolvedParameters = methodParameters(configuration.getTypeResolver(),
                handlerMethod.getMethod());
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        String [] parameterNames = getParameterNames(handlerMethod, methodParameters.length);
        for (int index = 0; index < handlerMethod.getMethodParameters().length; index++) {
            Parameter parameter = new Parameter();
            ResolvedType resolvedType = configuration.maybeGetAlternateType(resolvedParameters.get(index));
            if (resolvedParameters.size() == 0
                    || configuration.isParameterTypeIgnorable(resolvedType)) {
                continue;
            }
            FilterContext<Parameter> parameterContext
                    = new FilterContext<Parameter>(parameter);
            parameterContext.put("methodParameter", methodParameters[index]);
            parameterContext.put("parameterType", resolvedType);
            parameterContext.put("defaultParameterName", parameterNames[index]);
            parameterContext.put("controllerDocumentation", controllerDocumentation);
            Filters.Fn.applyFilters(configuration.getParameterFilters(), parameterContext);
            operation.addParameter(parameter);
        }
        for (NameValueExpression<String> expression : paramsCondition.getExpressions()) {
            if (expression.isNegated() || any(nullToEmptyList(operation.getParameters()),
                    withName(expression.getName()))) {
                continue;
            }
            Parameter parameter = new Parameter();
            parameter.setDataType("String");
            parameter.setName(expression.getName());
            parameter.setDefaultValue(expression.getValue());
            parameter.setRequired(true);
            parameter.setParamType("query");
            parameter.setAllowableValues(new DocumentationAllowableListValues(newArrayList(expression.getValue())));
            operation.addParameter(parameter);
        }

        List<DocumentationError> errors = newArrayList();
        FilterContext<List<DocumentationError>> errorContext = new FilterContext<List<DocumentationError>>(errors);
        errorContext.put("handlerMethod", handlerMethod);
        Filters.Fn.applyFilters(configuration.getErrorFilters(), errorContext);
        for (DocumentationError error : errors) {
            operation.addErrorResponse(error);
        }
        return operation;
    }

    private Iterable<Parameter> nullToEmptyList(List<Parameter> parameters) {
        if (parameters == null) {
            return newArrayList();
        }
        return parameters;
    }

    private String[] getParameterNames(HandlerMethod handlerMethod, int length) {
        String[] parameterNames
                = new LocalVariableTableParameterNameDiscoverer().getParameterNames(handlerMethod.getMethod());
        //Makeup names if it is null
        if (parameterNames == null) {
            parameterNames = new String[length];
            for (int index = 0; index < length; index++) {
                parameterNames[index] = String.format("p%s", index);
            }
        }
        return parameterNames;
    }

    private Predicate<? super Parameter> withName(final String name) {
        return new Predicate<Parameter>() {
            @Override
            public boolean apply(Parameter input) {
                return Objects.equal(input.getName(), name);
            }
        };
    }
}
