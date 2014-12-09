package com.shootr.android.task.validation.shot;

import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.exception.ShootrError;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidator;

public class DuplicatedValidator extends FieldValidator {

    private final ShotEntity previousShot;
    private final String previousComment;
    private final String newShotComment;

    public DuplicatedValidator(ShotEntity previousShot, String newShotComment) {
        this.previousShot = previousShot;
        this.newShotComment = newShotComment;
        previousComment = previousShot.getComment();
    }

    @Override protected int getField() {
        return FieldValidationError.FIELD_COMMENT;
    }

    @Override protected void performValidations() {
        validateDuplicateComment();
    }

    private void validateDuplicateComment() {
        if (sameCommentNotNull() || sameCommentNull()) {
            addError(ShootrError.ERROR_CODE_SHOT_TEXT_DUPLICATED);
        }
    }

    private boolean sameCommentNotNull() {
        return previousComment != null && previousComment.equals(newShotComment);
    }

    private boolean sameCommentNull() {
        return previousComment == null && newShotComment == null;
    }
}
