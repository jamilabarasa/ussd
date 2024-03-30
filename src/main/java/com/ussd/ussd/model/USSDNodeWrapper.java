package com.ussd.ussd.model;

import com.ussd.ussd.model.enumerations.FindUSSDNodeFailureReason;

public class USSDNodeWrapper {
    private USSDNode ussdNode;
    private FindUSSDNodeFailureReason reason;

    public USSDNodeWrapper(USSDNode ussdNode, FindUSSDNodeFailureReason reason) {
        this.ussdNode = ussdNode;
        this.reason = reason;
    }

    public USSDNode getUssdNode() {
        return ussdNode;
    }

    public void setUssdNode(USSDNode ussdNode) {
        this.ussdNode = ussdNode;
    }


    public FindUSSDNodeFailureReason getReason() {
        return reason;
    }

    public void setReason(FindUSSDNodeFailureReason reason) {
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
