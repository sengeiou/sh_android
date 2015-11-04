package com.shootr.mobile.service;

public class PaginatedResult<T> {

    int pageLimit;
    int pageOffset;
    int totalItems;
    T result;

    public PaginatedResult(T result) {
        this.result = result;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public PaginatedResult<T> setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

    public int getPageOffset() {
        return pageOffset;
    }

    public PaginatedResult<T> setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
        return this;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public PaginatedResult<T> setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        return this;
    }

    public T getResult() {
        return result;
    }

    public PaginatedResult<T> setResult(T result) {
        this.result = result;
        return this;
    }
}
