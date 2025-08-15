import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


// Class to represent a graph and implement Dijkstra's
// shortest path algorithm
class Graph {
  int[][] distFromLandmarks;
  int[][] distToLandmarks;
  private int V; // Number of vertices
  private List<int[]>[] adj; // Adjacency list to store graph edges
  private int nodesPickedDijkstras = 0;
  private int nodesPickedALT = 0;

  // Constructor to initialize the graph
  Graph(int V) {
    this.V = V; //number of vertices in the graph
    adj = new ArrayList[V]; // initializes an adjacency list with the same number of elements as the number of vertices
    for (int i = 0; i < V; ++i)
      adj[i] = new ArrayList<>(); //each element is initialized as a list, to contain the edges of the adjecency matrix
  }

  // Method to add an edge to the graph
  void addEdge(int u, int v, int w) {
    adj[u].add(new int[]{v, w});
    adj[v].add(new int[]{u, w});

    //adds a edge from u til v, and v to u, with the same with within the adj matrix, because it's a bidirectional graph
  }

  /*
  // Method to find the shortest paths from source vertex - Dijkstra's algorithm
  // to all other vertices
  void shortestPath(int src) {
    PriorityQueue<iPair> pq = new PriorityQueue<>();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);

    pq.add(new iPair(src, 0));
    dist[src] = 0;

    // Dijkstra's algorithm
    while (!pq.isEmpty()) {
      int u = pq.poll().vertex;

      for (int[] neighbor : adj[u]) {
        int v = neighbor[0];
        int weight = neighbor[1];

        // Relaxation step
        if (dist[v] > dist[u] + weight) {
          dist[v] = dist[u] + weight;
          pq.add(new iPair(v, dist[v]));
        }
      }
    }

  }

   */

  // Method to find the shortest paths from source vertex - Dijkstra's algorithm
  void runDijkstras(int src, int goal, Graph g) {
    long startTime = System.nanoTime(); //start time
    int[] dist = g.computeShortestPathsFromNode(src); //method to time
    long endTime = System.nanoTime(); //end time
    long duration = (endTime - startTime) / 1000000 ; //time taken in milliseconds
    long centiSeconds = dist[goal];
    long seconds = centiSeconds / 100;
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    seconds = seconds % 60;
    System.out.println("Shortest path found with driving time" + "\t\t" + hours + "h " + minutes + "m " + seconds + "s" + " Found with Dijkstra's algorithm");
    System.out.println("Time taken to find the shortest path: " + duration + " miliseconds");
  }


  // A method to find the shortest path to the 4 interest points
  void shortestPathToInterestPoints(int src, int interestCode, ArrayList<InterestPoint> interestPoints, ArrayList<Node> nodes) {
    PriorityQueue<iPair> pq = new PriorityQueue<>();
    int[] dist = new int[V];
    boolean[] visited = new boolean[V];
    Arrays.fill(dist, Integer.MAX_VALUE);

    pq.add(new iPair(src, 0));
    dist[src] = 0;

    List<InterestPoint> foundInterestPoints = new ArrayList<>();

    while (!pq.isEmpty() && foundInterestPoints.size() < 4) {
      int u = pq.poll().vertex;

      if (visited[u]) continue;  // Skip already visited nodes
      visited[u] = true;


      // Check if this node has the desired interest point but is not the starting node
      if (u != src) {
        for (InterestPoint ip : interestPoints) {
          if (ip.nodeNr == u && ip.code == interestCode) {
            foundInterestPoints.add(ip);
            System.out.println("Found interest point: Node " + ip.nodeNr + ", Place: " + ip.placeName + ", Code: " + ip.code);
            if (foundInterestPoints.size() == 4) break;
          }
        }
      }

      // Explore neighbors
      for (int[] neighbor : adj[u]) {
        int v = neighbor[0];
        int weight = neighbor[1];

        if (!visited[v] && dist[v] > dist[u] + weight) {
          dist[v] = dist[u] + weight;
          pq.add(new iPair(v, dist[v]));
        }
      }
    }

    if (foundInterestPoints.size() < 4) {
      System.out.println("Only " + foundInterestPoints.size() + " interest points found.");
    }
  }

  void preprocessLandmarks(List<Integer> landmarks) {
    int numLandmarks = landmarks.size();
    distFromLandmarks = new int[numLandmarks][V];
    distToLandmarks = new int[numLandmarks][V];

    for (int i = 0; i < numLandmarks; i++) {
      System.out.println("Processing landmark " + (i + 1) + "/" + numLandmarks);

      // Compute distances from landmark[i] to all nodes
      distFromLandmarks[i] = computeShortestPathsFromNode(landmarks.get(i));
      System.gc(); // Force garbage collection after each computation

      // Compute distances to landmark[i] by reversing the graph
      distToLandmarks[i] = computeShortestPathsToNode(landmarks.get(i));
      System.gc(); // Force garbage collection again
    }
  }


