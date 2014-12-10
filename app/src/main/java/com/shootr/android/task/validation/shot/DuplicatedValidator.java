package com.shootr.android.task.validation.shot;

import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.exception.ShootrError;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidator;

public class DuplicatedValidator extends FieldValidator {

    private final ShotEntity previousShot;
    private final String previousComment;
    private final String newShotComment;
    private String newImageUrl;
    private String previousImageUrl;

    public DuplicatedValidator(ShotEntity previousShot, String newShotComment, String newImageUrl) {
        this.previousShot = previousShot;
        this.newShotComment = newShotComment;
        this.newImageUrl = newImageUrl;
        previousComment = previousShot.getComment();
        previousImageUrl = previousShot.getImage();
    }

    @Override protected int getField() {
        return FieldValidationError.FIELD_COMMENT;
    }

    @Override protected void performValidations() {
        validateDuplicateComment();
    }

    private void validateDuplicateComment() {
        if (isSameComment() && isSameImage()) {
            addError(ShootrError.ERROR_CODE_SHOT_TEXT_DUPLICATED);
        }
    }

    private boolean isSameImage() {
        return sameImageNotNull() || sameImageNull();
    }

    private boolean isSameComment() {
        return sameCommentNotNull() || sameCommentNull();
    }

    private boolean sameImageNotNull() {
        return previousImageUrl != null && previousImageUrl.equals(newImageUrl);
    }

    private boolean sameImageNull() {
        return previousImageUrl == null && newImageUrl == null;
    }

    private boolean sameCommentNotNull() {
        return previousComment != null && previousComment.equals(newShotComment);
    }

    private boolean sameCommentNull() {
        return previousComment == null && newShotComment == null;
    }
}
