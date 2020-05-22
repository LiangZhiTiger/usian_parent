package com.usian.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleContent;

import java.io.Serializable;
import java.util.List;

public class CatNode implements Serializable {
    @JsonProperty("n")
    private String name;
    @JsonProperty("i")
    private List<?> item;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<?> getItem() {
        return item;
    }

    public void setItem(List<?> item) {
        this.item = item;
    }
}
