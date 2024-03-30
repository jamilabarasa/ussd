package com.ussd.ussd.model;

import com.ussd.ussd.model.enumerations.FindUSSDNodeTerminationReason;

public class FindUSSDNodeResponse {
    private USSDNode ussdNode;
    private FindUSSDNodeTerminationReason reason;

    public FindUSSDNodeResponse(USSDNode ussdNode, FindUSSDNodeTerminationReason reason) {
        this.ussdNode = ussdNode;
        this.reason = reason;
    }

    public USSDNode getUssdNode() {
        return ussdNode;
    }

    public void setUssdNode(USSDNode ussdNode) {
        this.ussdNode = ussdNode;
    }


    public FindUSSDNodeTerminationReason getReason() {
        return reason;
    }

    public void setReason(FindUSSDNodeTerminationReason reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "USSDNodeWrapper{" +
                "ussdNode=" + ussdNode +
                ", reason=" + reason +
                '}';
    }
}
