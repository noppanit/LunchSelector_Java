package com.thoughtworks.model;

public class Rule extends NodeObject{

    private String using;
    private String ruleType;
    private String affectTo;
    private String usingDataType;

    public String getUsing() {
        return using;
    }

    public void setUsing(String using) {
        this.using = using;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getAffectTo() {
        return affectTo;
    }

    public void setAffectTo(String affectTo) {
        this.affectTo = affectTo;
    }

    public String getUsingDataType() {
        return usingDataType;
    }

    public void setUsingDataType(String usingDataType) {
        this.usingDataType = usingDataType;
    }
}
