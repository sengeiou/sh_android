package com.shootr.mobile.data.entity;

public class SuggestedPeopleEntity extends UserEntity {

    private Long relevance;

    public Long getRelevance() {
        return relevance;
    }

    public void setRelevance(Long relevance) {
        this.relevance = relevance;
    }
}
