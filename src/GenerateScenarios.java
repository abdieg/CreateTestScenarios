import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.management.MBeanServerFactory.builder;

public class GenerateScenarios {
    static boolean TESTING = false;
    static Document doc = null;
    static Map<Integer, String> nodeid_value_dict = new HashMap<Integer, String>();
    static List<Integer> final_nodes = new ArrayList<Integer>();
    static Map<Integer, String> final_nodes_value_dict = new HashMap<Integer, String>();
    static List<int[]> edges_list = new ArrayList<int[]>();
    static int starting_node = 999999999;
    static NodeList nlsteps = null;
    static List<String> finalPaths_clone = null;
    static List<String> files_list = new ArrayList<String>();

    public static Document parseGraphml(String graphml_source) {
        File graphml_xml = null;

        try {
            graphml_xml = new File(graphml_source);
        } catch (NullPointerException e) {
            System.out.println("ERROR! THERE IS NO FILE!");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("UNEXPECTED ERROR!");
        }

        Document doc = null;

        try {
            doc = builder.parse(graphml_xml);
        } catch (SAXException e) {
            System.out.println("UNEXPECTED ERROR!");
        } catch (IOException e) {
            System.out.println("ERROR! UNABLE TO READ THE FILE!");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR! UNABLE TO READ THE FILE!");
        }

        return doc;
    }

