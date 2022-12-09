package com.said.oubella.so.artanddesign.network;

public enum Order {

    RELEVANCE("relevance"),
    NEWEST("newest"),
    OLDEST("oldest");

    private final String order;

    Order(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }
}
