package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Answer;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

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

        Collection<Node> allNodes = traverse.getAllNodes();
        List<Customer> allCustomers = (List<Customer>) ListHelper.convertNodesToNodeObjects(allNodes);

        return allCustomers;
    }

    public List<Menu> getPersonalisedMenu(Node customerNode) {
        MenuRepository menuRepository = new MenuRepository();
        List<Menu> listOfHotOrColdDishes = menuRepository.getDishes();
        List<Menu> listOfExcludedDishes = getExcludedDishes(customerNode);

        return ListHelper.substracts(listOfHotOrColdDishes, listOfExcludedDishes);
    }

    private List<Menu> getExcludedDishes(Node customerNode) {
        Traverser traverser = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.lastRelationshipTraversed() != null &&
                                traversalPosition.lastRelationshipTraversed().isType(MyRelationship.EXCLUDES))
                            return true;
                        return false;
                    }
                },
                MyRelationship.ANSWERED, Direction.OUTGOING,
                MyRelationship.EXCLUDES, Direction.OUTGOING);

        Collection<Node> allNodes = traverser.getAllNodes();
        List<Menu> excludedDishes = (List<Menu>) ListHelper.convertNodesToNodeObjects(allNodes);

        return excludedDishes;
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
