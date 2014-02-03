package com.mangofactory.swagger.filters;

import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.web.method.HandlerMethod;
import scala.Option;

import java.util.List;

public class AnnotatedErrorsFilter implements Filter<List<ResponseMessage>> {
    @Override
    public void apply(FilterContext<List<ResponseMessage>> context) {
        List<ResponseMessage> errors = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");

        discoverSwaggerAnnotatedExceptions(errors, handlerMethod);
    }

    private void discoverSwaggerAnnotatedExceptions(List<ResponseMessage> errors, HandlerMethod handlerMethod) {
        ApiResponses apiErrors = handlerMethod.getMethodAnnotation(ApiResponses.class);
        if (apiErrors == null) {
            return;
        }
        for (ApiResponse apiError : apiErrors.value()) {
            errors.add(new ResponseMessage(apiError.code(), apiError.message(), Option.<String>empty()));
        }
    }
}
