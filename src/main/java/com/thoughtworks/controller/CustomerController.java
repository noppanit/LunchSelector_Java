package com.thoughtworks.controller;

import com.thoughtworks.model.Customer;
import com.thoughtworks.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