  // Helper method to compute the shortest paths from a node (standard Dijkstra)
  private int[] computeShortestPathsFromNode(int src) {
    nodesPickedDijkstras = 0; // Reset the counter
    PriorityQueue<iPair> pq = new PriorityQueue<>();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    pq.add(new iPair(src, 0));

    while (!pq.isEmpty()) {
      nodesPickedDijkstras++;  // Increment the counter
      int u = pq.poll().vertex;
      for (int[] neighbor : adj[u]) {
        int v = neighbor[0];
        int weight = neighbor[1];
        if (dist[v] > dist[u] + weight) {
          dist[v] = dist[u] + weight;
          pq.add(new iPair(v, dist[v]));
        }
      }
    }
    return dist;
  }

  // Helper method to compute the shortest paths to a node (reverse graph)
  private int[] computeShortestPathsToNode(int src) {
    // Create a reversed adjacency list
    List<int[]>[] reverseAdj = new ArrayList[V];
    for (int i = 0; i < V; i++) reverseAdj[i] = new ArrayList<>();
    for (int u = 0; u < V; u++) {
      for (int[] neighbor : adj[u]) {
        reverseAdj[neighbor[0]].add(new int[]{u, neighbor[1]});
      }
    }

    PriorityQueue<iPair> pq = new PriorityQueue<>();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    pq.add(new iPair(src, 0));

    while (!pq.isEmpty()) {
      int u = pq.poll().vertex;
      for (int[] neighbor : reverseAdj[u]) {
        int v = neighbor[0];
        int weight = neighbor[1];
        if (dist[v] > dist[u] + weight) {
          dist[v] = dist[u] + weight;
          pq.add(new iPair(v, dist[v]));
        }
      }
    }
    return dist;
  }

  private int computeHeuristic(int current, int goal) {
    int maxHeuristic = 0;
    for (int i = 0; i < distFromLandmarks.length; i++) {
      int forward = Math.abs(distFromLandmarks[i][goal] - distFromLandmarks[i][current]);
      int backward = Math.abs(distToLandmarks[i][goal] - distToLandmarks[i][current]);
      maxHeuristic = Math.max(maxHeuristic, Math.max(forward, backward));
    }
    return maxHeuristic;
  }


  int shortestPathALT(int src, int goal) {
    nodesPickedALT = 0; // Reset the counter
    PriorityQueue<iPair> pq = new PriorityQueue<>();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);

    dist[src] = 0;
    pq.add(new iPair(src, computeHeuristic(src, goal)));

    while (!pq.isEmpty()) {
      nodesPickedALT++;   // Increment the counter
      int u = pq.poll().vertex;

      if (u == goal) {
        return dist[goal];
      }

      for (int[] neighbor : adj[u]) {
        int v = neighbor[0];
        int weight = neighbor[1];
        int newDist = dist[u] + weight;
        if (dist[v] > newDist) {
          dist[v] = newDist;
          int heuristic = computeHeuristic(v, goal);
          pq.add(new iPair(v, newDist + heuristic));
        }
      }
    }
    System.out.println("Path not found.");
    return -1;
  }

  void runALT(int src, int goal, Graph g){
    long startTime = System.nanoTime(); //start time
    long centiSeconds = g.shortestPathALT(src, goal); //call the method to time
    long endTime = System.nanoTime(); //end time
    long seconds = centiSeconds / 100;
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    seconds = seconds % 60;
    System.out.println("Shortest path found with driving time" + "\t\t" + hours + "h " + minutes + "m " + seconds + "s" + " Found with the ALT algorithm");
    long duration = (endTime - startTime) / 1000000 ; //time taken in milliseconds
    System.out.println("Time taken to find the shortest path: " + duration + " miliseconds");
  }

  // Method to compute the shortest path from src to goal and return the path as an array
  public List<Integer> computeShortestPathWithNodes(int src, int goal) {
    PriorityQueue<iPair> pq = new PriorityQueue<>();
    int[] dist = new int[V];
    int[] previous = new int[V]; // To track the shortest path
    Arrays.fill(dist, Integer.MAX_VALUE);
    Arrays.fill(previous, -1); // Initialize with -1 to indicate no parent
    dist[src] = 0;

    pq.add(new iPair(src, 0));

    while (!pq.isEmpty()) {
      int u = pq.poll().vertex;

      // Stop if we reach the goal early (optimization)
      if (u == goal) break;

      for (int[] neighbor : adj[u]) {
        int v = neighbor[0];
        int weight = neighbor[1];

        // Relaxation step
        if (dist[v] > dist[u] + weight) {
          dist[v] = dist[u] + weight;
          previous[v] = u; // Update the parent node
          pq.add(new iPair(v, dist[v]));
        }
      }
    }

    // If the goal is unreachable, return an empty list
    if (dist[goal] == Integer.MAX_VALUE) {
      System.out.println("No path found from " + src + " to " + goal);
      return new ArrayList<>();
    }

    // Reconstruct the path from goal to src using the `previous` array
    List<Integer> path = new ArrayList<>();
    for (int at = goal; at != -1; at = previous[at]) {
      path.add(at);
    }
    Collections.reverse(path); // Reverse to get the path from src to goal

    return path;
  }


  // Method to write the path coordinates to a file
  public void writePathCoordinatesToFileWithoutIDs(List<Integer> path, List<Node> nodes, String outputFilename) {
    // Configure DecimalFormat to always use '.' as the decimal separator
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    DecimalFormat decimalFormat = new DecimalFormat("0.0000000", symbols); // Seven decimal places

    try (FileWriter writer = new FileWriter(outputFilename)) {
      for (int nodeId : path) {
        // Find the node by ID in the list of nodes
        Node node = nodes.get(nodeId); // Assuming node IDs match indices in the list
        String latitude = decimalFormat.format(node.breddegrad);
        String longitude = decimalFormat.format(node.lengdegrad);
        writer.write(latitude + "\t" + longitude + "\n");
      }
      System.out.println("Path coordinates written to " + outputFilename);
    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
    } catch (IndexOutOfBoundsException e) {
      System.err.println("Node ID not found in the list of nodes: " + e.getMessage());
    }
  }





  //Main class containing the main method to test the graph