    public static void getNodeInformation(Document doc) {
        if (doc != null) {
            nlsteps = doc.getElementsByTagName("node");

            if (TESTING) {
                System.out.println("\nTotal nodes: " + nlsteps.getLength());
            }

            for (int i = 0; i < nlsteps.getLength(); i++) {
                Node stepNode = nlsteps.item(i);

                if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) stepNode;
                    String string_stepId = eElement.getAttribute("id").replace("n", "");
                    int stepId = Integer.valueOf(string_stepId);
                    String stepName = eElement.getElementsByTagName("y:Label.Text").item(0).getTextContent();
                    nodeid_value_dict.put(stepId, stepName);

                    if (stepName.contains("#")) {
                        final_nodes.add(stepId);
                        final_nodes_value_dict.put(stepId, stepName);
                    }
                    else if (stepName.toLowerCase().contains("start flow")) {
                        starting_node = stepId;
                    }
                }
            }
        }
        else {
            System.out.println("ERROR! UNABLE TO READ NODES!");
        }
    }

    public static void buildAdjacentMatrix() {
        if (starting_node != 999999999) {
            if (TESTING) {
                System.out.println("\nStarting node: " + starting_node);
                System.out.println("\nFinal nodes:");
                viewHashmap(final_nodes_value_dict);
                System.out.println("\nNodes and its values:");
                viewHashmap(nodeid_value_dict);
            }

            // Get adjacency matrix
            NodeList edrelations = doc.getElementsByTagName("edge");

            if (TESTING)
                System.out.println("\nTotal edges: " + edrelations.getLength());

            for (int i = 0; i < edrelations.getLength(); i++) {
                Node edgeNode = edrelations.item(i);

                if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                    int intArray[] = new int[2];
                    Element eElement = (Element) edgeNode;
                    String string_source = eElement.getAttribute("source").replace("n", "");
                    int source = Integer.valueOf(string_source);
                    intArray[0] = source;
                    String string_target = eElement.getAttribute("target").replace("n", "");
                    int target = Integer.valueOf(string_target);
                    intArray[1] = target;
                    edges_list.add(intArray);
                }
            }

            if (TESTING) {
                viewIntegerArrayList(edges_list);
                System.out.println("");
            }
        }
        else {
            System.out.println("ERROR! UNABLE TO FIND STARTING NODE!");
            System.out.println("FIRST NODE NEEDS TO HAVE THE LABEL 'START FLOW'");
        }
    }

    public static List<String> getAllPossiblePaths() {
        int vertices = -1;
        Paths p = new Paths();

        // Load all available paths from one node to another
        try {
            vertices = nlsteps.getLength();
        } catch (NullPointerException e) {
            System.out.println("ERROR! UNABLE TO GET ALL POSSIBLE PATHS!");
        }

        if (vertices == -1) {
            Graph graph = new Graph(vertices);
            for (int i = 0; i < edges_list.size(); i++) {
                graph.addEdge(edges_list.get(i)[0], edges_list.get(i)[1]);
            }

            // Compute all available paths from one node to another
            for (int i = 0; i < final_nodes.size(); i++) {
                p.getAllPaths(graph, starting_node, final_nodes.get(i));
            }
        }
        return new ArrayList<String>(p.finalPaths);
    }

    public static List<String> getSpecificPath(int end_node) {
        int vertices = -1;
        Paths p = new Paths();

        // Load all available paths from one node to another
        try {
            vertices = nlsteps.getLength();
        } catch (NullPointerException e) {
            System.out.println("ERROR! UNABLE TO GET ALL POSSIBLE PATHS!");
        }

        if (vertices == -1) {
            Graph graph = new Graph(vertices);
            for (int i = 0; i < edges_list.size(); i++) {
                graph.addEdge(edges_list.get(i)[0], edges_list.get(i)[1]);
            }

            // Compute path from starting node to final node defined
            p.getAllPaths(graph, starting_node, end_node);
        }
        return new ArrayList<String>(p.finalPaths);
    }

    public static Map<Integer, String> getFinalNodes(String graphml_source) {
        Map<Integer, String> temp_fn = null;
        doc = parseGraphml(graphml_source);
        getNodeInformation(doc);
        temp_fn = new HashMap<Integer, String>(final_nodes_value_dict);
        clean();

        if (TESTING) {
            System.out.println("Final nodes for this graph");
            viewHasmap(temp_fn);
        }
        return temp_fn;
    }

    public static void generateScenariosInConsole(String graphml_source) {
        // Read GraphML file to link the node id with its step. It also saves the final nodes available
        doc = parseGraphml(graphml_source);

        // Get node information
        getNodeInformation(doc);

        // Build adjacent matrix with edge information
        buildAdjacentMatrix();

        // Get all possible paths
        finalPaths_clone = getAllPossiblePaths();

        if (finalPaths_clone.size() > 0) {
            System.out.println("Available paths");
            System.out.println("--------------------------------------------------");

            for (int i = 0; i < finalPaths_clone.size(); i++) {
                String[] parts = finalPaths_clone.get(i).substring(1).split(",");
                System.out.println("TEST SCENARIO: " + Integer.valueOf(i+1));
                for (int j = 0; j < parts.length; j++) {
                    System.out.println(nodeid_value_dict.get(Integer.valueOf(parts[j])));
                }
                System.out.println("--------------------------------------------------");
            }
        }
        else {
            System.out.println("Available paths: NONE");
        }
    }

    public static void generateScenariosInConsole(String graphml_source, int end_node) {
        // Read GraphML file to link the node id with its step. It also saves the final nodes available
        doc = parseGraphml(graphml_source);

        // Get node information
        getNodeInformation(doc);

        if (isValidFinalNode(end_node)) {
            // Build adjacent matrix with edge information
            buildAdjacentMatrix();

            // Get specific paths
            finalPaths_clone = getSpecificPath(end_node);

            if (finalPaths_clone.size() > 0) {
                System.out.println("Available paths");
                System.out.println("--------------------------------------------------");

                for (int i = 0; i < finalPaths_clone.size(); i++) {
                    String[] parts = finalPaths_clone.get(i).substring(1).split(",");
                    System.out.println("TEST SCENARIO: " + Integer.valueOf(i+1));
                    for (int j = 0; j < parts.length; j++) {
                        System.out.println(nodeid_value_dict.get(Integer.valueOf(parts[j])));
                    }
                    System.out.println("--------------------------------------------------");
                }
            }
            else {
                System.out.println("Available paths: NONE");
            }
        }
        else {
            System.out.println("ERROR! FINAL NODE PROVIDED IS NOT VALID");
        }
    }

}
