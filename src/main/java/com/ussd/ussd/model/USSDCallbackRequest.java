package com.ussd.ussd.model;

/**
 * Represents the callback request from the telco
 * To be changed depending on what each telco request looks like
 */
public class USSDCallbackRequest {
    private String sessionId;
    private String serviceCode;
    private String phoneNumber;
    private String text;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "USSDCallbackRequest{" +
                "sessionId='" + sessionId + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
