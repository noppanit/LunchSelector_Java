package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Answer;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CustomerRepository {

    private DatabaseHelper db = DatabaseHelper.getInstance();

    public List<Customer> getCustomers() throws Exception {
        Node customerNode = db.getCustomerNode();
        Traverser traverse = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.CUSTOMER, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Customer> allCustomers = ListHelper.setSpecialProperties(nodes, new Customer());

        return allCustomers;
    }

    public List<Menu> getPersonalisedMenu(Node customerNode) throws Exception {
        MenuRepository menuRepository = new MenuRepository();
        List<Menu> listOfHotOrColdDishes = menuRepository.getDishes();
        List<Menu> listOfExcludedDishes = getExcludedDishes(customerNode);

        return ListHelper.substracts(listOfHotOrColdDishes, listOfExcludedDishes);
    }

    private List<Menu> getExcludedDishes(Node customerNode) throws Exception {
        Traverser traverser = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.lastRelationshipTraversed() != null &&
                                traversalPosition.lastRelationshipTraversed().isType(MyRelationship.EXCLUDES) &&
                                traversalPosition.currentNode().hasRelationship(MyRelationship.DISH, Direction.INCOMING))
                            return true;
                        return false;
                    }
                },
                MyRelationship.ANSWERED, Direction.OUTGOING,
                MyRelationship.EXCLUDES, Direction.OUTGOING);

        Collection<Node> nodes = traverser.getAllNodes();
        List<Menu> excludedDishes = ListHelper.setSpecialProperties(nodes, new Menu());

        return excludedDishes;
    }

    public void createCustomer(String name)
    {
        GraphDatabaseService graphDb = db.getDatabaseService();
        Transaction tx = graphDb.beginTx();

        try {
            db.createCustomer(name);
            tx.success();
        } catch (Exception ex) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    public Node getCustomer(final String name) {
        Node customers = db.getCustomerNode();
        Traverser traverser = customers.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.currentNode().getProperty(DatabaseHelper.NODE_NAME).equals(name)) {
                            return true;
                        }
                        return false;
                    }
                },
                MyRelationship.CUSTOMER, Direction.OUTGOING);

        return traverser.getAllNodes().iterator().next();

    }

    public void answerQuestion(String customerName, long questionId, List<Answer> listOfAnswer) {
        Node customerNode = getCustomer(customerName);

        QuestionRepository questionRepository = new QuestionRepository();

        GraphDatabaseService graphDb = db.getDatabaseService();

        Transaction tx = graphDb.beginTx();

        try {
            Node questionNode = questionRepository.getQuestionById(questionId);
            customerNode.createRelationshipTo(questionNode, MyRelationship.COMPLETED);

            for (Answer answer : listOfAnswer) {
                Node answerNode = questionRepository.getAnswerById(answer.getId());
                customerNode.createRelationshipTo(answerNode, MyRelationship.ANSWERED);
            }
            tx.success();
        } catch (Exception ex) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }
}
