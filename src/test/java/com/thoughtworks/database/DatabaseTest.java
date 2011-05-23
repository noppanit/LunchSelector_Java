package com.thoughtworks.database;

import com.thoughtworks.constant.Constant;
import com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes;
import com.thoughtworks.matcher.ContainsOnlySpecificNodes;
import com.thoughtworks.relationship.MyRelationship;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlySpecies;
import static org.junit.Assert.assertThat;

/**
 * Unit test for simple Server.
 */
public class DatabaseTest {

    private DatabaseHelper db = null;

    @Before
    public void setup() {
        File dbDir = new File(Constant.PROJECT_PATH + "/src/main/resource/db");
        deleteDir(dbDir);
        dbDir.mkdir();

        db = new DatabaseHelper();
        db.initialise();
    }

    @Test
    public void shouldReturnAllDishes() {
        Node menu = db.getMenu();
        Traverser traverse = menu.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.DISH, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        Collection<String> nodeNames = getNodeNames(nodes);

        assertThat(nodeNames, containsOnlySpecies("tuna salad", "pasta salad", "nut salad"));
    }

    private Collection<String> getNodeNames(Collection<Node> nodes) {
        Collection<String> nodeNames = new ArrayList<String>();
        for( Node node: nodes )
        {
            nodeNames.add(node.getProperty("name").toString());
        }
        return nodeNames;
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
