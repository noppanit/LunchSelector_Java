package com.thoughtworks.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.neo4j.graphdb.Node;

import java.util.HashSet;
import java.util.Set;

public class ContainsOnlySpecificNameOfNodes extends TypeSafeMatcher<Iterable<String>> {

    private final Set<String> names;
    private String failedToFindNodeNames;
    private boolean matchedSize;

    public ContainsOnlySpecificNameOfNodes(String... speciesNames) {
        this.names = new HashSet<String>();
        for (String name : speciesNames) {
            names.add(name);
        }
    }

    @Override
    public void describeTo(Description description) {
        if(failedToFindNodeNames != null) {
            description.appendText(String.format("Failed to find nodes [%s] in the given node names", failedToFindNodeNames));
        }
        
        if(!matchedSize) {
            description.appendText(String.format("Mismatched number of nodes, expected [%d]", names.size()));
        }
    }

    @Override
    public boolean matchesSafely(Iterable<String> nodes) {
        
        for (String n : nodes) {
            String speciesName = n;
            
            if (!names.contains(speciesName)) {
                failedToFindNodeNames = speciesName;
                return false;
            } 
            names.remove(speciesName);
        }

        return matchedSize = names.size() == 0;
    }

    @Factory
    public static <T> Matcher<Iterable<String>> containsOnlySpecies(String... speciesNames) {
        return new ContainsOnlySpecificNameOfNodes(speciesNames);
    }
}
