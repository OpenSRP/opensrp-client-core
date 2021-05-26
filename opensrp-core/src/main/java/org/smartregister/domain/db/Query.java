package org.smartregister.domain.db;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class Query {
    private String query = "";
    private FilterType filterType;

    public String query() {
        return query;
    }

    public Query(FilterType filterType) {
        this.filterType = filterType;
    }

    public Query(FilterType filterType, Query from) {
        this.filterType = filterType;
        if (from != null && !StringUtils.isBlank(from.query)) {
            this.query = "(" + from.query + ")";
        }
    }

    public Query eq(String name, String value) {
        addToQuery(name + ":" + value + " ");
        return this;
    }

    public Query like(String name, String value) {
        addToQuery(name + ":[" + value + " TO " + value + "zz] ");
        return this;
    }

    public Query eq(String name, DateTime value) {
        addToQuery(name + "<date>:[" + value.toString("yyyy-MM-dd") + " TO " + value.toString("yyyy-MM-dd") + "] ");
        return this;
    }

    public Query between(String name, DateTime from, DateTime to) {
        addToQuery(name + "<date>:[" + from.toString("yyyy-MM-dd'T'HH:mm:ss") + " TO " + to.toString("yyyy-MM-dd'T'HH:mm:ss") + "] ");
        return this;
    }

    private void addToQuery(String q) {
        if (!StringUtils.isBlank(query)) {
            query += filterType.name() + " " + q;
        } else query += q;
    }
}