package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Question;
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

    public void iterateRules(Node customer) throws Exception {
//        List<Rule> listOfRules = getRules();
//        for( Rule rule : listOfRules)
//        {
//            if( rule.getRuleType().equals("EvaluateOn"))
////                new CustomerRepository().getCustomerDateOfBirth(customer);
//                rule.getUsing();
//            // get using Age
//                evaluateRule();
//            else
//            {
//                // do something else
//            }
//        }
    }

    public Node evaluateRule(int age) {
        Node endNode = null;
        HashMap<String, HashMap<String, String>> maps = getRuleMetadata();
        Set<String> keySet = maps.keySet();

        for (String relationshipType : keySet) {
            if (relationshipType.equals("LESS_THAN")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                int threshold = Integer.parseInt(propertyMaps.get("Threshold"));
                if (age > 0 && age < threshold) {
                    endNode = getEndNode(propertyMaps);
                }

            } else if (relationshipType.equals("GREATER_THAN")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                int threshold = Integer.parseInt(propertyMaps.get("Threshold"));
                if (age > threshold) {
                    endNode = getEndNode(propertyMaps);
                }
            } else if (relationshipType.equals("BETWEEN")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                int from = Integer.parseInt(propertyMaps.get("From"));
                int to = Integer.parseInt(propertyMaps.get("To"));
                if (from < age && age < to) {
                    endNode = getEndNode(propertyMaps);
                }
            } else if (relationshipType.equals("UNKNOWN_AGE")) {
                HashMap<String, String> propertyMaps = maps.get(relationshipType);
                if (age == 0) {
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
     */
    private HashMap<String, HashMap<String, String>> getRuleMetadata() {
        Node rule = db.getNodeById(5);

        Iterator<Relationship> relationships = rule.getRelationships(Direction.OUTGOING).iterator();

        HashMap<String, HashMap<String, String>> rules = new HashMap<String, HashMap<String, String>>();

        while (relationships.hasNext()) {
            Relationship rel = relationships.next();
            Iterator relationshipKeys = rel.getPropertyKeys().iterator();

            HashMap<String, String> ruleEvaluator = new HashMap<String, String>();
            while (relationshipKeys.hasNext()) {

                String keyName = relationshipKeys.next().toString();
                ruleEvaluator.put(keyName, rel.getProperty(keyName).toString());
            }
            ruleEvaluator.put(END_NODE_ID, String.valueOf(rel.getEndNode().getId()));
            rules.put(rel.getType().toString(), ruleEvaluator);
        }
        return rules;
    }


}
