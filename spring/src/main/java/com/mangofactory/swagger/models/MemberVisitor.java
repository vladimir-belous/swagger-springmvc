package com.mangofactory.swagger.models;

public interface MemberVisitor {
    com.wordnik.swagger.model.Model schema(MemberInfoSource property);
}
