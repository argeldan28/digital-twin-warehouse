package com.warehouse.digitaltwin.engine.pathfinding;

import com.warehouse.digitaltwin.domain.model.GridNode;
import com.warehouse.digitaltwin.domain.model.NodeType;
import com.warehouse.digitaltwin.domain.model.Warehouse;

import java.util.*;

public class AStarPathfinder {

    public List<GridNode> findPath(Warehouse warehouse, GridNode start, GridNode target) {
        if (start.equals(target)) return Collections.singletonList(start);

        PriorityQueue<NodeWrapper> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<GridNode, GridNode> cameFrom = new HashMap<>();
        Map<GridNode, Double> gScore = new HashMap<>();
        
        gScore.put(start, 0.0);
        openSet.add(new NodeWrapper(start, heuristic(start, target)));

        while (!openSet.isEmpty()) {
            GridNode current = openSet.poll().node;

            if (current.equals(target)) {
                return reconstructPath(cameFrom, current);
            }

            for (GridNode neighbor : getNeighbors(warehouse, current)) {
                if (neighbor.getType() == NodeType.OBSTACLE) continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + 1;
                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    double fScore = tentativeGScore + heuristic(neighbor, target);
                    if (openSet.stream().noneMatch(nw -> nw.node.equals(neighbor))) {
                        openSet.add(new NodeWrapper(neighbor, fScore));
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private double heuristic(GridNode a, GridNode b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private List<GridNode> getNeighbors(Warehouse warehouse, GridNode node) {
        List<GridNode> neighbors = new ArrayList<>();
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        for (int i = 0; i < 4; i++) {
            GridNode neighbor = warehouse.getNode(node.getX() + dx[i], node.getY() + dy[i]);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    private List<GridNode> reconstructPath(Map<GridNode, GridNode> cameFrom, GridNode current) {
        List<GridNode> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private static class NodeWrapper {
        GridNode node;
        double fScore;

        NodeWrapper(GridNode node, double fScore) {
            this.node = node;
            this.fScore = fScore;
        }
    }
}
