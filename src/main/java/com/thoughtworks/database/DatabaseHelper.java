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

    private static DatabaseHelper db = null;
    private GraphDatabaseService graphDb = new EmbeddedGraphDatabase(Constant.PROJECT_PATH + "/src/main/resource/db");

    private Index<Node> customersIndex = graphDb.index().forNodes("customers");
    private Index<Node> menuIndex = graphDb.index().forNodes("menu");

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
            Node rootNode = getRoot();
            Node customer = createNode(nodeName, "Customers");
            Node question = createNode(nodeName, "Questions");

            Node menu = createNode(nodeName, "Menu");
            menuIndex.add(menu, nodeName, "Menu");

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
            customersIndex.add(mary, nodeName, "Mary");

            Node allergies = createNode(nodeName, "What allergies do you have?");

            question.createRelationshipTo(allergies, MyRelationship.QUESTION);

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

    public Node getMary() {
        return customersIndex.get("name", "Mary").getSingle();
    }

    public Node getMenu() {
        return menuIndex.get("name", "Menu").getSingle();
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
