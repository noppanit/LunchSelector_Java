package com.thoughtworks.repository;

import com.thoughtworks.model.Menu;
import com.thoughtworks.model.NodeObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseTest {

    protected <T> Collection<String> getNodeNames(List<T> dishes) {
        Collection<String> nodeNames = new ArrayList<String>();
        for( T node: dishes )
        {
            NodeObject nodeObject = (NodeObject) node;
            nodeNames.add(nodeObject.getName());
        }
        return nodeNames;
    }
}
