package com.thoughtworks.cypher;

import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.SyntaxError;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.neo4j.helpers.collection.IteratorUtil.asIterable;

public class CypherTest {

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
    public void exampleQuery() throws Exception {
        GraphDatabaseService db = new ImpermanentGraphDatabase();
        CypherParser parser = new CypherParser();
        ExecutionEngine engine = new ExecutionEngine(db);
        Query query = parser.parse("start n=(0) where 1=1 return n");
        ExecutionResult result = engine.execute(query);

        assertThat(result.columns(), hasItem("n"));
        Iterator<Node> n_column = result.columnAs("n");
        assertThat(asIterable(n_column), hasItem(db.getNodeById(0)));
        assertThat(result.toString(), containsString("Node[0]"));
    }

    @Test
    public void runSimpleQuery() throws Exception {
        Collection<Node> nodes = testQuery("start n=(0) return n");
        assertThat(nodes, hasItem(db.getReferenceNode()));
    }

    private Collection<Node> testQuery(String query) throws SyntaxError {
        Query compiledQuery = parser.parse(query);
        ExecutionResult result = engine.execute(compiledQuery);
        return IteratorUtil.asCollection(asIterable(result.<Node>columnAs("n")));
    }
}
