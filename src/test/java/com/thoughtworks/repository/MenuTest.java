package com.thoughtworks.repository;

import com.thoughtworks.model.Menu;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlyNodeNames;
import static org.junit.Assert.assertThat;

public class MenuTest extends BaseTest {

    @Test
    public void shouldReturnAllDishes() throws Exception {
        MenuRepository menu = new MenuRepository();
        List<Menu> nodes = menu.getDishes();
        Collection<String> nodeNames = getNodeNames(nodes);
        assertThat(nodeNames, containsOnlyNodeNames("tuna salad", "pasta salad", "nut salad", "sandwiches","grilled chicken potsu", "fried rice"));
    }


}
