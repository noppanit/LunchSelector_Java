package com.thoughtworks.util;

import com.thoughtworks.model.NodeObject;
import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ListHelper {

    public static <T> List<T> substracts(List<T> supersetNode, List<T> subsetNode) {

        for (Iterator<T> it =  supersetNode.iterator(); it.hasNext();) {

            NodeObject superNode = (NodeObject) it.next();
            for (Iterator<T> it1 = subsetNode.iterator(); it1.hasNext();) {
                NodeObject subNode = (NodeObject) it1.next();
                if (superNode.getName().equals(subNode.getName())) {
                    it.remove();
                }

            }
        }

        return supersetNode;
    }

    public static List<? extends NodeObject> convertNodesToNodeObjects(Collection<Node> nodes) {
        List<NodeObject> listOfQuestions = new ArrayList<NodeObject>();
        for (Node node : nodes) {

            NodeObject nodeObject = new NodeObject();
            nodeObject.setName(node.getProperty("name").toString());
            nodeObject.setId(node.getId());
            listOfQuestions.add(nodeObject);
        }
        return listOfQuestions;
    }
}
