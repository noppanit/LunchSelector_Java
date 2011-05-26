package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Answer;
import com.thoughtworks.model.Question;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

import java.util.Collection;
import java.util.List;

public class QuestionRepository {
    private DatabaseHelper db = DatabaseHelper.getInstance();

    public List<Question> getQuestions() throws Exception {
        Node questionNode = db.getQuestionsNode();
        Traverser traverse = questionNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.QUESTION, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Question> allQuestions = ListHelper.setSpecialProperties(nodes, new Question());

        return allQuestions;
    }

    public List<Question> getCompletedQuestions(Node customerNode) throws Exception {
        Traverser traverse = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.COMPLETED, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();

        List<Question> completedQuestions = ListHelper.setSpecialProperties(nodes, new Question());

        return completedQuestions;
    }

    public Node getAnswerById(long answerId) {
        return getNodeById(answerId);
    }

    public Node getQuestionById(long questionId) {
        return getNodeById(questionId);
    }

    private Node getNodeById(long questionId) {
        return db.getNodeById(questionId);
    }

    public Node getQuestion(final String questionString) {
        Node questionNode = db.getQuestionsNode();

        Traverser traverse = questionNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.currentNode().getProperty(DatabaseHelper.NODE_NAME).equals(questionString)) {
                            return true;
                        }
                        return false;
                    }
                },
                MyRelationship.QUESTION, Direction.OUTGOING);

        return traverse.getAllNodes().iterator().next();
    }

    public List<Answer> getAnswers(String questionString) throws Exception {
        Node theQuestion = getQuestion(questionString);

        Traverser traverse = theQuestion.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                MyRelationship.ANSWERS, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Answer> listOfAnswers = ListHelper.setSpecialProperties(nodes, new Answer());

        return listOfAnswers;
    }


    public List<Question> getNextQuestions(Node customerNode) throws Exception {
        Node questionNode = db.getQuestionsNode();
        List<Question> listOfQuestions = null;

        if (!customerNode.hasRelationship(MyRelationship.ANSWERED, Direction.BOTH)) {

            listOfQuestions = getQuestionsWhereNoQuestionsAnswered(questionNode);
        } else {

            listOfQuestions = getNextQuestionWhenFirstQuestionAnswered(customerNode);
            listOfQuestions.addAll(getCompletedQuestions(customerNode));
            listOfQuestions = ListHelper.substracts(getQuestions(), listOfQuestions);
        }

        return listOfQuestions;
    }

    private List<Question> getNextQuestionWhenFirstQuestionAnswered(Node customerNode) throws Exception {
        List<Question> listOfQuestions;
        Traverser traverse = customerNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.lastRelationshipTraversed() != null &&
                                traversalPosition.lastRelationshipTraversed().isType(MyRelationship.EXCLUDES) &&
                                traversalPosition.currentNode().hasRelationship(MyRelationship.QUESTION, Direction.INCOMING))
                            return true;
                        return false;
                    }
                },
                MyRelationship.ANSWERED, Direction.OUTGOING,
                MyRelationship.EXCLUDES, Direction.OUTGOING);
        Collection<Node> nodes = traverse.getAllNodes();
        listOfQuestions = ListHelper.setSpecialProperties(nodes, new Question());
        return listOfQuestions;
    }

    private List<Question> getQuestionsWhereNoQuestionsAnswered(Node questionNode) throws Exception {

        Traverser traverse = questionNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if (traversalPosition.lastRelationshipTraversed() != null &&
                                traversalPosition.currentNode().hasRelationship(MyRelationship.REQUIRES, Direction.INCOMING))
                            return false;
                        if (traversalPosition.isStartNode())
                            return false;
                        return true;
                    }
                },
                MyRelationship.QUESTION, Direction.OUTGOING);

        Collection<Node> nodes = traverse.getAllNodes();
        List<Question> listOfQuestions = ListHelper.setSpecialProperties(nodes, new Question());
        return listOfQuestions;
    }
}
