package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Question;
import com.thoughtworks.relationship.MyRelationship;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestionRepository {
    private DatabaseHelper db = DatabaseHelper.getInstance();

    public List<Question> getQuestions() {
        Node questionNode = db.getQuestionsNode();
        Traverser traverse = questionNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.QUESTION, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Question> allQuestions = new ArrayList<Question>();
        for (Node node : nodes) {

            Question question = new Question();
            question.setName(node.getProperty("name").toString());

            allQuestions.add(question);
        }

        return allQuestions;
    }
}
