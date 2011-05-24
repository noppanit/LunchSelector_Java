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

            Node mary = createNode(nodeName, "Mary");


            Node allergies = createNode(nodeName, "What allergies do you have?");

            Node hotOrCold = createNode(nodeName, "Do you want hot or cold food?");

            question.createRelationshipTo(allergies, MyRelationship.QUESTION);
            question.createRelationshipTo(hotOrCold, MyRelationship.QUESTION);

            Node hotFood = createNode(nodeName, "hot");
            Node coldFood = createNode(nodeName, "cold");

            hotOrCold.createRelationshipTo(hotFood, MyRelationship.ANSWERS);
            hotOrCold.createRelationshipTo(coldFood, MyRelationship.ANSWERS);

            Node potsu = createNode(nodeName, "grilled chicken potsu");
            Node rice = createNode(nodeName, "fried rice");

            hotFood.createRelationshipTo(potsu, MyRelationship.ANSWERS);
            hotFood.createRelationshipTo(rice, MyRelationship.ANSWERS);

            menu.createRelationshipTo(potsu, MyRelationship.DISH);
            menu.createRelationshipTo(rice, MyRelationship.DISH);

            Node sandwiches = createNode(nodeName, "sandwiches");

            coldFood.createRelationshipTo(sandwiches, MyRelationship.ANSWERS);
            coldFood.createRelationshipTo(tunaSalad, MyRelationship.ANSWERS);
            coldFood.createRelationshipTo(nutSalad, MyRelationship.ANSWERS);
            coldFood.createRelationshipTo(pastaSalad, MyRelationship.ANSWERS);

            menu.createRelationshipTo(sandwiches, MyRelationship.DISH);

            Node fish = createNode(nodeName, "fish");
            Node nut = createNode(nodeName, "nut");
            Node wheat = createNode(nodeName, "wheat");

            allergies.createRelationshipTo(fish, MyRelationship.ANSWERS);
            allergies.createRelationshipTo(nut, MyRelationship.ANSWERS);
            allergies.createRelationshipTo(wheat, MyRelationship.ANSWERS);

            customer.createRelationshipTo(mary, MyRelationship.CUSTOMER);

            nut.createRelationshipTo(nutSalad, MyRelationship.EXCLUDES);
            fish.createRelationshipTo(tunaSalad, MyRelationship.EXCLUDES);

            mary.createRelationshipTo(nut, MyRelationship.ANSWERED);
            mary.createRelationshipTo(fish, MyRelationship.ANSWERED);
            mary.createRelationshipTo(allergies, MyRelationship.COMPLETED);


            tx.success();
        } catch (Exception ex) {
            tx.failure();
        } finally {
            tx.finish();
        }

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

    public Node getCustomerNode() {
        return customersIndex.get("name", "Customers").getSingle();
    }

    public Node getMenuNode() {
        return menuIndex.get("name", "Menu").getSingle();
    }

    public Node getQuestionsNode() {
        return questionIndex.get("name", "Questions").getSingle();
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