// and Dijkstra's algorithm
  public static class Dijkstra {

    private static void interestPointMenu() {

      System.out.println("Her er bit og betydning for valg av interessepuntker            \n" +
          "1 Stedsnavn               \n" +
          "2 Bensinstasjon           \n" +
          "4 Ladestasjon             \n" +
          "8 Spisested               \n" +
          "16 Drikkested             \n" +
          "32 Overnattingssted");


    }

    public static void main(String[] args) throws FileNotFoundException {

      try {

      //Norden

      ArrayList<Edge> edgeData = ReadFiles.readEdgeFromFile("kanter.txt"); //- Norden testing
      ArrayList<Node> nodeData = ReadFiles.readNodesFromFile("noder.txt"); //- Norden testing
      ArrayList<InterestPoint> interestData = ReadFiles.readInterestFromFile("interessepkt.txt"); //- Norden testing

        //number of vertices in "Norden"
        int numberOfVertices = nodeData.size();

        Graph g = new Graph(numberOfVertices);
        for (Edge e : edgeData) {
          g.addEdge(e.fromNode, e.tilNode, e.driveTime);
        }

        //source and goal

        int sourceNode = 2486870;
        int goalNode = 5394165;

        System.out.println("For å bekreft at progrmmet fungerer har vi valgt å teste det med det første eksempelet fra LF");
        System.out.println("\n");
        System.out.println("\n");

        //Dijkstra's algorithm
        g.runDijkstras(sourceNode, goalNode, g); //Norden
        List<Integer> path = g.computeShortestPathWithNodes(sourceNode, goalNode);
        System.out.println("The number of nodes between src and goal is: " + path.size());
        System.out.println("The amount of nodes picked out of the priority queue is: " + g.nodesPickedDijkstras);


        //ALT algorithm
        List<Integer> landmarks = Arrays.asList(372809, 1859230, 373059, 1853227); //landmarks for norden
        g.preprocessLandmarks(landmarks);
        g.runALT(sourceNode, goalNode, g); //Norden
        System.out.println("The number of nodes between src and goal is: " + path.size());
        System.out.println("The amount of nodes picked out of the priority queue is: " + g.nodesPickedALT);

        // Write the path coordinates to a file
        System.out.println(path);
        g.writePathCoordinatesToFileWithoutIDs(path, nodeData, "path_coordinates.txt");

        Scanner scanner = new Scanner(System.in);

        boolean value = true;

        while (value) {

          interestPointMenu();

          try {
            System.out.println("Please enter a number from the list: [1, 2, 4, 8, 16, 32]");
            int interestValue = scanner.nextInt();
            System.out.println("Please enter the node number you want to start from: ");
            int nodeNr = scanner.nextInt();

            // Check if the answer is in the valid list
            if (interestValue == 1 || interestValue == 2 || interestValue == 4 || interestValue == 8 || interestValue == 16 || interestValue == 32) {
              //value = false;  // Exit the loop if valid
              System.out.println("You selected: " + interestValue);
              g.shortestPathToInterestPoints(nodeNr, interestValue, interestData, nodeData);
            } else {
              System.out.println("You need to type a whole number that is on the list.");
            }
          } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
            scanner.nextLine();  // Clear the invalid input
          }
        }


      } catch (FileNotFoundException e) {
        System.out.println("File not found");
      }

    }

  }

  // Inner class to represent a pair of vertex and its
  // weight
  class iPair implements Comparable<iPair> {
    int vertex, weight;

    iPair(int v, int w) {
      vertex = v;
      weight = w;
    }

    // Comparison method for priority queue
    public int compareTo(iPair other) {
      return Integer.compare(this.weight,
          other.weight);
    }
  }

}

