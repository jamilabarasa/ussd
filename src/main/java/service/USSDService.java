package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ussd.ussd.model.USSDNode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class USSDService {

    private List<USSDNode> ussdNodes;

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

    public List<USSDNode> loadRootNodes() {
        return ussdNodes.stream()
                .filter(node -> node.getParent() == -1)
                .collect(Collectors.toList());
    }

    public List<USSDNode> loadChildrenByParent(int parentId) {
        return ussdNodes.stream()
                .filter(node -> node.getParent() == parentId)
                .collect(Collectors.toList());
    }


    public List<USSDNode> resolveUssdString(String ussdString) {

        return null;
    }

    





}
