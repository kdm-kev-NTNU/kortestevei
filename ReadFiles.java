import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class ReadFiles {

  public static ArrayList<Node> readNodesFromFile(String filename) throws FileNotFoundException {
    Scanner scanner = new Scanner(new File(filename));
    scanner.useLocale(Locale.US);

    ArrayList<Node> nodes = new ArrayList<>();
      //save the first line to a variable:
      int firstLine = scanner.nextInt();
      System.out.println("Number of nodes: " + firstLine);
    for (int i = 0; i < firstLine; i++) {
        int id = scanner.nextInt();
        double lat = scanner.nextDouble();
        double lon = scanner.nextDouble();
        nodes.add(new Node(id, lat, lon));
    }
    if (nodes.size()==firstLine) {
        System.out.println("All nodes read");
      } else {
        System.out.println("Nodes: " + nodes.size() + " Expected: " + firstLine);
      }
    return nodes;
  }

  public static ArrayList<Edge> readEdgeFromFile(String filename) throws FileNotFoundException {
    Scanner scanner = new Scanner(new File(filename));
    ArrayList<Edge> edges = new ArrayList<>();
    //save the first line to a variable:
    int firstLine = scanner.nextInt();
    System.out.println("Number of edges: " + firstLine);
    for (int i = 0; i < firstLine; i++) {
      int fromNode = scanner.nextInt();
      int toNode = scanner.nextInt();
      int driveTime = scanner.nextInt();
      int clear1 = scanner.nextInt();
      int clear2 = scanner.nextInt();
      edges.add(new Edge(fromNode, toNode, driveTime));
    }

    if (edges.size()==firstLine) {
      System.out.println("All edges read");
    } else {
      System.out.println("Edges: " + edges.size() + " Expected: " + firstLine);
    }
    return edges;
  }


  public static ArrayList<InterestPoint> readInterestFromFile(String filename) throws FileNotFoundException {
    Scanner scanner = new Scanner(new File(filename));
    ArrayList<InterestPoint> interestPoints = new ArrayList<>();
    int numberOfPoints = 0;

    if (scanner.hasNextLine()) {
      String firstLine = scanner.nextLine().trim();

      try {
        numberOfPoints = Integer.parseInt(firstLine);
        System.out.println("Number of Points: " + numberOfPoints);
        for (int i = 0; i < numberOfPoints; i++) {
          if (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            Scanner lineScanner = new Scanner(line);
            if (lineScanner.hasNextInt()) {
              int nodeNr = lineScanner.nextInt();
              if (lineScanner.hasNextInt()) {
                int someValue = lineScanner.nextInt();
                if (lineScanner.hasNext()) {
                  String description = lineScanner.next();
                  interestPoints.add(new InterestPoint(nodeNr, someValue, description));
                } else {
                  System.out.println("Expected description at line " + (i + 2));
                  break;
                }
              } else {
                System.out.println("Expected integer value at line " + (i + 2));
                break;
              }
            } else {
              System.out.println("Expected node number at line " + (i + 2));
              break;
            }
            lineScanner.close();
          } else {
            System.out.println("Expected more lines in the file");
            break;
          }
        }
      } catch (NumberFormatException e) {
        System.out.println("Expected number of nodes at the first line");
      }
    } else {
      System.out.println("File is empty or does not contain the expected number of nodes");
    }

    if (interestPoints.size() == numberOfPoints) {
      System.out.println("All interest points read");
    } else {
      System.out.println("Interest points: " + interestPoints.size() + " Expected: " + numberOfPoints);
    }
    return interestPoints;
  }

  public static void main(String[] args) {
    try {
      ArrayList<Edge> edgesFromNorden = readEdgeFromFile("Norden/kanter.txt");
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
  }
}
