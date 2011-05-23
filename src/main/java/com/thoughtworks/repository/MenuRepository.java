package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MenuRepository {

    DatabaseHelper db = DatabaseHelper.getInstance();

    public List<Menu> getDishes() {
        Node menuNode = db.getMenuNode();
        Traverser traverse = menuNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.DISH, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Menu> allDishes = new ArrayList<Menu>();
        for (Node node : nodes) {

            Menu menu = new Menu();
            menu.setName(node.getProperty("name").toString());

            allDishes.add(menu);
        }

        return allDishes;
    }
}
