package com.thoughtworks.controller;

import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.repository.CustomerRepository;
import org.neo4j.graphdb.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CustomerController {

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String customers(Model model)
    {
        CustomerRepository customerRepository = new CustomerRepository();
        List<Customer> customers = customerRepository.getCustomers();

        model.addAttribute("customers",customers);
        return "customers";
    }

    @RequestMapping(value = "/customers/{customername}/questions", method = RequestMethod.GET)
    public String getNextQuestion()
    {
        return "customerQuestion";
    }

    @RequestMapping(value = "/customers/menu/{customername}", method = RequestMethod.GET)
    public String getPersonalisedMenu(@PathVariable String customername, Model model)
    {
        CustomerRepository customerRepository = new CustomerRepository();
        Node customer = customerRepository.getCustomer(customername);
        List<Menu> customerPersonalisedMenu = customerRepository.getPersonalisedMenu(customer);
        model.addAttribute("personalisedMenus", customerPersonalisedMenu);

        return "customerMenu";
    }
}
