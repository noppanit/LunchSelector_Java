package com.thoughtworks.util;

import com.thoughtworks.model.NodeObject;

import java.util.Iterator;
import java.util.List;

public class ListHelper {
    public List<? extends NodeObject> substracts(List<? extends NodeObject> supersetNode, List<? extends NodeObject> subsetNode) {

        for (Iterator<NodeObject> it = (Iterator<NodeObject>) supersetNode.iterator(); it.hasNext();) {

            NodeObject superNode = it.next();
            for (Iterator<NodeObject> it1 = (Iterator<NodeObject>) subsetNode.iterator(); it1.hasNext();) {
                NodeObject subNode = it1.next();
                if (superNode.getName().equals(subNode.getName())) {
                    it.remove();
                }

            }
        }

        return supersetNode;
    }
}
