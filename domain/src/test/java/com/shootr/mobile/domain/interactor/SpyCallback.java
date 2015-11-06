package com.shootr.mobile.domain.interactor;

import java.util.ArrayList;
import java.util.List;

public class SpyCallback<Result> implements Interactor.Callback<Result> {

    private List<Result> results = new ArrayList<>();
    private Result lastResult;

    @Override public void onLoaded(Result result) {
        this.lastResult = result;
        this.results.add(result);
    }

    public List<Result> results() {
        return results;
    }

    public Result lastResult() {
        return lastResult;
    }

    public Result firstResult() {
        return results.get(0);
    }
}
