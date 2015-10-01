package com.shootr.android.ui.views;

public interface ProfileView {

    void showListingCount(Integer listingCount);

    void navigateToListing(String idUser);

    void showLogoutInProgress();

    void showError();

    void hideLogoutInProgress();

    void navigateToWelcomeScreen();

    void showLogoutButton();

    void showSupportButton();

    /**
     * @deprecated Este metodo se añade para no refactorizar ahora la carga de shots de ProfileFragment, pero
     * lo marco como deprecated porque el presenter no deberia llamar asi a la vista y este esta interaccion deberia
     * borrarse cuanto sea posible.
     */
    @Deprecated
    void loadLastShots();

    void showChangePasswordButton();

    void showOpenStream();

    void navigateToCreatedStreamDetail(String streamId);

    void showShotShared();

    void setFollowing(Boolean following);
}
