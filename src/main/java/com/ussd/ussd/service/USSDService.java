package com.ussd.ussd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ussd.ussd.model.USSDCallbackRequest;
import com.ussd.ussd.model.USSDNode;
import com.ussd.ussd.model.USSDNodeWrapper;
import com.ussd.ussd.model.enumerations.FindUSSDNodeFailureReason;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class USSDService {
    private List<USSDNode> ussdNodes;
    private final Logger log = LoggerFactory.getLogger(USSDService.class);

    @PostConstruct
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/menu-data.json");
            ussdNodes = mapper.readValue(inputStream, new TypeReference<List<USSDNode>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to load root nodes
    public List<USSDNode> loadRootNodes() {

        // TODO: if response exceeds a certain length,show some  add More to the list,for user to see the remaining items in list

        return ussdNodes.stream()
                .filter(node -> node.getParent() == -1)
                .collect(Collectors.toList());
    }

    //function to load child nodes by parent id
    public List<USSDNode> loadChildrenByParent(int parentId) {
        // TODO: if response exceeds a certain length,show some  add More to the list,for user to see the remaining items in list
        // TODO: add option for back ,so we can go back to the previouse node 

//        load children if it is not terminal,and perform the necessary action if it is
//        terminal
        if (isNodeTerminal(parentId)) {

            //call method to perform the action
            return null;
        }

        return ussdNodes.stream()
                .filter(node -> node.getParent() == parentId)
                .collect(Collectors.toList());
    }


    //function to resolve ussd string

    public List<USSDNode> resolveUssdString(String ussdString) {
        // Remove the first five characters (USSD code,*544*) including the first start and the last character (#)
        ussdString = ussdString.substring(5, ussdString.length() - 1);

        // Split the string by asterisk (*) and store the result in an array
        String[] nodes = ussdString.split("\\*");

        int currentnode = Integer.parseInt(nodes[0]);


        // Check if the first node is a valid parent node
        if (nodes.length > 0) {
            try {
                int nodeId = Integer.parseInt(nodes[0]);
                boolean first = true;


                if (findNode(nodeId, first)) {
                    first = false;


                    // Loop through each node in the array
                    // Find the node in ussdNodes list by its ID
                    // If currentNode is null, it means this is the first node in the sequence
                    // Check if it's a root node
                    // If it's a root node, set it as currentNode
                    // If it's not a root node, return all root nodes
                    // Check if the current node is a child of the previous node
                    // If it's a child node, set it as currentNode and continue to the next node
                    // If it's not a child node, return all children of the previous node
                    // if node is terminal true ,call the action function

                    return loadChildrenByParent(nodeId);

                } else {

                    if (first) {
                        // If the first node is not a valid parent node, return all root nodes
                        return loadRootNodes();
                    } else {
                        // If the first node is a valid parent node, return its children

                        return loadChildrenByParent(nodeId);
                    }

                }

            } catch (NumberFormatException e) {
                // If the first element is not a valid number, return all root nodes
                return loadRootNodes();
            }
        } else {
            // If there are no nodes, return all root nodes
            return loadRootNodes();
        }
    }

    public String resolveUssdString(USSDCallbackRequest callbackRequest) {
        log.debug("About to resolve ussd string: {} for user: {}", callbackRequest.getText(), callbackRequest.getPhoneNumber());

        List<USSDNode> nodes = getNodes(callbackRequest.getText());
        if (nodes.isEmpty()) {
            return "END this menu has not yet been populated, please try again later";
        } else if (nodes.size() == 1) {
            // node is terminal and should have a way to fetch the data
            return "END resolving my message";
        } else {
            StringBuilder sb = new StringBuilder("CON Select an option below\n");

            for (USSDNode ussdNode : nodes) {
                sb.append(ussdNode.getRank()).append(". ").append(ussdNode.getDisplay()).append("\n");
            }

            return sb.toString();
        }
    }

    public List<USSDNode> getNodes(String text) {
        List<USSDNode> nodes = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return loadRootNodes();
        }

        USSDNodeWrapper nodeWrapper = findNodeByUSSDString(text);

        switch (nodeWrapper.getReason()) {
            case CHILDREN_NODES_NOT_FOUND:
                return nodes;
            case NODE_IS_TERMINAL:
                nodes.add(nodeWrapper.getUssdNode());
                return nodes;
            default:
                return loadChildrenByParent(nodeWrapper.getUssdNode().getId());
        }

    }

    public USSDNodeWrapper findNodeByUSSDString(String ussdString) {
        String[] nodes = ussdString.split("\\*");

        USSDNode selectedNode = null;
        FindUSSDNodeFailureReason reason = FindUSSDNodeFailureReason.NONE;

        for (int i = 0; i < nodes.length; i++) {
            boolean success = true;
            Integer rank = Integer.parseInt(nodes[i]);

            List<USSDNode> levelNodes;
            if (i == 0) {
                levelNodes = loadRootNodes();
            } else {
                levelNodes = loadChildrenByParent(selectedNode.getId());
            }

            Optional<USSDNode> optionalUSSDNode = levelNodes
                    .stream()
                    .filter(node -> node.getRank().equals(rank))
                    .findFirst();

            if (optionalUSSDNode.isPresent()) {
                selectedNode = optionalUSSDNode.get();
            }

            if (selectedNode.isTerminal()) {
                reason = FindUSSDNodeFailureReason.NODE_IS_TERMINAL;
                success = false;
            } else {
                List<USSDNode> children = loadChildrenByParent(selectedNode.getId());
                if (children == null || children.isEmpty()) {
                    reason = FindUSSDNodeFailureReason.CHILDREN_NODES_NOT_FOUND;
                    success = false;
                }
            }

            if (!success) {
                log.debug("USSD String resolution for string {} broke at index {}, reason is {}", ussdString, i + 1, reason);
                break;
            } else {
                log.debug("USSD String resolution proceeding, selectedNode is {}, isTerminal {}", selectedNode.getDisplay(), selectedNode.isTerminal());
            }

        }

        return new USSDNodeWrapper(selectedNode, reason);
    }

    //function to find the node
    public boolean findNode(int nodeId, boolean first) {

        if (!first) {
            // Find the node in ussdNodes list by its ID
            Optional<USSDNode> optionalNode = ussdNodes.stream()
                    .filter(node -> node.getId() == nodeId)
                    .findFirst();

            return optionalNode.isPresent();
        }

        // Find the node in ussdNodes list by its ID
        Optional<USSDNode> optionalNode = ussdNodes.stream()
                .filter(node -> node.getId() == nodeId && node.getParent() == -1)
                .findFirst();


        return optionalNode.isPresent();
    }

    //function to check if node is terminal
    public boolean isNodeTerminal(int nodeId) {

        // Find the node in ussdNodes list by its ID
        Optional<USSDNode> optionalNode = ussdNodes.stream()
                .filter(node -> node.getId() == nodeId && node.isTerminal() == true)
                .findFirst();


        return optionalNode.isPresent();
    }


}
