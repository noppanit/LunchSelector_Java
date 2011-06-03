package com.thoughtworks.model;

public class Menu extends NodeObject{
    private String child;
    private String regular;
    private String pensioner;

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getPensioner() {
        return pensioner;
    }

    public void setPensioner(String pensioner) {
        this.pensioner = pensioner;
    }
}
