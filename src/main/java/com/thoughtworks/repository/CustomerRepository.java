package com.thoughtworks.repository;

import com.thoughtworks.constant.Constant;
import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Answer;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.model.Rule;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

import java.text.ParseException;
import java.util.*;

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

    public void createCustomer(String name, String dob) {
        GraphDatabaseService graphDb = db.getDatabaseService();
        Transaction tx = graphDb.beginTx();

        try {
            Node customer = db.createCustomer(name);
            customer.setProperty(DatabaseHelper.NODE_DOB, dob);
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

    public Calendar getCustomerDateOfBirth(Node customer) throws ParseException {
        String customerDOB = (String) customer.getProperty(DatabaseHelper.NODE_DOB);

        Date customerDate = Constant.SIMPLE_DATE_FORMAT.parse(customerDOB);
        Calendar customerDateOfBirth = Calendar.getInstance();
        customerDateOfBirth.setTime(customerDate);

        return customerDateOfBirth;
    }

    public int calculateAge(Calendar customerDateOfBirth) {
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        return nowYear - customerDateOfBirth.get(Calendar.YEAR);
    }

    public int getAge(Node customer) {
        Calendar customerDOB = null;
        try {
            customerDOB = getCustomerDateOfBirth(customer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calculateAge(customerDOB);
    }

    public HashMap<String, String> getEndNodeByRule(Node customer) throws Exception {
        RuleRepository ruleRepository = new RuleRepository();
        int value = 0;
        HashMap<String,String> mapOfRuleAndEndNode = new HashMap<String,String>();
        List<Rule> listOfRules = getCustomerRules();
        for (Rule rule : listOfRules) {
            String ruleUsesThis = rule.getUsing();
            value = getValueBasedOn(ruleUsesThis, customer);
            Node endNode = ruleRepository.evaluateRuleBasedOn(ruleUsesThis).withValue(value);
            String endNodeString = endNode.getProperty(DatabaseHelper.NODE_NAME).toString();
            mapOfRuleAndEndNode.put(ruleUsesThis, endNodeString);
        }
        return mapOfRuleAndEndNode;
    }

    private int getValueBasedOn(String using, Node customerNode) {
        int value = 0;
        if (using == "Age")
            value = getAge(customerNode);

        return value;
    }

    public List<Rule> getCustomerRules() throws Exception {
        Node customerNode = db.getCustomerNode();

        Traverser traverse = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.APPLIES, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Rule> allCustomerRules = ListHelper.setSpecialProperties(nodes, new Rule());
        return allCustomerRules;
    }

    public boolean hasRules() {
        try {
            return getCustomerRules().size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
