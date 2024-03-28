package com.ussd.ussd.controller;

import com.ussd.ussd.model.USSDNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ussd.ussd.service.USSDService;

import java.util.List;

@RestController
@RequestMapping("api/ussd")
public class USSDController {

    @Autowired
    private USSDService ussdService;

    // TODO best practice for rest controllers is to use nouns instead of verbs
    // reference document - https://www.freecodecamp.org/news/rest-api-best-practices-rest-endpoint-design-examples/

    @GetMapping("/rootNodes")
    public List<USSDNode> loadRootNodes() {
        return ussdService.loadRootNodes();
    }

    @GetMapping("/childNodes/{parentId}")
    public List<USSDNode> loadChildrenByParent(@PathVariable int parentId) {
        return ussdService.loadChildrenByParent(parentId);
    }

    @PostMapping("/ussdString")
    public List<USSDNode> resolveUssdString(@RequestBody String ussdString) {
        return ussdService.resolveUssdString(ussdString);
    }



}
