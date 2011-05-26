package com.thoughtworks.repository;

import com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes;
import com.thoughtworks.model.Question;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import java.util.Collection;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlyNodeNames;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest extends BaseTest {

    @Test
    public void shouldReturnAllQuestions() {
        QuestionRepository questionRepository = new QuestionRepository();
        List<Question> listOfQuestions = questionRepository.getQuestions();

        assertThat(listOfQuestions.size(), is(3));

    }

    @Test
    public void shouldReturnCompletedQuestions() {
        QuestionRepository questionRepository = new QuestionRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        Node customerNode = customerRepository.getCustomer("Joy");

        List<Question> listOfQuestions = questionRepository.getCompletedQuestions(customerNode);
        Collection<String> nodeNames = getNodeNames(listOfQuestions);
        assertThat(listOfQuestions.size(), is(1));
        assertThat(nodeNames, containsOnlyNodeNames("Can you eat all food types?"));
    }

    @Test
    public void shouldReturnRelevantQuestionsWhenNoQuestionsAnswered() {
        QuestionRepository questionRepository = new QuestionRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        Node customerNode = customerRepository.getCustomer("John");

        List<Question> listOfQuestions = questionRepository.getNextQuestions(customerNode);
        Collection<String> nodeNames = getNodeNames(listOfQuestions);
        assertThat(listOfQuestions.size(), is(2));
        assertThat(nodeNames, containsOnlyNodeNames("Do you want hot or cold food?", "Can you eat all food types?"));
    }

    @Test
    public void shouldReturnRelevantQuestionsWhenFirstQuestionAnswered() {
        QuestionRepository questionRepository = new QuestionRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        Node customerNode = customerRepository.getCustomer("Joy");

        List<Question> listOfQuestions = questionRepository.getNextQuestions(customerNode);
        Collection<String> nodeNames = getNodeNames(listOfQuestions);
        assertThat(listOfQuestions.size(), is(1));
        assertThat(nodeNames, containsOnlyNodeNames("Do you want hot or cold food?"));
    }
}
