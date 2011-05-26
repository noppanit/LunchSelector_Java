package com.thoughtworks.controller;

import com.thoughtworks.model.Answer;
import com.thoughtworks.model.Customer;
import com.thoughtworks.model.Menu;
import com.thoughtworks.model.Question;
import com.thoughtworks.repository.CustomerRepository;
import com.thoughtworks.repository.QuestionRepository;
import org.neo4j.graphdb.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/customers/{customername}/questions", method = RequestMethod.GET)
    public String getNextQuestion(@PathVariable String customername, Model model)
    {
        QuestionRepository questionRepository = new QuestionRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        Node customerNode = customerRepository.getCustomer(customername);
        List<Question> listOfNextQuestions = questionRepository.getNextQuestions(customerNode);
        model.addAttribute("nextQuestions", listOfNextQuestions);
        model.addAttribute("customername",customername);

        return "customerQuestion";
    }

    @RequestMapping(value = "/customers/{customername}/questions/{questionId}", method = RequestMethod.GET)
    public String getAnswers(@PathVariable String customername, @PathVariable String questionId, Model model)
    {
        QuestionRepository questionRepository = new QuestionRepository();
        Node theQuestion = questionRepository.getQuestionById(Long.parseLong(questionId));
        String questionText = theQuestion.getProperty("name").toString();

        List<Answer> listOfAnswers = questionRepository.getAnswers(questionText);
        model.addAttribute("answers",listOfAnswers);
        model.addAttribute("customername",customername);
        model.addAttribute("questionText",questionText);
        model.addAttribute("questionId",questionId);

        return "answers";
    }

    @RequestMapping(value = "/customers/{customername}/questions/{questionId}", method = RequestMethod.POST)
    public String answerTheQuestion(@PathVariable String customername, @PathVariable String questionId, Model model)
    {

        return "customerMenu";
    }

    @RequestMapping(value = "/customers/menu/{customername}", method = RequestMethod.GET)
    public String getPersonalisedMenu(@PathVariable String customername, Model model)
    {
        CustomerRepository customerRepository = new CustomerRepository();
        Node customer = customerRepository.getCustomer(customername);
        List<Menu> customerPersonalisedMenu = customerRepository.getPersonalisedMenu(customer);
        model.addAttribute("personalisedMenus", customerPersonalisedMenu);
        model.addAttribute("customername",customername);

        return "customerMenu";
    }
}
