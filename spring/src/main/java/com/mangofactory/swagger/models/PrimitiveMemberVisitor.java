package com.mangofactory.swagger.models;

import com.google.common.base.Function;
import com.wordnik.swagger.model.Model;

public class PrimitiveMemberVisitor implements MemberVisitor {
    private static MemberVisitor instance = new PrimitiveMemberVisitor();

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider schemaProvider) {
                return instance;
            }
        };
    }

    @Override
    public Model schema(MemberInfoSource member) {
        Class<?> returnType = member.getType();
        String propertyType;
        if (returnType.isAssignableFrom(int.class) || returnType.isAssignableFrom(Integer.class)
                || returnType.isAssignableFrom(short.class) || returnType.isAssignableFrom(Short.class)) {
            propertyType = "int";
        } else {
            propertyType = returnType.getSimpleName().toLowerCase();
        }
        Model propertySchema = new Model();
        propertySchema.setName(member.getName());
        propertySchema.setType(propertyType);
        return propertySchema;
    }
}
