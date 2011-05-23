package com.thoughtworks.repository;

import com.thoughtworks.model.Customer;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlySpecies;
import static org.junit.Assert.assertThat;

public class CustomerTest extends BaseTest {

    @Test
    public void shouldReturnAllCustomers() {
        CustomerRepository customerRepository = new CustomerRepository();
        List<Customer> customers = customerRepository.getCustomers();
        Collection<String> nodeNames = getNodeNames(customers);
        assertThat(nodeNames, containsOnlySpecies("Mary"));
    }


}
