package com.thoughtworks.repository;

import com.thoughtworks.database.DatabaseHelper;
import com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes;
import com.thoughtworks.model.Rule;
import com.thoughtworks.relationship.MyRelationship;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphmatching.*;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.jmx.Description;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static com.thoughtworks.matcher.ContainsOnlySpecificNameOfNodes.containsOnlyNodeNames;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.neo4j.helpers.collection.IteratorUtil.asIterable;

public class RuleRepositoryTest {

    private static final String USING_DOB = "Dob";

    private CypherParser parser;
    private ImpermanentGraphDatabase db;
    private ExecutionEngine engine;

    @Before
    public void setUp() throws Exception {
        parser = new CypherParser();
        db = new ImpermanentGraphDatabase();
        engine = new ExecutionEngine(db);
    }

    @Test
    public void shouldReturnAgeLessThanCategoryBasedOnRule() {
        int age = 10;

        RuleRepository ruleRepository = new RuleRepository();
        Node child = ruleRepository.evaluateRuleBasedOn(USING_DOB).withValue(age);
        assertThat(child.getProperty(DatabaseHelper.NODE_NAME).toString(), is("Child"));
    }

    @Test
    public void shouldReturnAgeGreaterThanCategoryBasedOnRule() {
        int age = 66;

        RuleRepository ruleRepository = new RuleRepository();
        Node pensioner = ruleRepository.evaluateRuleBasedOn(USING_DOB).withValue(age);
        assertThat(pensioner.getProperty(DatabaseHelper.NODE_NAME).toString(), is("Pensioner"));
    }

    @Test
    public void shouldReturnAgeBetweenCategoryBasedOnRule() {
        int age = 60;

        RuleRepository ruleRepository = new RuleRepository();
        Node adult = ruleRepository.evaluateRuleBasedOn(USING_DOB).withValue(age);
        assertThat(adult.getProperty(DatabaseHelper.NODE_NAME).toString(), is("Adult"));
    }

    @Test
    public void shouldReturnAgeDefaultCategoryBasedOnRule() {
        int age = 0;

        RuleRepository ruleRepository = new RuleRepository();
        Node adult = ruleRepository.evaluateRuleBasedOn(USING_DOB).withValue(age);
        assertThat(adult.getProperty(DatabaseHelper.NODE_NAME).toString(), is("Adult"));
    }

    @Test
    public void shouldReturnTenPercentDiscount() {
        int quantity = 3;

        RuleRepository ruleRepository = new RuleRepository();
        Node adult = ruleRepository.evaluateRuleBasedOn("Quantity").withValue(quantity);
        assertThat(adult.getProperty(DatabaseHelper.NODE_NAME).toString(), is("10"));
    }

    @Test
    public void shouldReturnWeatherThereIsRule() {
        RuleRepository ruleRepository = new RuleRepository();
        boolean hasRule = ruleRepository.hasRule();
        assertThat(hasRule, is(true));
    }

    @Test
    public void shouldReturnAllTheRules() throws Exception {
        RuleRepository ruleRepository = new RuleRepository();
        List<Rule> listOfRules = ruleRepository.getRules();
        assertThat(listOfRules.size(), is(2));
    }

    @Ignore
    @Test
    /*
    It's not possible to do in Cypher just yet, because right now the match relationship cannot use wildcard relationshipType.
     */
    public void findByCypher() throws Exception {
        int age = 10;
        GraphDatabaseService db = DatabaseHelper.getInstance().getDatabaseService();
        CypherParser parser = new CypherParser();
        ExecutionEngine engine = new ExecutionEngine(db);
        Query query = parser.parse("START rules=(4) MATCH (rules)-[:RULE]->(rule)-[r,:LESS_THAN]->(answer) WHERE r.Threshold > "+String.valueOf(age)+"  RETURN answer");

        ExecutionResult result = engine.execute(query);

        assertThat(result.columns(), hasItem("answer"));
        Iterator<Node> answerColumn = result.columnAs("answer");

        while( answerColumn.hasNext())
        {
            Node node = answerColumn.next();
            System.out.println(node.getProperty("Name"));
        }
    }

    @Ignore
    @Test
    public void findByPatternMatching() throws Exception {
        final int value = 10;

        PatternNode subReferenceNode = new PatternNode();

        PatternNode rulesNode = new PatternNode();

        PatternNode ruleNode = new PatternNode();
        rulesNode.createRelationshipTo(ruleNode, MyRelationship.RULE, Direction.OUTGOING);

        subReferenceNode.createRelationshipTo(rulesNode, MyRelationship.APPLIES, Direction.OUTGOING);

        final PatternNode possibleAnswerNode = new PatternNode();

        PatternRelationship ruleToAnswerRelationship = ruleNode.createRelationshipTo(possibleAnswerNode, MyRelationship.LESS_THAN);
        RelationshipType relationshipType = ruleToAnswerRelationship.getType();

        if (relationshipType.name().equals("LESS_THAN")) {
            ruleToAnswerRelationship.addPropertyConstraint("Threshold", new ValueMatcher() {
                @Override
                public boolean matches(Object o) {
                    int threshold = (Integer) o;
                    if (o instanceof Integer) {
                        if (value > 0 && value < threshold) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        PatternMatcher matcher = PatternMatcher.getMatcher();
        Iterable<PatternMatch> matches = matcher.match(rulesNode, DatabaseHelper.getInstance().getRuleNode());

        HashSet<String> possibleNodes = new HashSet<String>();

        PatternUtil.printGraph(rulesNode, System.out);

        for (PatternMatch pm : matches) {
            possibleNodes.add((String) pm.getNodeFor(possibleAnswerNode).getProperty("Name"));
        }

        assertThat( possibleNodes, containsOnlyNodeNames("Child"));

    }

}
