package com.mangofactory.swagger.models;

import com.google.common.base.Function;
import com.wordnik.swagger.model.Model;

public class ObjectMemberVisitor implements MemberVisitor {
    private static MemberVisitor instance = new ObjectMemberVisitor();

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
        Model propertySchema = new Model();
        propertySchema.setName(member.getName());
        return propertySchema;
    }
}
