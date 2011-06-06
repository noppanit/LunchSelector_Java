package com.thoughtworks.database;

import com.thoughtworks.constant.Constant;
import com.thoughtworks.relationship.MyRelationship;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;

public class DatabaseHelper {
    public static final String NODE_NAME = "Name";
    public static final String NODE_QUESTION_TYPE = "QuestionType";
    public static final String NODE_PRICE_CHILD = "Child";
    public static final String NODE_PRICE_DEFAULT = "Regular";
    public static final String NODE_PRICE_PENSIONER = "Pensioner";

    public static final String NODE_DOB = "Dob";

    public static final String NODE_THRESHOLD = "Threshold";

    public static final String NODE_FROM = "From";
    public static final String NODE_TO = "To";


    private static DatabaseHelper db = getInstance();
    private GraphDatabaseService graphDb = new EmbeddedGraphDatabase(Constant.PROJECT_PATH + "/src/main/resource/db");

    private Index<Node> customersIndex = null;
    private Index<Node> menuIndex = null;
    private Index<Node> questionIndex = null;
    private Index<Node> ruleIndex = null;

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

    public Node getRuleNode() {
        return ruleIndex.get(NODE_NAME, "Rules").getSingle();
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
            ruleIndex = graphDb.index().forNodes("rules");

            Node rootNode = getRoot();
            Node customer = createNode(NODE_NAME, "Customers");
            Node question = createNode(NODE_NAME, "Questions");

            Node menu = createNode(NODE_NAME, "Menu");
            Node rules = createNode(NODE_NAME, "Rules");

            Node priceDependsAgeRule = createNode(NODE_NAME, "Price depends on age");
            rules.createRelationshipTo(priceDependsAgeRule, MyRelationship.RULE);

            Node child = createNode(NODE_NAME, "Child");
            Relationship childRel = priceDependsAgeRule.createRelationshipTo(child, MyRelationship.LESS_THAN);
            childRel.setProperty(NODE_THRESHOLD, 12);

            Node pensioner = createNode(NODE_NAME, "Pensioner");
            Relationship pensionerRel = priceDependsAgeRule.createRelationshipTo(pensioner, MyRelationship.GREATER_THAN);
            pensionerRel.setProperty(NODE_THRESHOLD, 65);

            Node adult = createNode(NODE_NAME, "Adult");
            priceDependsAgeRule.createRelationshipTo(adult, MyRelationship.UNKNOWN_AGE);
            Relationship adultRel = priceDependsAgeRule.createRelationshipTo(adult, MyRelationship.BETWEEN);
            adultRel.setProperty(NODE_FROM, 12);
            adultRel.setProperty(NODE_TO, 65);

            menuIndex.add(menu, NODE_NAME, "Menu");
            customersIndex.add(customer, NODE_NAME, "Customers");
            questionIndex.add(question, NODE_NAME, "Questions");
            ruleIndex.add(rules, NODE_NAME, "Rules");

            relateToRoot(rootNode, customer);
            relateToRoot(rootNode, question);
            relateToRoot(rootNode, menu);
            relateToRoot(rootNode, rules);

            Node tunaSalad = createNode(NODE_NAME, "tuna salad");
            setPricesForDish(tunaSalad, "4", "10", "6");
            Node pastaSalad = createNode(NODE_NAME, "pasta salad");
            setPricesForDish(pastaSalad, "5", "11", "7");
            Node nutSalad = createNode(NODE_NAME, "nut salad");
            setPricesForDish(nutSalad, "6", "12", "8");

            menu.createRelationshipTo(tunaSalad, MyRelationship.DISH);
            menu.createRelationshipTo(pastaSalad, MyRelationship.DISH);
            menu.createRelationshipTo(nutSalad, MyRelationship.DISH);

            Node mary = createCustomer("Mary");
            mary.setProperty(NODE_DOB, "01/01/1990");
            Node joy = createCustomer("Joy");
            Node john = createCustomer("John");

            Node hotOrCold = createQuestion("Do you want hot or cold food?");
            hotOrCold.setProperty(NODE_QUESTION_TYPE, "single");
            Node canYouEat = createQuestion("Can you eat all food types?");
            canYouEat.setProperty(NODE_QUESTION_TYPE, "single");

            Node vegetarian = createQuestion("Are you a vegetarian?");
            vegetarian.setProperty(NODE_QUESTION_TYPE, "single");

            Node yes = addAnswerToQuestion(canYouEat, "yes");
            Node no = addAnswerToQuestion(canYouEat, "no");

            Node veganYes = addAnswerToQuestion(vegetarian, "yes");
            Node veganNo = addAnswerToQuestion(vegetarian, "no");

            joy.createRelationshipTo(yes, MyRelationship.ANSWERED);

            Node allergies = createQuestion("What allergies do you have?");
            allergies.setProperty(NODE_QUESTION_TYPE, "multiple");

            yes.createRelationshipTo(allergies, MyRelationship.EXCLUDES);
            no.createRelationshipTo(allergies, MyRelationship.REQUIRES);

            Node hotFood = addAnswerToQuestion(hotOrCold, "hot");
            Node coldFood = addAnswerToQuestion(hotOrCold, "cold");

            Node potsu = createNode(NODE_NAME, "grilled chicken potsu");
            setPricesForDish(potsu, "4", "10", "6");
            Node rice = createNode(NODE_NAME, "fried rice");
            setPricesForDish(rice, "5", "11", "7");

            menu.createRelationshipTo(potsu, MyRelationship.DISH);
            menu.createRelationshipTo(rice, MyRelationship.DISH);

            Node sandwiches = createNode(NODE_NAME, "sandwiches");
            setPricesForDish(sandwiches, "5", "11", "7");

            veganYes.createRelationshipTo(potsu, MyRelationship.EXCLUDES);
            veganYes.createRelationshipTo(rice, MyRelationship.EXCLUDES);
            veganYes.createRelationshipTo(tunaSalad, MyRelationship.EXCLUDES);
            veganYes.createRelationshipTo(sandwiches, MyRelationship.EXCLUDES);

            veganNo.createRelationshipTo(pastaSalad, MyRelationship.EXCLUDES);
            veganNo.createRelationshipTo(nutSalad, MyRelationship.EXCLUDES);


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
            mary.createRelationshipTo(no, MyRelationship.ANSWERED);
            mary.createRelationshipTo(canYouEat, MyRelationship.COMPLETED);
            mary.createRelationshipTo(coldFood, MyRelationship.ANSWERED);

            joy.createRelationshipTo(canYouEat, MyRelationship.COMPLETED);


            tx.success();
        } catch (Exception ex) {
            tx.failure();
        } finally {
            tx.finish();
        }

    }

    private void setPricesForDish(Node dish, String child, String regular, String pensioner) {
        dish.setProperty(NODE_PRICE_CHILD, child);
        dish.setProperty(NODE_PRICE_DEFAULT, regular);
        dish.setProperty(NODE_PRICE_PENSIONER, pensioner);
    }

    public Node createCustomer(String customerName) {
        Node customer = getCustomerNode();
        Node customerNode = createNode(DatabaseHelper.NODE_NAME, customerName);
        customer.createRelationshipTo(customerNode, MyRelationship.CUSTOMER);
        return customerNode;
    }

    public Node createNode(String nodeName, String nodeValue) {
        Node node = graphDb.createNode();
        node.setProperty(nodeName, nodeValue);
        return node;
    }

    private Node addAnswerToQuestion(Node question, String answerString) {
        Node answer = createNode(NODE_NAME, answerString);
        question.createRelationshipTo(answer, MyRelationship.ANSWERS);
        return answer;
    }

    private Node createQuestion(String questionString) {
        Node questionNode = getQuestionsNode();
        Node question = createNode(NODE_NAME, questionString);
        questionNode.createRelationshipTo(question, MyRelationship.QUESTION);
        return question;
    }

    private void relateToRoot(Node rootNode, Node node) {
        rootNode.createRelationshipTo(node, MyRelationship.CONTAINS);
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
