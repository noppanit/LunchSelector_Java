package com.thoughtworks.relationship;

import org.neo4j.graphdb.RelationshipType;

public enum MyRelationship implements RelationshipType {
    CUSTOMER,ANSWERS,ANSWERED,DISH,COMPLETED,CONTAINS,EXCLUDES,QUESTION,REQUIRES,
    RULE, LESS_THAN, GREATER_THAN, UNKNOWN_AGE, BETWEEN,
    APPLIES, PRICE, RELATED_RULE
}
