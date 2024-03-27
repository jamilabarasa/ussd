package com.ussd.ussd.controller;

import com.ussd.ussd.model.USSDNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.USSDService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/ussd")
public class USSDController {


    @Autowired
    private USSDService ussdService;

    @GetMapping("/loadRootNodes")
    public List<USSDNode> loadRootNodes() {
        return ussdService.loadRootNodes();
    }

    @GetMapping("/loadChildrenByParent/{parentId}")
    public List<USSDNode> loadChildrenByParent(@PathVariable int parentId) {
        return ussdService.loadChildrenByParent(parentId);
    }

    @PostMapping("/resolveUssdString")
    public List<USSDNode> resolveUssdString(@RequestBody String ussdString) {
        return ussdService.resolveUssdString(ussdString);
    }



}
