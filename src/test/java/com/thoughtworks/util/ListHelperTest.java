package com.thoughtworks.util;

import com.thoughtworks.model.Menu;
import com.thoughtworks.model.NodeObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.util.ListHelper.substracts;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ListHelperTest {

    @Test
    public void shouldReturnOnlyDifferentItemOfList()
    {
        Menu menu1 = new Menu();
        menu1.setName("menu1");

        Menu menu2 = new Menu();
        menu2.setName("menu2");

        Menu menu3 = new Menu();
        menu3.setName("menu3");

        List<NodeObject> nodeObjectList = new ArrayList<NodeObject>();
        nodeObjectList.add(menu1);
        nodeObjectList.add(menu2);

        Menu menu21 = new Menu();
        menu21.setName("menu2");

        Menu menu31 = new Menu();
        menu31.setName("menu3");

        List<NodeObject> nodeObjectList1 = new ArrayList<NodeObject>();
        nodeObjectList1.add(menu21);
        nodeObjectList1.add(menu31);

        List<NodeObject> substractedNode = substracts(nodeObjectList, nodeObjectList1);

        assertThat( substractedNode.size(), is(1));
        assertThat( substractedNode.get(0).getName(), is("menu1"));




    }
}
