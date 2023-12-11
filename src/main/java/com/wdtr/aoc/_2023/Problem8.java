package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.wdtr.aoc._2023.Problem8.Direction.LEFT;

public class Problem8 {

    public static void main(String[] args) throws IOException {
        String filePath = "src/main/resources/aoc-2023/problem8/input.txt";
        Path path = Path.of(filePath);

        Map<String, Node> idToNodeMap = new HashMap<>();
        List<String> lines = Files.lines(path).toList();
        List<Direction> route = parseRoute(lines.get(0));
        for (String nodeLine : lines.subList(2, lines.size())) {
            parseNode(nodeLine, idToNodeMap);
        }


        // QXA
        // PDA
        // TDA
        // QQA
        // PPA
        // AAA

        // HLZ
        // PXZ
        // VJZ
        // NBZ
        // XBZ
        // ZZZ
        // Cycle detection + ggd
        System.out.println("Solution part one: " + countPathLengthFromNodeToNodeUsingRoute(route, idToNodeMap.get("AAA"), idToNodeMap.get("ZZZ")));
        List<Node> nodesEndingInA = new ArrayList<>();
        List<Node> nodesEndingInZ = new ArrayList<>();
        for (String idNode : idToNodeMap.keySet()) {
            if(idNode.endsWith("A")) {
                nodesEndingInA.add(idToNodeMap.get(idNode));
            }
            if(idNode.endsWith("Z")) {
                nodesEndingInZ.add(idToNodeMap.get(idNode));
            }
        }
    }

    private static int countPathLengthFromNodeToNodeUsingRoute(List<Direction> route, Node beginNode, Node endNode) {
        Node currentNode = beginNode;
        int hops = 0;
        while(!currentNode.nodeId.equals(endNode.nodeId)) {
            Direction direction = route.get(hops % route.size());
            if(direction == LEFT) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
            hops++;
        }
        return hops;
    }




    private static List<Direction> parseRoute(String routeString) {
        List<Direction> route = new ArrayList<>();
        for (char currChar : routeString.toCharArray()) {
            if (currChar == 'L') {
                route.add(LEFT);
            } else if (currChar == 'R') {
                route.add(Direction.RIGHT);
            }
        }
        return route;
    }

    private static void parseNode(String nodeLine, Map<String, Node> idToNodeMap) {
        // CTK = (JLT, HRF)
        String[] split = nodeLine.split("=");
        String nodeId = split[0].trim();
        String[] neighbors = split[1].replace("(", "").replace(")", "").split(",");
        String leftNeighborId = neighbors[0].trim();
        String rightNeighborId = neighbors[1].trim();
        Node currentNode = createIfNotExists(nodeId, idToNodeMap);
        Node leftNode = createIfNotExists(leftNeighborId, idToNodeMap);
        Node rightNode = createIfNotExists(rightNeighborId, idToNodeMap);
        currentNode.setLeft(leftNode);
        currentNode.setRight(rightNode);
    }

    public static Node createIfNotExists(String nodeId, Map<String, Node> idToNodeMap) {
        if(idToNodeMap.containsKey(nodeId)) {
            return idToNodeMap.get(nodeId);
        }
        Node newNode = new Node(nodeId, null, null);
        idToNodeMap.put(nodeId, newNode);
        return newNode;
    }

    public enum Direction {
        LEFT,
        RIGHT
    }

    public static final class Node {
        private final String nodeId;
        private Node left;
        private Node right;

        public Node(String nodeId, Node left, Node right) {
            this.nodeId = nodeId;
            this.left = left;
            this.right = right;
        }



        public void setLeft(Node left) {
            this.left = left;
        }


        public void setRight(Node right) {
            this.right = right;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Node) obj;
            return Objects.equals(this.nodeId, that.nodeId) &&
                    Objects.equals(this.left, that.left) &&
                    Objects.equals(this.right, that.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeId, left, right);
        }

        @Override
        public String toString() {
            return  nodeId;
        }


        }
}