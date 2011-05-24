package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.Traversal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

        List<Menu> cannotEatDishes = new ArrayList<Menu>();

        Traverser traverser = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    public boolean isReturnableNode(TraversalPosition currentPosition) {
                        Relationship rel = currentPosition.lastRelationshipTraversed();
                        if (rel != null && rel.isType(MyRelationship.EXCLUDES)) {
                            return true;
                        }
                        return false;
                    }
                },
                MyRelationship.ANSWERED, Direction.OUTGOING,
                MyRelationship.EXCLUDES, Direction.OUTGOING);

        for (Node dish : traverser) {
            Menu menu = new Menu();
            menu.setName(dish.getProperty("name").toString());

            cannotEatDishes.add(menu);
        }

        List<Menu> allDishes = new MenuRepository().getDishes();

        return (List<Menu>) new ListHelper().substracts(allDishes, cannotEatDishes);
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
