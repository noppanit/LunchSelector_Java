package com.thoughtworks.database;

import com.thoughtworks.constant.Constant;
import com.thoughtworks.model.Menu;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.repository.MenuRepository;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlySpecies;
import static org.junit.Assert.assertThat;

public class DatabaseTest {

    @Test
    public void shouldReturnAllDishes() {
        MenuRepository menu = new MenuRepository();

        List<Menu> nodes = menu.getDishes();
        Collection<String> nodeNames = getNodeNames(nodes);

        assertThat(nodeNames, containsOnlySpecies("tuna salad", "pasta salad", "nut salad"));
    }

    private Collection<String> getNodeNames(List<Menu> dishes) {
        Collection<String> nodeNames = new ArrayList<String>();
        for( Menu node: dishes )
        {
            nodeNames.add(node.getName());
        }
        return nodeNames;
    }


}
