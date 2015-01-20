package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EditInfoView;
import javax.inject.Inject;

public class EditInfoPresenter {

    private EditInfoView editInfoView;
    private String eventTitle;
    private String statusText;
    private boolean watching;

    @Inject public EditInfoPresenter() {
    }

    public void initialize(EditInfoView editInfoView, String eventTitle, String statusText, boolean watching) {
        this.editInfoView = editInfoView;
        this.eventTitle = eventTitle;
        this.statusText = statusText;
        this.watching = watching;
        this.updateViewWithInfo();
    }

    public void placeTextChanged() {
        this.updateSendButtonStatus();
    }

    public void sendNewStatus() {
        String placeText = getPlaceText();
        closeScreenWithResult(placeText);
    }

    private void closeScreenWithResult(String statusText) {
        this.editInfoView.closeScreenWithResult(statusText);
    }

    private void updatePlaceInputStatus() {
        this.editInfoView.setFocusOnPlace();
    }

    private void updateSendButtonStatus() {
        editInfoView.setSendButonEnabled(hasChangedInfo());
    }

    private String getPlaceText() {
        String placeText = this.editInfoView.getPlaceText();
        if (placeText != null) {
            placeText = filterPlaceText(placeText);
        }
        return placeText;
    }

    private String filterPlaceText(String placeText) {
        //TODO can't be more than [60] characters (business logic)
        placeText = placeText.trim();
        placeText = placeText.replace("\n", "");
        if (placeText.isEmpty()) {
            placeText = null;
        }
        return placeText;
    }

    private void updateViewWithInfo() {
        this.editInfoView.setTitle(eventTitle);
        this.editInfoView.setPlaceText(statusText);
        this.updatePlaceInputStatus();
        if (watching) {
            this.editInfoView.setMenuShoot();
        } else {
            this.editInfoView.setMenuDone();
        }
    }

    private boolean hasChangedInfo() {
        return placeTextHasChanged();
    }

    private boolean placeTextHasChanged() {
        String newPlaceText = getPlaceText();
        String currentPlaceText = statusText;
        if (currentPlaceText == null) {
            return newPlaceText != null;
        } else {
            return !currentPlaceText.equals(newPlaceText);
        }
    }
}
