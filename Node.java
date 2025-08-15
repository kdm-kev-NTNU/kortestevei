/**
 * The node class that contains the node number, latitude and longitude.
 */
public class Node {
  int nodeNr;
  double breddegrad;
  double lengdegrad;

  public Node(int nodeNr, double breddegrad, double lengdegrad) {
    this.nodeNr = nodeNr;
    this.breddegrad = breddegrad;
    this.lengdegrad = lengdegrad;
  }
}

/**
 * The edge class that contains only the necessary information, that is the fromNode, tilNode, driveTime.
 */
class Edge {
  int fromNode;
  int tilNode;
  int driveTime;
  public Edge(int fromNode, int toNode, int driveTime) {
    this.fromNode = fromNode;
    this.tilNode = toNode;
    this.driveTime = driveTime;
  }
}

/**
 * The interest point class that contains the node number, code and place name.
 */

class InterestPoint {
  int nodeNr;
  int code;
  String placeName;

  public InterestPoint(int nodeNr, int code, String placeName) {
    this.nodeNr = nodeNr;
    this.code = code;
    this.placeName = placeName;
  }
}