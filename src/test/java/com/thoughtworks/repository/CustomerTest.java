package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import java.util.Collection;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlyNodeNames;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CustomerTest extends BaseTest {

    @Test
    public void shouldReturnAllCustomers() throws Exception {
        CustomerRepository customerRepository = new CustomerRepository();
        List<Customer> customers = customerRepository.getCustomers();
        Collection<String> nodeNames = getNodeNames(customers);
        assertThat(nodeNames, containsOnlyNodeNames("Mary", "Joy", "John"));
    }

    @Test
    public void shouldReturnPersonalisedMenu() throws Exception {
        CustomerRepository customerRepository = new CustomerRepository();
        Node mary = customerRepository.getCustomer("Mary");
        List<Menu> dishes = customerRepository.getPersonalisedMenu(mary);
        Collection<String> nodeNames = getNodeNames(dishes);

        assertThat(nodeNames, containsOnlyNodeNames("pasta salad","sandwiches"));
    }

    @Test
    public void shouldReturnPersonalisedMenuFromCustomerNotAnswerQuestion() throws Exception
    {
        CustomerRepository customerRepository = new CustomerRepository();
        Node mary = customerRepository.getCustomer("Joy");
        List<Menu> dishes = customerRepository.getPersonalisedMenu(mary);
        Collection<String> nodeNames = getNodeNames(dishes);

        assertThat(nodeNames, containsOnlyNodeNames("tuna salad","nut salad","grilled chicken potsu","fried rice","pasta salad","sandwiches"));
    }

    @Test
    public void shouldReturnACustomerByName() {
        CustomerRepository customerRepository = new CustomerRepository();
        Node mary = customerRepository.getCustomer("Mary");

        assertThat(mary.getProperty(DatabaseHelper.NODE_NAME).toString(), is("Mary"));

    }


}
