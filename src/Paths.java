import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Paths {
    public List<String> finalPaths = new ArrayList<String>();

    public void print(Graph graph, int start, int end, String path, boolean[] visited) {
        String newPath = path + "," + start;
        visited[start] = true;
        LinkedList<xNode> list = graph.adjacencyList[start];
        for (int i = 0; i < list.size(); i++) {
            xNode node = list.get(i);
            if (node.destination != end && visited[node.destination] == false) {
                print(graph, node.destination, end, newPath, visited);
            } else if (node.destination == end) {
                finalPaths.add(newPath + "," + node.destination);
            }
            visited[start] = false;
        }
    }

    public void getAllPaths(Graph graph, int start, int end) {
        boolean[] visited = new boolean[graph.vertices];
        visited[start] = true;
        print(graph, start, end, "", visited);
    }
}