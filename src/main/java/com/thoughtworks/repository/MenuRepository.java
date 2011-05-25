package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

import java.util.Collection;
import java.util.List;

public class MenuRepository {

    private DatabaseHelper db = DatabaseHelper.getInstance();

    public List<Menu> getDishes() {
        Node menuNode = db.getMenuNode();
        Traverser traverse = menuNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.DISH, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Menu> allDishes = (List<Menu>) ListHelper.convertNodesToNodeObjects(nodes);

        return allDishes;
    }
}
