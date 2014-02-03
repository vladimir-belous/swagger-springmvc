package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.mangofactory.swagger.AliasedResolvedField;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.model.Model;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Models.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class ResolvedTypeMemberVisitor implements MemberVisitor {

    private final SchemaProvider context;

    public ResolvedTypeMemberVisitor(SchemaProvider context) {
        this.context = context;
    }

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider input) {
                return new ResolvedTypeMemberVisitor(input);
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Model schema(MemberInfoSource member) {
        if (context.getSchemaMap().containsKey(modelName(member.getResolvedType()))) {
            Model schema = new Model();
            schema.setType(modelName(member.getResolvedType()));
            schema.setName(member.getName());
            return schema;
        }
        ResolvedType resolvedMember = member.getResolvedType();
        Class<?> erasedClass = resolvedMember.getErasedType();
        if (resolvedMember.getTypeParameters().size() == 0) {
            if (resolvedMember.isPrimitive() || isPrimitive(resolvedMember.getErasedType())) {
                return PrimitiveMemberVisitor.factory().apply(context).schema(new PrimitiveMemberInfo(erasedClass));
            } else if (EnumHelper.isEnum(resolvedMember.getErasedType())) {
                Model schema = new Model();
                schema.setType(modelName(resolvedMember));
                schema.setName(resolvedMember.getErasedType().getName());
                DocumentationAllowableListValues list = new DocumentationAllowableListValues();
                list.setValues(EnumHelper.getEnumValues(resolvedMember.getErasedType()));
                schema.setAllowableValues(list);
                schema.setProperties(new HashMap<String, Model>());
                context.getSchemaMap().put(schema.getType(), schema);
                return schema;
            } else if (resolvedMember.getErasedType() == Object.class) {
                return null;
            }
        }
        if (ResolvedCollection.isList(member)) {
            Model schema = new Model();
            schema.setType("List");
            schema.setName(member.getName());
            ResolvedType resolvedType = ResolvedCollection.listElementType(member);
            Model itemSchema = context.schema(resolvedType);
            Model itemSchemaRef = new Model();
            if (itemSchema != null) {
                itemSchemaRef.ref_$eq(itemSchema.getType());
            } else {
                itemSchemaRef.ref_$eq("any");
            }
            schema.setItems(itemSchemaRef);
            return schema;
        }
        if (ResolvedCollection.isSet(member)) {
            Model schema = new Model();
            schema.setType("Set");
            schema.setName(member.getName());
            ResolvedType resolvedType = ResolvedCollection.setElementType(member);
            Model itemSchema = context.schema(resolvedType);
            Model itemSchemaRef = new Model();
            if (itemSchema != null) {
                itemSchemaRef.ref_$eq(itemSchema.getType());
            } else {
                itemSchemaRef.ref_$eq("any");
            }
            schema.setItems(itemSchemaRef);
            return schema;
        }

        Model objectSchema = new Model();
        objectSchema.setName(member.getName());
        objectSchema.setType(modelName(resolvedMember));
        context.getSchemaMap().put(modelName(resolvedMember), objectSchema);
        Map<String, Model> propertyMap = newHashMap();
        for (AliasedResolvedField childField: context.getResolvedFields(resolvedMember)){
            Model childSchema = context.schema(childField.getResolvedField());
            if (childSchema != null) {
                propertyMap.put(childField.getName(), childSchema);
            }
        }
        for (ResolvedPropertyInfo childProperty: context.getResolvedProperties(resolvedMember)) {
            Model childPropertySchema = context.schema(childProperty);
            if (childPropertySchema != null) {
                propertyMap.put(childProperty.getName(), childPropertySchema);
            }
        }
        objectSchema.setProperties(propertyMap);
        return objectSchema;
    }
}
