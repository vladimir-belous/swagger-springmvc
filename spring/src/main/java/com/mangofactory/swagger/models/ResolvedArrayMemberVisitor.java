package com.mangofactory.swagger.models;

import com.fasterxml.classmate.types.ResolvedArrayType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.wordnik.swagger.model.Model;

import static com.mangofactory.swagger.models.ResolvedTypes.modelName;

public class ResolvedArrayMemberVisitor implements MemberVisitor {
    private final SchemaProvider context;

    public ResolvedArrayMemberVisitor(SchemaProvider context) {
        this.context = context;
    }

    public static Function<SchemaProvider, MemberVisitor> factory() {
        return new Function<SchemaProvider, MemberVisitor>() {
            @Override
            public MemberVisitor apply(SchemaProvider input) {
                return new ResolvedArrayMemberVisitor(input);
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Model schema(MemberInfoSource member) {
        Preconditions.checkArgument(member.getResolvedType() instanceof ResolvedArrayType);
        if (context.getSchemaMap().containsKey(member.getType().getSimpleName())) {
            Model schema = new Model();
            schema.setType(modelName(member.getResolvedType()));
            schema.setName(member.getName());
            return schema;
        }
        ResolvedArrayType resolvedArrayType = (ResolvedArrayType) member.getResolvedType();
        Model schema = new Model();
        schema.setType("Array");
        schema.setName(member.getName());
        Model itemSchema = context.schema(resolvedArrayType.getArrayElementType());
        Model itemSchemaRef = new Model();
        itemSchemaRef.ref_$eq(itemSchema.getType());
        schema.setItems(itemSchemaRef);
        return schema;

    }
}
