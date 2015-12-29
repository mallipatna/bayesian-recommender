package nandini.bayes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * Main java program to start calculate conditional probability using a bayesian
 * network model. User needs to create nodes, input probabilities and the
 * bayesian network model can be used to calculate the conditional probability.
 */
public class MainProgram {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        BayesianGraph graph = new BayesianGraph();

        while (true)
        {
            int key = displayOptions();

            switch (key) {
                case 1:
                    String name = askNodeName();
                    graph.addNewNode(name);
                    break;
                case 2:
                    String from;
                    String to;
                    do {
                        from = askFrom();
                        to = askTo();
                    } while (!graph.addAdjacent(from, to));
                    break;
                case 3:
                    graph.acceptProbabilities();
                    break;
                case 4:
                    Map<BayesianNode, Boolean> conditions = askConditions(graph);
                    System.out.println("Joint probability is: " + graph.jointProbability(conditions));
                    break;
                case 5:
                    computeConditionalPorbability(graph);
                    break;
                case 6:
                    String outFilename = askFileName();
                    saveGraph(graph, outFilename);
                    break;
                case 7:
                    String inFilename = askFileName();
                    graph = loadGraph(inFilename);
                default:
                    break;
            }

            System.out.println();
        }

    }

    public static int displayOptions() {
        System.out.println("1. Enter event");
        System.out.println("2. Relate events");
        System.out.println("3. Accept probabilities");
        System.out.println("4: Joint probability");
        System.out.println("5: Conditional probability");
        System.out.println("6. Save graph");
        System.out.println("7. Load graph");
        System.out.print(">: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public static String askNodeName() {
        System.out.print("Enter the name of the new event: ");
        return scanner.nextLine();
    }

    public static String askFrom() {
        System.out.print("Enter the name of the from event: ");
        return scanner.nextLine();

    }

    public static String askTo() {
        System.out.print("Enter the name of the to event: ");
        return scanner.nextLine();

    }

    public static String askFileName() {
        System.out.print("Enter the name of the file: ");
        return scanner.nextLine();
    }

    // ask user for what probability they need to calculate and also the
    // condition to calculate the conditional probability.
    public static void computeConditionalPorbability(BayesianGraph graph) {

        // ask the condition form the user for e.g probability that an event A
        // is true given that an event B is true.
        System.out.print("Calculate the probability of event: ");
        BayesianNode node1 = graph.nodes().get(scanner.nextLine());
        System.out.print(" being (T/F): ");
        boolean condition1;
        if ("T".equals(scanner.nextLine())) {
            condition1 = true;
        } else {
            condition1 = false;
        }
        System.out.print(" given that the probability of event: ");
        BayesianNode node2 = graph.nodes().get(scanner.nextLine());
        System.out.print(" is (T/F): ");
        Boolean condition2;
        if ("T".equals(scanner.nextLine())) {
            condition2 = true;
        } else {
            condition2 = false;
        }

        // calculate the numerator of the conditional probability equation
        List<BayesianNode> remainingNodes = new ArrayList<BayesianNode>();
        for (BayesianNode bayesianNode : graph.nodes().values()) {
            if (!bayesianNode.equals(node1) && !bayesianNode.equals(node2)) {
                remainingNodes.add(bayesianNode);
            }
        }
        double numerator = 0;
        if (remainingNodes.size() > 0) {
            List<Map<BayesianNode, Boolean>> possibilities = possibilities(remainingNodes, 0);
            for (Map<BayesianNode, Boolean> possibility : possibilities) {
                possibility.put(node1, condition1);
                possibility.put(node2, condition2);
                numerator += graph.jointProbability(possibility);
            }
        } else {
            Map<BayesianNode, Boolean> condition = new HashMap<BayesianNode, Boolean>();
            condition.put(node1, condition1);
            condition.put(node2, condition2);
            numerator = graph.jointProbability(condition);
        }

        // calculate the denominator of the conditional probability equation
        List<BayesianNode> remainingNodesD = new ArrayList<BayesianNode>();
        for (BayesianNode bayesianNode : graph.nodes().values()) {
            if (!bayesianNode.equals(node2)) {
                remainingNodesD.add(bayesianNode);
            }
        }
        double denominator = 0;
        if (remainingNodesD.size() > 0) {
            List<Map<BayesianNode, Boolean>> possibilities = possibilities(remainingNodesD, 0);
            for (Map<BayesianNode, Boolean> possibility : possibilities) {
                possibility.put(node2, condition2);
                denominator += graph.jointProbability(possibility);
            }
        } else {
            Map<BayesianNode, Boolean> condition = new HashMap<BayesianNode, Boolean>();
            condition.put(node1, condition1);
            condition.put(node2, condition2);
            denominator = graph.jointProbability(condition);
        }

        System.out.println("Conditional probability=" + (numerator / denominator));
    }

    private static List<Map<BayesianNode, Boolean>> possibilities(List<BayesianNode> nodes, int from) {

        List<Map<BayesianNode, Boolean>> possibilities = new ArrayList<>();
        if (from == (nodes.size() - 1)) {
            Map<BayesianNode, Boolean> possibility1 = new HashMap<BayesianNode, Boolean>();
            possibility1.put(nodes.get(from), true);
            Map<BayesianNode, Boolean> possibility2 = new HashMap<BayesianNode, Boolean>();
            possibility2.put(nodes.get(from), false);

            possibilities.add(possibility1);
            possibilities.add(possibility2);
            return Collections.synchronizedList(possibilities);
        }

        List<Map<BayesianNode, Boolean>> childPossibilities = possibilities(nodes, from + 1);

        for (Map<BayesianNode, Boolean> childPossibility : childPossibilities) {
            Map<BayesianNode, Boolean> possibility1 = new HashMap<BayesianNode, Boolean>(childPossibility);
            possibility1.put(nodes.get(from), true);
            Map<BayesianNode, Boolean> possibility2 = new HashMap<BayesianNode, Boolean>(childPossibility);
            possibility2.put(nodes.get(from), false);
            possibilities.add(possibility1);
            possibilities.add(possibility2);
        }

        return Collections.synchronizedList(possibilities);

    }

    public static Map<BayesianNode, Boolean> askConditions(BayesianGraph graph) {
        Map<BayesianNode, Boolean> conditions = new HashMap<BayesianNode, Boolean>();
        for (Entry<String, BayesianNode> node : graph.nodes().entrySet()) {
            System.out.print("Enter the condition for " + node.getValue().name() + ": ");
            boolean condition = Boolean.parseBoolean(scanner.nextLine());
            conditions.put(node.getValue(), condition);
        }
        return conditions;
    }

    public static void saveGraph(BayesianGraph graph, String name) {
        try {
            FileOutputStream fos = new FileOutputStream(new File("src/nandini/bayes/" + name));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.close();
        } catch (Exception e) {
            throw new RuntimeException("unable to save graph " + name);
        }
    }

    public static BayesianGraph loadGraph(String name) {
        try {
            FileInputStream fis = new FileInputStream(new File("src/nandini/bayes/" + name));
            ObjectInputStream ois = new ObjectInputStream(fis);
            BayesianGraph graph = (BayesianGraph) ois.readObject();
            ois.close();
            return graph;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
