package com.ussd.ussd.model;

public class USSDNode {

    private Integer id;
    private String display;
    private Integer parent;
    private String condition;
    private String url;
    private boolean terminal;
    private Integer rank;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "USSDNode{" +
                "id=" + id +
                ", display='" + display + '\'' +
                ", parent=" + parent +
                ", condition='" + condition + '\'' +
                ", url='" + url + '\'' +
                ", terminal=" + terminal +
                ", rank=" + rank +
                '}';
    }
}
