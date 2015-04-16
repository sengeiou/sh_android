package com.shootr.android.domain.interactor;

import java.util.ArrayList;
import java.util.List;

public class SpyCallback<Result> implements Interactor.Callback<Result>{

    public List<Result> results = new ArrayList<>();
    public Result lastResult;

    @Override public void onLoaded(Result result) {
        this.lastResult = result;
        results.add(result);
    }
}
