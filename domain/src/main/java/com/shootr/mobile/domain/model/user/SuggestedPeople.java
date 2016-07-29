package com.shootr.mobile.domain.model.user;

public class SuggestedPeople {

    private User user;
    private Long relevance;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getRelevance() {
        return relevance;
    }

    public void setRelevance(Long relevance) {
        this.relevance = relevance;
    }
}
