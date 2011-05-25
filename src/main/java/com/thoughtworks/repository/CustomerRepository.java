package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;
import org.neo4j.graphmatching.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerRepository {

    private DatabaseHelper db = DatabaseHelper.getInstance();

    public List<Customer> getCustomers() {

        Node customerNode = db.getCustomerNode();
        Traverser traverse = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.CUSTOMER, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Customer> allCustomers = new ArrayList<Customer>();
        for (Node node : nodes) {

            Customer customer = new Customer();
            customer.setName(node.getProperty("name").toString());

            allCustomers.add(customer);
        }
        return allCustomers;
    }

    public List<Menu> getPersonalisedMenu(Node customerNode) {

        List<Menu> listOfHotOrColdDishes = getDishesOfFirstQuestion(customerNode);
        List<Menu> listOfExcludedDishes = getDishesOfSecondQuestion(customerNode);

        return (List<Menu>) new ListHelper().substracts(listOfHotOrColdDishes, listOfExcludedDishes);
    }

    private List<Menu> getDishesOfSecondQuestion(Node customerNode) {
        PatternNode customerPatternNode = new PatternNode();

        PatternNode secondQuestionNode = new PatternNode();
        PatternRelationship customerSecondQuestionRelationship = customerPatternNode.createRelationshipTo(secondQuestionNode, MyRelationship.COMPLETED);
        customerSecondQuestionRelationship.addPropertyConstraint("SEQUENCE", CommonValueMatchers.exact("2"));

        PatternNode answerOfSecondQuestionNode = new PatternNode();
        secondQuestionNode.createRelationshipTo(answerOfSecondQuestionNode, MyRelationship.ANSWERS);

        customerPatternNode.createRelationshipTo(answerOfSecondQuestionNode, MyRelationship.ANSWERED);

        PatternNode cannotEatDishes = new PatternNode();
        answerOfSecondQuestionNode.createRelationshipTo(cannotEatDishes, MyRelationship.EXCLUDES);

        PatternMatcher matcher1 = PatternMatcher.getMatcher();
        final Iterable<PatternMatch> matches1 = matcher1.match(customerPatternNode, customerNode);

        List<Menu> excludedDishes = new ArrayList<Menu>();

        for (PatternMatch pm : matches1) {
            Menu menu = new Menu();
            menu.setName(pm.getNodeFor(cannotEatDishes).getProperty("name").toString());
            excludedDishes.add(menu);
        }

        return excludedDishes;
    }

    private List<Menu> getDishesOfFirstQuestion(Node customerNode) {
        PatternNode customerPatternNode = new PatternNode();

        PatternNode firstQuestionNode = new PatternNode();
        PatternRelationship customerFirstQuestionRelationship = customerPatternNode.createRelationshipTo(firstQuestionNode, MyRelationship.COMPLETED);
        customerFirstQuestionRelationship.addPropertyConstraint("SEQUENCE", CommonValueMatchers.exact("1"));

        PatternNode answerOfFirstQuestionNode = new PatternNode();
        firstQuestionNode.createRelationshipTo(answerOfFirstQuestionNode, MyRelationship.ANSWERS);

        PatternNode canEatDishes = new PatternNode();

        answerOfFirstQuestionNode.createRelationshipTo(canEatDishes, MyRelationship.ANSWERS);

        customerPatternNode.createRelationshipTo(answerOfFirstQuestionNode, MyRelationship.ANSWERED);

        PatternMatcher matcher = PatternMatcher.getMatcher();
        final Iterable<PatternMatch> matches = matcher.match(customerPatternNode, customerNode);

        List<Menu> hotOrColdDishes = new ArrayList<Menu>();

        for (PatternMatch pm : matches) {
            Menu menu = new Menu();
            menu.setName(pm.getNodeFor(canEatDishes).getProperty("name").toString());
            hotOrColdDishes.add(menu);
        }

        return hotOrColdDishes;
    }

    public Node getCustomer(final String name) {
        Node customers = db.getCustomerNode();
        Traverser traverser = customers.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.currentNode().getProperty("name").equals(name)) {
                            return true;
                        }
                        return false;
                    }
                },
                MyRelationship.CUSTOMER, Direction.OUTGOING);
        return traverser.getAllNodes().iterator().next();

    }
}
