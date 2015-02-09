package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EditInfoView;
import javax.inject.Inject;

public class EditInfoPresenter {

    private EditInfoView editInfoView;
    private String eventTitle;
    private String statusText;

    @Inject public EditInfoPresenter() {
    }

    public void initialize(EditInfoView editInfoView, String eventTitle, String statusText) {
        this.editInfoView = editInfoView;
        this.eventTitle = eventTitle;
        this.statusText = statusText;
        this.updateViewWithInfo();
    }

    public void statusTextChanged() {
        this.updateSendButtonStatus();
    }

    public void sendNewStatus() {
        String statusText = getStatusText();
        closeScreenWithResult(statusText);
    }

    private void closeScreenWithResult(String statusText) {
        this.editInfoView.closeScreenWithResult(statusText);
    }

    private void updateStatusInputStatus() {
        this.editInfoView.setFocusOnStatus();
    }

    private void updateSendButtonStatus() {
        editInfoView.setSendButonEnabled(hasChangedInfo());
    }

    private String getStatusText() {
        String statusText = this.editInfoView.getStatusText();
        if (statusText != null) {
            statusText = filterStatusText(statusText);
        }
        return statusText;
    }

    private String filterStatusText(String statusText) {
        //TODO can't be more than [60] characters (business logic)
        statusText = statusText.trim();
        statusText = statusText.replace("\n", "");
        if (statusText.isEmpty()) {
            statusText = null;
        }
        return statusText;
    }

    private void updateViewWithInfo() {
        this.editInfoView.setStatusText(statusText);
        this.updateStatusInputStatus();
        this.editInfoView.setMenuShoot();
    }

    private boolean hasChangedInfo() {
        return statusTextHasChanged();
    }

    private boolean statusTextHasChanged() {
        String newStatusText = getStatusText();
        String currentStatusText = statusText;
        if (currentStatusText == null) {
            return newStatusText != null;
        } else {
            return !currentStatusText.equals(newStatusText);
        }
    }
}
