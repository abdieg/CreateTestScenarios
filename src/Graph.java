import java.util.LinkedList;

public class Graph{
    int vertices;
    LinkedList<xNode> [] adjacencyList;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjacencyList = new LinkedList[vertices];
        for (int i = 0; i < vertices; i++)
            adjacencyList[i] = new LinkedList<xNode>();
    }

    public void addEdge(int source, int destination) {
        xNode node = new xNode(source, destination);
        adjacencyList[source].addLast(node);
    }
}