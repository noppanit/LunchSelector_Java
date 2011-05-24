package com.thoughtworks.repository;

import com.thoughtworks.model.Menu;
import com.thoughtworks.model.NodeObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseTest {

    protected Collection<String> getNodeNames(List<? extends NodeObject> dishes) {
        Collection<String> nodeNames = new ArrayList<String>();
        for( NodeObject node: dishes )
        {
            nodeNames.add(node.getName());
        }
        return nodeNames;
    }
}
