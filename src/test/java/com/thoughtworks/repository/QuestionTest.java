package com.thoughtworks.repository;

import com.thoughtworks.model.Question;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    @Test
    public void shouldReturnAllQuestions() {
        QuestionRepository questionRepository = new QuestionRepository();
        List<Question> listOfQuestions = questionRepository.getQuestions();

        assertThat(listOfQuestions.size(), is(3));

    }

    @Test
    public void shouldReturnRelevantQuestionsWhenNoQuestionsAnswered() {
        QuestionRepository questionRepository = new QuestionRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        Node customerNode = customerRepository.getCustomer("John");

        List<Question> listOfQuestions = questionRepository.getNextQuestions(customerNode);
        assertThat(listOfQuestions.size(), is(2));
    }

    @Test
    public void shouldReturnRelevantQuestions() {
        QuestionRepository questionRepository = new QuestionRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        Node customerNode = customerRepository.getCustomer("Joy");

        List<Question> listOfQuestions = questionRepository.getNextQuestions(customerNode);
        assertThat(listOfQuestions.size(), is(1));
    }
}
