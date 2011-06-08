package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Rule;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

import java.util.*;

public class RuleRepository {

    private static final String END_NODE_ID = "EndNode";
    private DatabaseHelper db = DatabaseHelper.getInstance();

    public boolean hasRule() {

        Node rule = db.getRuleNode();
        return rule.hasRelationship(MyRelationship.RULE, Direction.OUTGOING);
    }

    public List<Rule> getRules() throws Exception {
        Node questionNode = db.getRuleNode();
        Traverser traverse = questionNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.RULE, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Rule> allRules = ListHelper.setSpecialProperties(nodes, new Rule());
        return allRules;
    }

    public Node evaluateRule(int value, String using) {
        Node endNode = null;
        HashMap<RelationshipType, HashMap<String, String>> maps = null;

        try {
            maps = getRuleMetadata(using);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<RelationshipType> keySet = maps.keySet();

        for (RelationshipType relationshipType : keySet) {
            if (relationshipType.name().equals("LESS_THAN")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                int threshold = Integer.parseInt(propertyMaps.get("Threshold"));
                if (value > 0 && value < threshold) {
                    endNode = getEndNode(propertyMaps);
                }

            } else if (relationshipType.name().equals("GREATER_THAN")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                int threshold = Integer.parseInt(propertyMaps.get("Threshold"));
                if (value > threshold) {
                    endNode = getEndNode(propertyMaps);
                }
            } else if (relationshipType.name().equals("BETWEEN")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                int from = Integer.parseInt(propertyMaps.get("From"));
                int to = Integer.parseInt(propertyMaps.get("To"));
                if (from < value && value < to) {
                    endNode = getEndNode(propertyMaps);
                }
            } else if (relationshipType.name().equals("UNKNOWN_AGE")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                if (value == 0) {
                    endNode = getEndNode((propertyMaps));
                }
            }
        }
        return endNode;
    }

    private Node getEndNode(HashMap<String, String> propertyMaps) {
        Node endNode;
        String endNodeId = propertyMaps.get(END_NODE_ID);
        endNode = db.getNodeById(Long.parseLong(endNodeId));
        return endNode;
    }

    /**
     * Could get endNode by the key "EndNode"
     *
     * @return
     * @param using
     */
    private HashMap<RelationshipType, HashMap<String, String>> getRuleMetadata(String using) throws Exception {

        Node theRule = getRuleByUsing(using);

        Iterator<Relationship> relationships = theRule.getRelationships(Direction.OUTGOING).iterator();

        HashMap<RelationshipType, HashMap<String, String>> rules = new HashMap<RelationshipType, HashMap<String, String>>();

        while (relationships.hasNext()) {
            Relationship rel = relationships.next();
            Iterator relationshipKeys = rel.getPropertyKeys().iterator();

            HashMap<String, String> ruleEvaluator = new HashMap<String, String>();
            while (relationshipKeys.hasNext()) {

                String keyName = relationshipKeys.next().toString();
                ruleEvaluator.put(keyName, rel.getProperty(keyName).toString());
            }
            ruleEvaluator.put(END_NODE_ID, String.valueOf(rel.getEndNode().getId()));
            rules.put(rel.getType(), ruleEvaluator);
        }
        return rules;
    }

    private Node getRuleByUsing(String using) throws Exception {
        Node theRule = null;
        List<Rule> listOfRules = getRules();
        for(Rule rule : listOfRules)
        {
            if(rule.getUsing().equals(using))
            {
                theRule = db.getNodeById(rule.getId());
            }
        }
        return theRule;
    }


}
