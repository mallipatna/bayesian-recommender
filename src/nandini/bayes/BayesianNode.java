package nandini.bayes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Bayesian node represents an event with conditional probability function with
 * respect to probability of the parent nodes. For example, if there are m
 * parent nodes, then there are 2^m conditional possibilities for the parents to
 * be either true/false. 
 */
public class BayesianNode implements Serializable {

    static Scanner scanner = new Scanner(System.in);

    // the name of the event
    String name;

    // stores conditional probability table (CPT), will only store the
    // event=true case. we can calculate event=false by subtracting it with 1.
    double[] probabilities;

    // list of parent nodes of each node 
    List<BayesianNode> parents;

    public BayesianNode(String name) {
        this.name = name;
        this.parents = new ArrayList<BayesianNode>();
    }

    // add a parent to this node. we will have to later add a conditional
    // probabilities in the CPT.
    public void addParent(BayesianNode parent) {
        parents.add(parent);
    }

    // will ask user for conditional probabilities for populating the CPT
    public void acceptProbabilities() {
        int parentLength = parents.size();
        int probabilitySize = (int) Math.pow(2, parentLength);
        this.probabilities = new double[probabilitySize];

        if (parentLength == 0) {
            System.out.print("enter P(" + name + "):");
            this.probabilities[0] = Double.parseDouble(scanner.nextLine());
        } else {

            // ask the conditional probability for each of the different 2^m
            // combinations of parent event condition (true/false)
            for (int i = 0; i < probabilitySize; i++) {

                String binary = Integer.toBinaryString(i);
                int remaining = parentLength - binary.length();
                for (int j = 0; j < remaining; j++) {
                    binary = "0" + binary;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("enter P(" + name + ") given");
                for (int j = 0; j < binary.length(); j++) {
                    char condition = binary.charAt(j) == '0' ? 'F' : 'T';
                    sb.append(" P(" + parents.get(j).name + ")=" + condition);
                }

                System.out.print(sb.toString() + ":");
                probabilities[i] = Double.parseDouble(scanner.nextLine());

            }
        }
    }

    // calculate the conditional probability of the event being true/false
    // (eventOutcome argument) given
    // the events being either true or false for all parent nodes.
    public double conditionalProbability(boolean eventOutcome, Map<BayesianNode, Boolean> parentEventCondition) {
        if (parentEventCondition.size() != parents.size()) {
            throw new RuntimeException("lengths does not match");
        }

        // there are no parents, hence return the probability of this event only
        if (parents.size() == 0) {
            if (eventOutcome) {
                return probabilities[0];
            } else {
                return 1.0 - probabilities[0];
            }
        }

        // calculate the position of the entry within the CPT from 1 to 2^m
        // entries in the table for the given parent conditions.
        String binary = "";
        for (BayesianNode parent : parents) {
            if (parentEventCondition.get(parent)) {
                binary = binary + "1";
            } else {
                binary = binary + "0";
            }
        }
        int pos = Integer.parseInt(binary, 2);

        if (eventOutcome) {
            return probabilities[pos];
        } else {
            // condition that the event outcome is false =
            // 1 - condition (event outcome is true)
            return 1.0 - probabilities[pos];
        }
    }

    // returns list of parent nodes for this node
    public List<BayesianNode> parents() {
        return parents;
    }

    // returns the event name
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BayesianNode)) {
            return false;
        }
        return ((BayesianNode) obj).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    static final long serialVersionUID = 5045062031967276312L;
}
