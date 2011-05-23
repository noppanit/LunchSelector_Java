package com.thoughtworks.repository;

import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import java.util.Collection;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlySpecies;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CustomerTest extends BaseTest {

    @Test
    public void shouldReturnAllCustomers() {
        CustomerRepository customerRepository = new CustomerRepository();
        List<Customer> customers = customerRepository.getCustomers();
        Collection<String> nodeNames = getNodeNames(customers);
        assertThat(nodeNames, containsOnlySpecies("Mary"));
    }

    @Test
    public void shouldReturnPersonalisedMenu() {
        CustomerRepository customerRepository = new CustomerRepository();
        List<Menu> dishes = customerRepository.getPersonalisedMenu();

    }

    @Test
    public void shouldReturnACustomerByName()
    {
        CustomerRepository customerRepository = new CustomerRepository();
        Node mary = customerRepository.getCustomer("Mary");

        assertThat(mary.getProperty("name").toString(), is("Mary"));

    }


}
