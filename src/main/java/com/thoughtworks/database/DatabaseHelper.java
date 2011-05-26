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

    private static DatabaseHelper db = getInstance();
    private GraphDatabaseService graphDb = new EmbeddedGraphDatabase(Constant.PROJECT_PATH + "/src/main/resource/db");

    private Index<Node> customersIndex = null;
    private Index<Node> menuIndex = null;
    private Index<Node> questionIndex = null;

    private String nodeName = "name";

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
        return customersIndex.get("name", "Customers").getSingle();
    }

    public Node getMenuNode() {
        return menuIndex.get("name", "Menu").getSingle();
    }

    public Node getQuestionsNode() {
        return questionIndex.get("name", "Questions").getSingle();
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
            Node customer = createNode(nodeName, "Customers");
            Node question = createNode(nodeName, "Questions");

            Node menu = createNode(nodeName, "Menu");
            menuIndex.add(menu, nodeName, "Menu");
            customersIndex.add(customer, nodeName, "Customers");
            questionIndex.add(question, nodeName, "Questions");

            relateToRoot(rootNode, customer);
            relateToRoot(rootNode, question);
            relateToRoot(rootNode, menu);

            Node tunaSalad = createNode(nodeName, "tuna salad");
            Node pastaSalad = createNode(nodeName, "pasta salad");
            Node nutSalad = createNode(nodeName, "nut salad");

            menu.createRelationshipTo(tunaSalad, MyRelationship.DISH);
            menu.createRelationshipTo(pastaSalad, MyRelationship.DISH);
            menu.createRelationshipTo(nutSalad, MyRelationship.DISH);

            Node mary = createCustomer(customer, "Mary");
            Node joy = createCustomer(customer, "Joy");
            Node john = createCustomer(customer, "John");

            Node hotOrCold = addQuestion("Do you want hot or cold food?", question);
            Node canYouEat = addQuestion("Can you eat all food types?", question);

            Node yes = addAnswerToQuestion(canYouEat, "yes");
            Node no = addAnswerToQuestion(canYouEat, "no");

            joy.createRelationshipTo(yes, MyRelationship.ANSWERED);

            Node allergies = addQuestion("What allergies do you have?", question);

            no.createRelationshipTo(allergies, MyRelationship.REQUIRES);

            Node hotFood = addAnswerToQuestion(hotOrCold, "hot");
            Node coldFood = addAnswerToQuestion(hotOrCold, "cold");

            Node potsu = createNode(nodeName, "grilled chicken potsu");
            Node rice = createNode(nodeName, "fried rice");

            menu.createRelationshipTo(potsu, MyRelationship.DISH);
            menu.createRelationshipTo(rice, MyRelationship.DISH);

            Node sandwiches = createNode(nodeName, "sandwiches");

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
        Node customerNode = createNode(nodeName, customerName);
        customer.createRelationshipTo(customerNode, MyRelationship.CUSTOMER);
        return customerNode;
    }

    private Node addAnswerToQuestion(Node question, String answerString) {
        Node answer = createNode(nodeName, answerString);
        question.createRelationshipTo(answer, MyRelationship.ANSWERS);
        return answer;
    }

    private Node addQuestion(String questionString, Node questionNode) {
        Node question = createNode(nodeName, questionString);
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
