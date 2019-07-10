package com.example.kaBank.util;

import java.util.List;

public class ServerPaging<T> {
    private Integer total;		// 전체 ITEM수.

    private List<?> results;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<?> getResults() {
        return results;
    }

    public void setResults(List<?> results) {
        this.results = results;
    }
}