package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetBlogInteractor;
import com.shootr.android.domain.interactor.stream.GetHelpInteractor;
import com.shootr.android.ui.views.SupportView;
import javax.inject.Inject;

public class SupportPresenter implements Presenter {

    private final GetBlogInteractor getBlogInteractor;
    private final GetHelpInteractor getHelpInteractor;

    private SupportView supportView;
    private Stream blog;
    private Stream help;

    @Inject public SupportPresenter(GetBlogInteractor getBlogInteractor, GetHelpInteractor getHelpInteractor) {
        this.getBlogInteractor = getBlogInteractor;
        this.getHelpInteractor = getHelpInteractor;
    }

    protected void setView(SupportView supportView) {
        this.supportView = supportView;
    }

    public void initialize(SupportView supportView) {
        this.setView(supportView);
        loadBlog();
        loadHelp();
    }

    private void loadHelp() {
        getHelpInteractor.obtainHelpStream(new Interactor.Callback<Stream>() {
            @Override public void onLoaded(Stream helpStream) {
                help = helpStream;
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                supportView.showError();
            }
        });
    }

    private void loadBlog() {
        getBlogInteractor.obtainBlogStream(new Interactor.Callback<Stream>() {
            @Override public void onLoaded(Stream blogStream) {
                blog = blogStream;
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                supportView.showError();
            }
        });
    }

    public void blogClicked() {
        if (blog != null) {
            supportView.goToStream(blog);
        }
    }

    public void helpClicked() {
        if (help != null) {
            supportView.goToStream(help);
        }
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
