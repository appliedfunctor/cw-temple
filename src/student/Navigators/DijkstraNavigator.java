package student.Navigators;

import student.Maps.CavernMap;
import student.Navigators.Navigator;
import student.Nodes.CavernNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alexander Worton on 05/02/2017.
 * This class attempts to implement Dijkstra's shortest path algorithm,
 * albeit slightly modified from the original to better match the
 * requirements of the task.
 */
public class DijkstraNavigator implements Navigator {

    private CavernMap map;
    private CavernNode startNode;
    private CavernNode destinationNode;
    private List<CavernNode> nodesPendingProcessing;

    public DijkstraNavigator(CavernMap map) {
        setMap(map);
    }

    @Override
    public void setStartNode(CavernNode node) {
        this.startNode = node;
    }

    @Override
    public void setDestinationNode(CavernNode node) {
        this.destinationNode = node;
    }

    @Override
    public List<CavernNode> getPathFromStartToDestination() {
        if(getStartNode() == null || getDestinationNode() == null)
            throw new IllegalStateException("Start and destination nodes must be set before generating path");

        initialiseAllNodes();
        return getShortestPath(map.getAllNodes());
    }

    /**
     * This is the driving method for obtaining the shortest path. Nodes (in order of lowest
     * path value first) which haven't been checked (by having all their connecting nodes examined)
     * are held in a list and popped off when checking is completed.
     * @param allNodes
     * @return the (a) shortest route from start to destination, start at index 0.
     */
    private List<CavernNode> getShortestPath(List<CavernNode> allNodes) {
        nodesPendingProcessing = allNodes;
        CavernNode currentNode;

        while(nodesPendingProcessing.size() > 0) {
            currentNode = getLowestPathValuePendingNode();
            processNeighbours(currentNode);
        }
        return getPath();
    }

    /**
     * To generate the shortest path, we start at the destination and work back one node at
     * a time choosing the first shortest node path value each time. There may be multiple
     * equal distance paths, all but the first are ignored.
     * @return List of CavernNodes with the start at index 0
     */
    private List<CavernNode> getPath() {
        CavernNode currentNode = destinationNode;
        List<CavernNode> path = new LinkedList<>();
        path.add(0,currentNode);
        while (currentNode != startNode){
            currentNode = getLowestPathValueNeighbour(currentNode);
            path.add(0,currentNode);
        }
        return path;
    }

    /**
     * This locates the neighbouring connected node with the lowest value
     * to become the next node in the return path to the start
     * @param currentNode
     * @return
     */
    private CavernNode getLowestPathValueNeighbour(CavernNode currentNode) {
        return map.getConnectedNodes(currentNode).stream()
                .sorted(Comparator.comparingInt(e -> e.getPathValue()))
                .findFirst()
                .get();
    }

    /**
     * All neighbours of a specified node are processed by updating their path value
     * to be the value of the previous node + 1 (since all paths are unweighted) if
     * and only if this new path represents a shorte path (by being a smaller path value
     * @param currentNode the current node being checked
     */
    private void processNeighbours(CavernNode currentNode) {
        List<CavernNode> neighbours = map.getConnectedNodes(currentNode);
        int newPathValue = currentNode.getPathValue()+1;
        neighbours.forEach(n -> {
            if(n.getPathValue() > newPathValue)
                n.setPathValue(newPathValue);
        });
    }

    /**
     * This gets a node holding the lowest available path value out of the set of
     * nodes which have not yet been checked.
     * @return a node with the lowest path value
     */
    private CavernNode getLowestPathValuePendingNode() {
        nodesPendingProcessing = nodesPendingProcessing.stream()
                .sorted(Comparator.comparingInt(e -> e.getPathValue()))
                .collect(Collectors.toList());

        return nodesPendingProcessing.remove(0);
    }

    /***
     * set the initial state of all nodes.
     * - All path values to be set to max, except the start node path value which is set to 0
     */
    protected void initialiseAllNodes() {
        map.getAllNodes().forEach(e -> {
            if(e.equals(startNode))
                e.setPathValue(0);
            else
                e.setPathValue(Integer.MAX_VALUE);
        });
    }

    private void setMap(CavernMap map) {
        this.map = map;
    }

    private CavernNode getStartNode() {
        return startNode;
    }

    private CavernNode getDestinationNode() {
        return destinationNode;
    }
}