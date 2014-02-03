package com.mangofactory.swagger.models;

import com.google.common.base.Function;
import com.wordnik.swagger.model.Model;

public class DateMemberVisitor implements MemberVisitor {
    private static MemberVisitor instance = new DateMemberVisitor();

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider context) {
                return instance;
            }
        };
    }

    @Override
    public Model schema(MemberInfoSource member) {
        Class<?> returnType = member.getType();
        String propertyType = returnType.getSimpleName();
        Model propertySchema = new Model();
        propertySchema.setName(member.getName());
        propertySchema.setType(propertyType);
        return propertySchema;
    }
}
