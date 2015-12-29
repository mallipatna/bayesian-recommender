package nandini.bayes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Bayesian graph contains Bayesian nodes in them. Every node in the graph is an
 * event that has associated conditional probabilities. This will be used to
 * model the joint probability and predict conditional probabilities. The class implements 
 * serializable so that we can store the graph in a file and load later.
 */
public class BayesianGraph implements Serializable {

    // mapping of all the nodes of the graph with their names
    Map<String, BayesianNode> nodes = new HashMap<String, BayesianNode>();

    // add a node to the graph
    public void addNewNode(String name) {
        nodes.put(name, new BayesianNode(name));
    }

    // defines graph adjacency from one node to another in the graph, used for
    // defining conditional probability of events from one event to another
    public boolean addAdjacent(String from, String to) {
        BayesianNode fromNode = nodes.get(from);
        BayesianNode toNode = nodes.get(to);
        if (fromNode == null || toNode == null) {
            System.out.println("invalid from/to");
            return false;
        }
        toNode.addParent(fromNode);
        return true;
    }

    // this method will ask the user for entering the probabilities for each
    // node and conditional probabilities
    public void acceptProbabilities() {
        for (BayesianNode node : nodes.values()) {
            node.acceptProbabilities();
        }
        System.out.println();
    }

    // this method will calculate the joint probability for the given
    // conditions. the conditions are provided as a mapping of the probability
    // of events represented by the node being either true or false
    // for e.g. consider a graph with 3 events(nodes) n1,n2 and n3 
    // In this, calculate
    // P(n1,n2,n3) = P(n1|n2,n3)P(n2|n3)P(n3)
    // for conditions n1=true, n3=false and n2=true, the mapping will be done in
    // the following way:
    // n1->T
    // n2->T
    // n3->F
    public double jointProbability(Map<BayesianNode, Boolean> conditions) {
        if (conditions.size() != nodes.size()) {
            throw new RuntimeException("all nodes should be included");
        }

        double jointProbability = 1.0;

        // calculate conditional probability for each node in the graph
        for (Entry<BayesianNode, Boolean> entry : conditions.entrySet()) {
            BayesianNode node = nodes.get(entry.getKey().name());

            // step 1: first get the parent nodes of the current node
            List<BayesianNode> parents = node.parents();

            // step 2: capture all the parent event conditions
            Map<BayesianNode, Boolean> parentConditions = new HashMap<BayesianNode, Boolean>();
            for (BayesianNode parentNode : parents) {
                parentConditions.put(parentNode, conditions.get(parentNode));
            }

            // step 3: calculate the joint probability as given in the above
            // formula
            // for the above example this will calculate P(n1|n2,n3) in first
            // iteration, P(n2|n3) in second iteration and P(n3) in third
            jointProbability *= node.conditionalProbability(conditions.get(node), parentConditions);
        }
        return jointProbability;
    }

    // returns all the nodes in the graph
    public Map<String, BayesianNode> nodes() {
        return nodes;
    }

    static final long serialVersionUID = 8775054129842270794L;
}
