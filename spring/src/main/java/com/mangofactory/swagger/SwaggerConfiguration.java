package com.mangofactory.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.configuration.DefaultConfigurationModule;
import com.mangofactory.swagger.configuration.ExtensibilityModule;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.TypeProcessingRule;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ApiListingReference;
import com.wordnik.swagger.model.AuthorizationType;
import com.wordnik.swagger.model.Operation;
import com.wordnik.swagger.model.Parameter;
import com.wordnik.swagger.model.ResourceListing;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import scala.Option;
import scala.collection.mutable.ListBuffer;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.filters.Filters.Fn.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.WildcardType.*;

@Component
public class SwaggerConfiguration {
    public static final String API_DOCS_PATH = "/api-docs";
    public static final String SWAGGER_VERSION = "1.0";

    private final String documentationBasePath;
    private final String swaggerVersion;

    private final List<Filter<ResourceListing>> documentationFilters = newArrayList();
    private final List<Filter<ApiListing>> endpointFilters = newArrayList();
    private final List<Filter<Operation>> operationFilters = newArrayList();
    private final List<Filter<Parameter>> parameterFilters = newArrayList();
    private final List<Filter<List<ResponseMessage>>> errorFilters = newArrayList();
    private final List<TypeProcessingRule> typeProcessingRules = newArrayList();

    @Autowired private DocumentationTransformer documentationTransformer;
    @Autowired private DocumentationSchemaProvider schemaProvider;
    @Autowired private TypeResolver typeResolver;
    @Autowired private DefaultConfigurationModule defaultConfig;
    @Autowired private ExtensibilityModule extensibility;

    private String apiVersion;
    private String basePath;
    private List<String> excludedResources = newArrayList();

    @PostConstruct
    public void applyCustomizations() {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration(apiVersion, basePath);
        extensibility.apply(defaultConfig.apply(swaggerConfiguration));
    }

    @Autowired
    public SwaggerConfiguration(@Value("${documentation.services.version}") String apiVersion,
                                @Value("${documentation.services.basePath}") String basePath) {
        this.swaggerVersion = SWAGGER_VERSION;
        this.documentationBasePath = API_DOCS_PATH;
        this.basePath = basePath;
        this.apiVersion = apiVersion;
    }

    public ResourceListing newDocumentation(WebApplicationContext webApplicationContext) {
        FilterContext<ResourceListing> context = new FilterContext<ResourceListing>(new ResourceListing(apiVersion,
                swaggerVersion, new ListBuffer<ApiListingReference>().readOnly(), new ListBuffer<AuthorizationType>()
                .readOnly(), Option.<ApiInfo>empty()));
        context.put("swagger", this);
        context.put("webApplicationContext", webApplicationContext);
        applyFilters(documentationFilters, context);
        return context.subject();
    }

    public boolean isExcluded(List<String> documentationEndpointUris) {
        if (documentationEndpointUris == null) {
            return false;
        }
        for(String uri: documentationEndpointUris) {
            if(isNullOrEmpty(uri)) {
                return false;
            }
            String controllerUri = uri;
            if (uri.contains(API_DOCS_PATH)) {
               controllerUri = uri.substring(API_DOCS_PATH.length());
            }
            if (excludedResources.contains(controllerUri)) {
                return true;
            }
       }
       return false;
    }

    public boolean isParameterTypeIgnorable(final ResolvedType type) {
        TypeProcessingRule rule = findProcessingRule(type);
        return rule.isIgnorable();
    }

    public ResolvedType maybeGetAlternateType(final ResolvedType parameterType) {
        TypeProcessingRule rule = findProcessingRule(parameterType);
        return rule.alternateType(parameterType);
    }

    private TypeProcessingRule findProcessingRule(final ResolvedType parameterType) {
        return find(typeProcessingRules, new Predicate<TypeProcessingRule>() {
            @Override
            public boolean apply(TypeProcessingRule input) {
                return (hasWildcards(input.originalType()) && wildcardMatch(parameterType, input.originalType()))
                        || exactMatch(input.originalType(), parameterType);
            }
        }, new DefaultProcessingRule(parameterType.getErasedType()));
    }

    public DocumentationSchemaProvider getSchemaProvider() {
        if (schemaProvider == null) {
            schemaProvider = new DocumentationSchemaProvider(getTypeResolver(), this);
        }
        return schemaProvider;
    }

    public TypeResolver getTypeResolver() {
        if (typeResolver == null) {
            typeResolver = new TypeResolver();
        }
        return typeResolver;
    }

    public List<String> getExcludedResources() {
        return excludedResources;
    }

    public String getDocumentationBasePath() {
        return documentationBasePath;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public DocumentationTransformer getDocumentationTransformer() {
        return documentationTransformer;
    }

    public void setDocumentationTransformer(DocumentationTransformer documentationTransformer) {
        this.documentationTransformer = documentationTransformer;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }
    public String getBasePath() {
        return basePath;
    }
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<Filter<ResourceListing>> getDocumentationFilters() {
        return documentationFilters;
    }

    public List<Filter<ApiListing>> getEndpointFilters() {
        return endpointFilters;
    }

    public List<Filter<Operation>> getOperationFilters() {
        return operationFilters;
    }

    public List<Filter<Parameter>> getParameterFilters() {
        return parameterFilters;
    }

    public List<Filter<List<ResponseMessage>>> getErrorFilters() {
        return errorFilters;
    }

    public List<TypeProcessingRule> getTypeProcessingRules() {
        return typeProcessingRules;
    }

    static class DefaultProcessingRule implements TypeProcessingRule {
        private Class<?> clazz;

        public DefaultProcessingRule(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean isIgnorable() {
            return false;
        }

        @Override
        public boolean hasAlternateType() {
            return false;
        }

        @Override
        public ResolvedType originalType() {
            return asResolvedType(clazz);
        }

        @Override
        public ResolvedType alternateType(ResolvedType parameterType) {
            return parameterType;
        }
    }
}
