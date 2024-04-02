package com.ussd.ussd.controller;

import com.ussd.ussd.model.USSDCallbackRequest;
import com.ussd.ussd.service.USSDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ussd")
public class USSDController {

    private final USSDService ussdService;
    private final Logger logger = LoggerFactory.getLogger(USSDController.class);

    public USSDController(USSDService ussdService) {
        this.ussdService = ussdService;
    }

    @PostMapping()
    public String resolveUssdString(@RequestBody USSDCallbackRequest callbackRequest) {
        logger.debug("Request to process callbackRequest", callbackRequest);
        return ussdService.resolveUssdString(callbackRequest);
    }
}
