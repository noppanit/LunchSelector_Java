package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.model.Question;
import com.thoughtworks.relationship.MyRelationship;
import com.thoughtworks.util.ListHelper;
import org.neo4j.graphdb.*;

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
        List<Question> allQuestions = (List<Question>) ListHelper.convertNodesToNodeObjects(nodes);

        return allQuestions;
    }

    public List<Question> getNextQuestions(Node customerNode) {
        Node questionNode = db.getQuestionsNode();

        Traverser traverse = questionNode.traverse(Traverser.Order.BREADTH_FIRST,
                StopEvaluator.END_OF_GRAPH,
                new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition traversalPosition) {
                        if( traversalPosition.lastRelationshipTraversed() != null &&
                                traversalPosition.currentNode().hasRelationship(MyRelationship.REQUIRES,Direction.INCOMING))
                            return false;
                        if( traversalPosition.isStartNode() )
                            return false;
                        return true;
                    }
                },
                MyRelationship.QUESTION,Direction.OUTGOING);


        Collection<Node> nodes = traverse.getAllNodes();
        List<Question> listOfQuestions = (List<Question>) ListHelper.convertNodesToNodeObjects(nodes);

        return listOfQuestions;
    }


}
