package com.thoughtworks.repository;

import com.thoughtworks.model.Answer;
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

    @Test
    public void shouldReturnQuestionNodeByName()
    {
        QuestionRepository questionRepository = new QuestionRepository();
        Node theQuestion = questionRepository.getQuestion("Can you eat all food types?");

        assertThat(theQuestion.getProperty("name").toString(), is("Can you eat all food types?"));
    }

    @Test
    public void shouldReturnAnswersFromQuestion()
    {
        QuestionRepository questionRepository = new QuestionRepository();
        List<Answer> listOfAnswers = questionRepository.getAnswers("Can you eat all food types?");

        Collection<String> nodeNames = getNodeNames(listOfAnswers);
        assertThat(listOfAnswers.size(), is(2));
        assertThat(nodeNames, containsOnlyNodeNames("yes","no"));
    }
}
