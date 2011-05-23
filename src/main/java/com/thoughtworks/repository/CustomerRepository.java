package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Customer;
import com.thoughtworks.relationship.MyRelationship;
import org.neo4j.graphdb.*;

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
}
