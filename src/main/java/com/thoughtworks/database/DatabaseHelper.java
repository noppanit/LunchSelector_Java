package com.thoughtworks.database;

import com.thoughtworks.constant.Constant;
import com.thoughtworks.relationship.MyRelationship;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;

public class DatabaseHelper {
    public static final String NODE_NAME = "Name";
    public static final String NODE_QUESTION_TYPE = "QuestionType";

    private static DatabaseHelper db = getInstance();
    private GraphDatabaseService graphDb = new EmbeddedGraphDatabase(Constant.PROJECT_PATH + "/src/main/resource/db");

    private Index<Node> customersIndex = null;
    private Index<Node> menuIndex = null;
    private Index<Node> questionIndex = null;



    public static DatabaseHelper getInstance() {
        if (db == null) {
            File dbDir = new File(Constant.PROJECT_PATH + "/src/main/resource/db");
            deleteDir(dbDir);
            dbDir.mkdir();
            return new DatabaseHelper();
        }
        return db;

    }

    public Node getCustomerNode() {
        return customersIndex.get(NODE_NAME, "Customers").getSingle();
    }

    public Node getMenuNode() {
        return menuIndex.get(NODE_NAME, "Menu").getSingle();
    }

    public Node getQuestionsNode() {
        return questionIndex.get(NODE_NAME, "Questions").getSingle();
    }

    public Node getNodeById(long nodeId) {
        return graphDb.getNodeById(nodeId);
    }

    public GraphDatabaseService getDatabaseService() {
        return graphDb;
    }

    private DatabaseHelper() {
        Transaction tx = graphDb.beginTx();
        try {
            customersIndex = graphDb.index().forNodes("customers");
            menuIndex = graphDb.index().forNodes("menu");
            questionIndex = graphDb.index().forNodes("questions");

            Node rootNode = getRoot();
            Node customer = createNode(NODE_NAME, "Customers");
            Node question = createNode(NODE_NAME, "Questions");

            Node menu = createNode(NODE_NAME, "Menu");
            menuIndex.add(menu, NODE_NAME, "Menu");
            customersIndex.add(customer, NODE_NAME, "Customers");
            questionIndex.add(question, NODE_NAME, "Questions");

            relateToRoot(rootNode, customer);
            relateToRoot(rootNode, question);
            relateToRoot(rootNode, menu);

            Node tunaSalad = createNode(NODE_NAME, "tuna salad");
            Node pastaSalad = createNode(NODE_NAME, "pasta salad");
            Node nutSalad = createNode(NODE_NAME, "nut salad");

            menu.createRelationshipTo(tunaSalad, MyRelationship.DISH);
            menu.createRelationshipTo(pastaSalad, MyRelationship.DISH);
            menu.createRelationshipTo(nutSalad, MyRelationship.DISH);

            Node mary = createCustomer(customer, "Mary");
            Node joy = createCustomer(customer, "Joy");
            Node john = createCustomer(customer, "John");

            Node hotOrCold = addQuestion("Do you want hot or cold food?", question);
            hotOrCold.setProperty("QuestionType","single");
            Node canYouEat = addQuestion("Can you eat all food types?", question);
            canYouEat.setProperty("QuestionType","single");

            Node yes = addAnswerToQuestion(canYouEat, "yes");
            Node no = addAnswerToQuestion(canYouEat, "no");

            joy.createRelationshipTo(yes, MyRelationship.ANSWERED);

            Node allergies = addQuestion("What allergies do you have?", question);
            allergies.setProperty("QuestionType","multiple");

            no.createRelationshipTo(allergies, MyRelationship.REQUIRES);

            Node hotFood = addAnswerToQuestion(hotOrCold, "hot");
            Node coldFood = addAnswerToQuestion(hotOrCold, "cold");

            Node potsu = createNode(NODE_NAME, "grilled chicken potsu");
            Node rice = createNode(NODE_NAME, "fried rice");

            menu.createRelationshipTo(potsu, MyRelationship.DISH);
            menu.createRelationshipTo(rice, MyRelationship.DISH);

            Node sandwiches = createNode(NODE_NAME, "sandwiches");

            hotFood.createRelationshipTo(sandwiches, MyRelationship.EXCLUDES);
            hotFood.createRelationshipTo(tunaSalad, MyRelationship.EXCLUDES);
            hotFood.createRelationshipTo(nutSalad, MyRelationship.EXCLUDES);
            hotFood.createRelationshipTo(pastaSalad, MyRelationship.EXCLUDES);

            coldFood.createRelationshipTo(potsu, MyRelationship.EXCLUDES);
            coldFood.createRelationshipTo(rice, MyRelationship.EXCLUDES);

            menu.createRelationshipTo(sandwiches, MyRelationship.DISH);

            Node fish = addAnswerToQuestion(allergies, "fish");
            Node nut = addAnswerToQuestion(allergies, "nut");
            Node wheat = addAnswerToQuestion(allergies, "wheat");

            nut.createRelationshipTo(nutSalad, MyRelationship.EXCLUDES);
            fish.createRelationshipTo(tunaSalad, MyRelationship.EXCLUDES);

            mary.createRelationshipTo(nut, MyRelationship.ANSWERED);
            mary.createRelationshipTo(fish, MyRelationship.ANSWERED);
            mary.createRelationshipTo(allergies, MyRelationship.COMPLETED);
            mary.createRelationshipTo(hotOrCold, MyRelationship.COMPLETED);

            mary.createRelationshipTo(coldFood, MyRelationship.ANSWERED);

            joy.createRelationshipTo(canYouEat, MyRelationship.COMPLETED);

            yes.createRelationshipTo(allergies, MyRelationship.EXCLUDES);

            tx.success();
        } catch (Exception ex) {
            tx.failure();
        } finally {
            tx.finish();
        }

    }

    private Node createCustomer(Node customer, String customerName) {
        Node customerNode = createNode(NODE_NAME, customerName);
        customer.createRelationshipTo(customerNode, MyRelationship.CUSTOMER);
        return customerNode;
    }

    private Node addAnswerToQuestion(Node question, String answerString) {
        Node answer = createNode(NODE_NAME, answerString);
        question.createRelationshipTo(answer, MyRelationship.ANSWERS);
        return answer;
    }

    private Node addQuestion(String questionString, Node questionNode) {
        Node question = createNode(NODE_NAME, questionString);
        questionNode.createRelationshipTo(question, MyRelationship.QUESTION);
        return question;
    }

    private void relateToRoot(Node rootNode, Node node) {
        rootNode.createRelationshipTo(node, MyRelationship.CONTAINS);
    }

    private Node createNode(String nodeName, String nodeValue) {
        Node node = graphDb.createNode();
        node.setProperty(nodeName, nodeValue);
        return node;
    }

    private Node getRoot() {
        return graphDb.getReferenceNode();
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
