package com.ussd.ussd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ussd.ussd.model.FindUSSDNodeResponse;
import com.ussd.ussd.model.USSDCallbackRequest;
import com.ussd.ussd.model.USSDNode;
import com.ussd.ussd.model.enumerations.FindUSSDNodeTerminationReason;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            ussdNodes = new ArrayList<>();
            log.error("system|encountered exception {}", e.getMessage());
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

    public String resolveUssdString(USSDCallbackRequest callbackRequest) {
        String response = null;
        log.debug("transaction|phoneNumber={}|sessionId={}|ussdString={}|about to resolve ussd string",
                callbackRequest.getPhoneNumber(), callbackRequest.getSessionId(), callbackRequest.getText());

        List<USSDNode> nodes = getNodes(callbackRequest.getText());
        log.debug("transaction|phoneNumber={}|sessionId={}|ussdString={}|extracted {} nodes",
                callbackRequest.getPhoneNumber(), callbackRequest.getSessionId(), callbackRequest.getText(), nodes.size());

        if (nodes.isEmpty()) {
            response = "END this menu has not yet been populated, please try again later";
        } else if (nodes.size() == 1) {
            // node is terminal and should have a way to fetch the data
            return "END resolving my message";
        } else {
            StringBuilder sb = new StringBuilder("CON Select an option below\n");
            for (USSDNode ussdNode : nodes) {
                sb.append(ussdNode.getRank()).append(". ").append(ussdNode.getDisplay()).append("\n");
            }
            response = sb.toString();
        }

        log.debug("transaction|phoneNumber={}|sessionId={}|ussdString={}|about to respond to user",
                callbackRequest.getPhoneNumber(), callbackRequest.getSessionId(), callbackRequest.getText());
        return response;
    }

    public List<USSDNode> getNodes(String text) {
        List<USSDNode> nodes = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return loadRootNodes();
        }

        FindUSSDNodeResponse nodeWrapper = findNodeByUSSDString(text);

        return switch (nodeWrapper.getReason()) {
            case CHILDREN_NODES_NOT_FOUND -> nodes;
            case NODE_IS_TERMINAL -> {
                nodes.add(nodeWrapper.getUssdNode());
                yield nodes;
            }
            default -> loadChildrenByParent(nodeWrapper.getUssdNode().getId());
        };

    }

    public FindUSSDNodeResponse findNodeByUSSDString(String ussdString) {
        String[] nodes = ussdString.split("\\*");

        USSDNode selectedNode = null;
        FindUSSDNodeTerminationReason reason = FindUSSDNodeTerminationReason.NONE;

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

            assert selectedNode != null;
            if (selectedNode.isTerminal()) {
                reason = FindUSSDNodeTerminationReason.NODE_IS_TERMINAL;
                success = false;
            } else {
                List<USSDNode> children = loadChildrenByParent(selectedNode.getId());
                if (children == null || children.isEmpty()) {
                    reason = FindUSSDNodeTerminationReason.CHILDREN_NODES_NOT_FOUND;
                    success = false;
                }
            }

            if (!success) {
                break;
            }
        }

        return new FindUSSDNodeResponse(selectedNode, reason);
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
                .filter(node -> node.getId() == nodeId && node.isTerminal())
                .findFirst();

        return optionalNode.isPresent();
    }

}
