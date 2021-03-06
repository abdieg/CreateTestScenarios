import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateScenarios {
    static final String init_wording = "Start-flow";
    static final String final_wording = "#";
    static boolean TESTING = false;
    static Document doc = null;
    static Map<Integer, String> node_id_value_dict = new HashMap<Integer, String>();
    static List<Integer> final_nodes = new ArrayList<Integer>();
    static Map<Integer, String> final_nodes_value_dict = new HashMap<Integer, String>();
    static List<int[]> edges_list = new ArrayList<int[]>();
    static int starting_node = 999999999;
    static NodeList nodes_steps = null;
    static List<String> finalPaths_clone = null;
    static List<String> files_list = new ArrayList<String>();

    public static Document parseGraphml(String graphml_source) {
        if (TESTING)
            System.out.println("\nWe are reading the following file: " + graphml_source);

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
            System.out.println("UNEXPECTED ERROR UPON PARSING DOCUMENT BUILDER!");
        } catch (NullPointerException e) {
            System.out.println("ERROR! DOCUMENT BUILDER IS NULL!");
        }

        Document doc = null;

        try {
            doc = builder.parse(graphml_xml);

            if (TESTING)
                System.out.println("\nGraphML file successfully parsed");
        } catch (SAXException e) {
            System.out.println("UNEXPECTED ERROR WHEN PARSING THE DOCUMENT!");
        } catch (NullPointerException e) {
            System.out.println("UNEXPECTED ERROR DUE TO EMPTY DOCUMENT!");
        } catch (IOException e) {
            System.out.println("ERROR! UNABLE TO READ THE FILE!");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR! UNABLE TO READ THE FILE DUE TO BAD ARGUMENTS!");
        }

        return doc;
    }

    public static void getNodeInformation(Document doc) {
        if (doc != null) {
            nodes_steps = doc.getElementsByTagName("node");

            if (TESTING)
                System.out.println("\nTotal nodes: " + nodes_steps.getLength());

            for (int i = 0; i < nodes_steps.getLength(); i++) {
                Node stepNode = nodes_steps.item(i);

                if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) stepNode;
                    String string_stepId = eElement.getAttribute("id").replace("n", "");
                    int stepId = Integer.valueOf(string_stepId);
                    String stepName = eElement.getElementsByTagName("y:Label.Text").item(0).getTextContent();
                    node_id_value_dict.put(stepId, stepName);

                    if (stepName.contains(final_wording)) {
                        final_nodes.add(stepId);
                        final_nodes_value_dict.put(stepId, stepName);

                        if (TESTING)
                            System.out.println("\nFinal nodes added to their respective variable");
                    }
                    else if (stepName.contains(init_wording)) {
                        starting_node = stepId;

                        if (TESTING)
                            System.out.println("\nInitial node found");
                    }
                }
            }

            if (TESTING) {
                System.out.println("\nThis is how the ending nodes look after adding them to the dictionary:");
                viewHashmap(final_nodes_value_dict);
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
                viewHashmap(node_id_value_dict);
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
            vertices = nodes_steps.getLength();
        } catch (NullPointerException e) {
            System.out.println("ERROR! UNABLE TO GET ALL POSSIBLE PATHS!");
        }

        if (vertices != -1) {
            Graph graph = new Graph(vertices);
            for (int i = 0; i < edges_list.size(); i++) {
                graph.addEdge(edges_list.get(i)[0], edges_list.get(i)[1]);

                if(TESTING)
                    System.out.println("Edge from " + edges_list.get(i)[0] + " to " + edges_list.get(i)[1]);
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
            vertices = nodes_steps.getLength();
        } catch (NullPointerException e) {
            System.out.println("ERROR! UNABLE TO GET ALL POSSIBLE PATHS!");
        }

        if (vertices != -1) {
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
            viewHashmap(temp_fn);
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
            System.out.println("The following scenarios were created as per selection made\n");
            System.out.println("--------------------------------------------------");

            for (int i = 0; i < finalPaths_clone.size(); i++) {
                String[] parts = finalPaths_clone.get(i).substring(1).split(",");
                System.out.println("TEST SCENARIO: " + Integer.valueOf(i+1));
                for (int j = 0; j < parts.length; j++) {
                    System.out.println(node_id_value_dict.get(Integer.valueOf(parts[j])));
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
                System.out.println("The following scenarios were created as per selection made\n");
                System.out.println("--------------------------------------------------");

                for (int i = 0; i < finalPaths_clone.size(); i++) {
                    String[] parts = finalPaths_clone.get(i).substring(1).split(",");
                    System.out.println("TEST SCENARIO: " + Integer.valueOf(i+1));
                    for (int j = 0; j < parts.length; j++) {
                        System.out.println(node_id_value_dict.get(Integer.valueOf(parts[j])));
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

    public static void generateScenariosInTextFile(String dir) {
        String export_folder = dir + "/" + createFolder(dir, "TestCasesGenerated");

        if (TESTING)
            System.out.println("Export folder: " + export_folder);

        getGraphmlFilesInCurrentFolder(dir);
        System.out.println("\nTest cases will be automatically generated for the following files:");
        viewStringList(files_list);

        BufferedWriter bw = null;
        FileWriter fw = null;

        for (int i = 0; i < files_list.size(); i++) {
            try {
                fw = new FileWriter(export_folder + "/" + files_list.get(i) + ".txt");
                bw = new BufferedWriter(fw);

                // Read GraphML file to link the node id with its step. It also saves the final nodes available
                doc = parseGraphml(dir +  "/" + files_list.get(i));

                // Get node information
                getNodeInformation(doc);

                // Build adjacent matrix with edge information
                buildAdjacentMatrix();

                // Get all possible paths
                finalPaths_clone = getAllPossiblePaths();

                if (finalPaths_clone.size() > 0) {
                    bw.write("The following scenarios were created as per selection made\n");
                    bw.write("--------------------------------------------------\n");

                    for (int a = 0; a < finalPaths_clone.size(); a++) {
                        String[] parts = finalPaths_clone.get(a).substring(1).split(",");
                        bw.write("TEST SCENARIO: " + Integer.valueOf(a+1) + "\n");
                        for (int j = 0; j < parts.length; j++) {
                            bw.write(node_id_value_dict.get(Integer.valueOf(parts[j])) + "\n");
                        }
                        bw.write("--------------------------------------------------\n");
                    }
                }
                else {
                    System.out.println("Available paths: NONE");
                }
            } catch (IOException e) {
                System.out.println("ERROR! THERE WAS A PROBLEM WRITING THE TEXT FILE");
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    System.out.println("ERROR! THERE WAS A PROBLEM CLOSING THE TEXT FILE");
                }
            }
            clean();
        }
    }

    public static void generateScenariosInTextFile(String dir, String graphml_name) {
        String export_folder = dir;

        if (TESTING)
            System.out.println("Export folder: " + export_folder);

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(export_folder + "/" + graphml_name + ".txt");
            bw = new BufferedWriter(fw);

            // Read GraphML file to link the node id with its step. It also saves the final nodes available
            doc = parseGraphml(export_folder +  "/" + graphml_name);

            // Get node information
            getNodeInformation(doc);

            // Build adjacent matrix with edge information
            buildAdjacentMatrix();

            // Get all possible paths
            finalPaths_clone = getAllPossiblePaths();

            if (finalPaths_clone.size() > 0) {
                bw.write("Scenarios created for file: " + graphml_name + "\n");
                bw.write("--------------------------------------------------\n");

                for (int a = 0; a < finalPaths_clone.size(); a++) {
                    String[] parts = finalPaths_clone.get(a).substring(1).split(",");
                    bw.write("TEST SCENARIO: " + Integer.valueOf(a+1) + "\n");
                    for (int j = 0; j < parts.length; j++) {
                        bw.write(node_id_value_dict.get(Integer.valueOf(parts[j])) + "\n");
                    }
                    bw.write("--------------------------------------------------\n");
                }
            }
            else {
                System.out.println("Available paths: NONE");
            }
        } catch (IOException e) {
            System.out.println("ERROR! THERE WAS A PROBLEM WRITING THE TEXT FILE");
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                System.out.println("ERROR! THERE WAS A PROBLEM CLOSING THE TEXT FILE");
            }
        }
        clean();
    }

    public static void generateScenariosInTextFile(String dir, String graphml_name, int end_node) {
        String export_folder = dir;

        if (TESTING)
            System.out.println("Export folder: " + export_folder);

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            // Read GraphML file to link the node id with its step. It also saves the final nodes available
            doc = parseGraphml(dir +  "/" + graphml_name);

            // Get node information
            getNodeInformation(doc);

            if (isValidFinalNode(end_node)) {
                // Build adjacent matrix with edge information
                buildAdjacentMatrix();

                // Clean the file name so it does not cause any trouble
                String endingName = cleanFileName(node_id_value_dict.get(end_node));
                if (TESTING)
                    System.out.println(endingName);

                // Use the JIRA format to label the file
//                String[] sprintAndStory = graphml_name.split(" - ");
//                String sprintNumber = sprintAndStory[0];
//                String jiraNumber = sprintAndStory[1];

//                fw = new FileWriter(export_folder + "/" + sprintNumber + " - " + jiraNumber + " - " + endingName + " - " + generateTimestamp() + ".txt");
                fw = new FileWriter(export_folder + "/" + endingName + " - " + generateTimestamp() + ".txt");
                bw = new BufferedWriter(fw);

                // Get specific paths
                finalPaths_clone = getSpecificPath(end_node);

                if (finalPaths_clone.size() > 0) {
                    bw.write("The following scenarios were created as per final testing outcome selected\n");
                    bw.write("--------------------------------------------------\n");

                    for (int a = 0; a < finalPaths_clone.size(); a++) {
                        String[] parts = finalPaths_clone.get(a).substring(1).split(",");
//                        bw.write("TEST SCENARIO: " + Integer.valueOf(a + 1) + "\n");
                        for (int j = 0; j < parts.length; j++) {
                            bw.write(node_id_value_dict.get(Integer.valueOf(parts[j])) + "\n");
                        }
                        bw.write("--------------------------------------------------\n");
                    }
                } else {
                    System.out.println("Available paths: NONE");
                }
            }
            else {
                System.out.println("ERROR! FINAL NODE PROVIDED IS NOT VALID");
            }
        } catch (IOException e) {
            System.out.println("ERROR! THERE WAS A PROBLEM WRITING THE TEXT FILE");
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                System.out.println("ERROR! THERE WAS A PROBLEM CLOSING THE TEXT FILE");
            }
        }
        clean();
    }

    public static void clean() {
        if (TESTING)
            System.out.println("\nCleaning document...");
        doc = null;

        if (TESTING)
            System.out.println("Cleaning nodeid dictionary...");
        node_id_value_dict.clear();

        if (TESTING)
            System.out.println("Cleaning finalnodes list...");
        final_nodes.clear();

        if (TESTING)
            System.out.println("Cleaning finalnodes dictionary...");
        final_nodes_value_dict.clear();

        if (TESTING)
            System.out.println("Cleaning edges...");
        edges_list.clear();

        if (TESTING)
            System.out.println("Resetting starting node...");
        starting_node = 999999999;

        if (TESTING)
            System.out.println("Cleaning nodes_steps list...");
        nodes_steps = null;

        if (TESTING)
            System.out.println("Cleaning finalpathsclone list...");
        if (finalPaths_clone != null) {
            finalPaths_clone.clear();
        }
    }

    public static boolean isValidFinalNode(int final_node) {
        boolean is_valid_final = false;
        for (int i = 0; i < final_nodes.size(); i++) {
            if (final_nodes.get(i) == final_node) {
                is_valid_final = true;
            }
        }
        return is_valid_final;
    }

    public static String createFolder(String location, String folderName) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd-hh-mm-ss");
        String export_dir_name = folderName + "_" + dateFormat.format(now);
        File file_export_dir = new File(location + "/" + export_dir_name);
        file_export_dir.mkdir();

        return file_export_dir.getName();
    }

    public static void getGraphmlFilesInCurrentFolder(String dir) {
        File currentDir = new File(dir);
        File[] filesList = currentDir.listFiles();
        for (File f : filesList) {
            if (!f.isDirectory()) {
                if (f.isFile()) {
                    if (f.getName().endsWith(".graphml")) {
                        files_list.add(f.getName());
                    }
                }
            }
        }
    }

    public static Map<Integer, String> showGraphmlFilesInCurrentFolder(String dir) {
        Map<Integer, String> number_file = new HashMap<Integer, String>();
        int i = 0;
        File currentDir = new File(dir);
        File[] filesList = currentDir.listFiles();
        for (File f : filesList) {
            if (!f.isDirectory()) {
                if (f.isFile()) {
                    if (f.getName().endsWith(".graphml")) {
                        number_file.put(i, f.getName());
                        i++;
                    }
                }
            }
        }
        return number_file;
    }

    public static void viewHashmap(Map<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    public static void viewIntegerArrayList(List<int[]> intList) {
        for (int i = 0; i < intList.size(); i++) {
            System.out.println("Source: " + intList.get(i)[0] + " | target: " + intList.get(i)[1]);
        }
    }

    public static void viewIntegerList(List<Integer> intList) {
        for (int i = 0; i < intList.size(); i++) {
            System.out.println(intList.get(i));
        }
    }

    public static void viewStringList(List<String> strList) {
        for (int i = 0; i < strList.size(); i++) {
            System.out.println(strList.get(i));
        }
    }

    public static String cleanFileName(String str) {
        String cleanStr = str
                .replace("\\", "")
                .replace("/", "")
                .replace(":", "")
                .replace("*", "")
                .replace("?", "")
                .replace("<", "")
                .replace(">", "")
                .replace("|", "")
                .replace(".", "");

        return cleanStr;
    }

    public static String generateTimestamp() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String export_timestamp = sdf.format(now);

        return export_timestamp;
    }

}