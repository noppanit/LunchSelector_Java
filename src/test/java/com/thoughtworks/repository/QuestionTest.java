package com.thoughtworks.repository;

import com.thoughtworks.model.Question;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    @Test
    public void shouldReturnAllQuestions()
    {
        QuestionRepository questionRepository = new QuestionRepository();
        List<Question> listOfQuestions = questionRepository.getQuestions();

        assertThat(listOfQuestions.size(), is(2));

    }
}
