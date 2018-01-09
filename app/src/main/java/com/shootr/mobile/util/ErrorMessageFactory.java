package com.shootr.mobile.util;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForFacebookException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForShootrException;
import com.shootr.mobile.domain.exception.MassiveRegisterErrorException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.UserNotFoundException;
import com.shootr.mobile.domain.service.ChangePasswordInvalidException;
import com.shootr.mobile.domain.service.EmailInUseException;
import com.shootr.mobile.domain.service.PollHasBeenDeletedException;
import com.shootr.mobile.domain.service.StreamIsAlreadyInFavoritesException;
import com.shootr.mobile.domain.service.UserCannotVoteException;
import com.shootr.mobile.domain.service.user.CannotAddContributorException;
import com.shootr.mobile.domain.service.user.CannotFollowBlockedUserException;
import com.shootr.mobile.domain.service.user.LoginException;
import com.shootr.mobile.domain.service.user.UserAlreadyCheckedException;
import com.shootr.mobile.domain.service.user.UserCannotCheckInException;
import com.shootr.mobile.domain.service.user.UserHasVotedException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class ErrorMessageFactory {

  public static final int RESOURCE_ERROR_UNKNOWN = R.string.error_message_unknown;

  private Map<String, Integer> codeResourceMap;
  private Context context;

  @Inject public ErrorMessageFactory(Application context) {
    this.context = context.getApplicationContext();
    generateCodeResourceMap();
  }

  private void generateCodeResourceMap() {
    codeResourceMap = new HashMap<>();
    codeResourceMap.put(ShootrError.ERROR_CODE_UNKNOWN_ERROR, RESOURCE_ERROR_UNKNOWN);
    codeResourceMap.put(ShootrError.ERROR_CODE_INVALID_IMAGE, R.string.error_message_invalid_image);
    codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_DUPLICATE,
        R.string.error_message_username_duplicate);
    codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_NULL,
        R.string.error_message_username_empty);
    codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_TOO_SHORT,
        R.string.error_message_username_short);
    codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_TOO_LONG,
        R.string.error_message_username_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_INVALID_CHARACTERS,
        R.string.error_message_username_invalid);
    codeResourceMap.put(ShootrError.ERROR_CODE_NAME_TOO_LONG, R.string.error_message_name_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_NAME_INVALID_CHARACTERS,
        R.string.error_message_name_invalid);
    codeResourceMap.put(ShootrError.ERROR_CODE_WEBSITE_WRONG_URI,
        R.string.error_message_website_invalid);
    codeResourceMap.put(ShootrError.ERROR_CODE_WEBSITE_TOO_LONG,
        R.string.error_message_website_too_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_BIO_TOO_LONG, R.string.error_message_bio_too_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_SHOT_TEXT_DUPLICATED, R.string.new_shot_repeated);
    codeResourceMap.put(ShootrError.ERROR_CODE_SEARCH_TOO_SHORT,
        R.string.error_message_search_too_short);
    codeResourceMap.put(ShootrError.ERROR_CODE_STREAM_TITLE_TOO_SHORT,
        R.string.error_message_stream_title_too_short);
    codeResourceMap.put(ShootrError.ERROR_CODE_STREAM_TITLE_TOO_LONG,
        R.string.error_message_stream_title_too_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE,
        R.string.error_message_registration_username_duplicated);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_NULL,
        R.string.error_message_registration_username_empty);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_SHORT,
        R.string.error_message_registration_username_too_short);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_LONG,
        R.string.error_message_registration_username_too_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_INVALID_CHARACTERS,
        R.string.error_message_registration_username_invalid);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_IN_USE,
        R.string.error_message_registration_email_existing);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT,
        R.string.error_message_registration_email_invalid);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL,
        R.string.error_message_registration_email_empty);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL,
        R.string.error_message_registration_password_null);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT,
        R.string.error_message_registration_password_too_short);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG,
        R.string.error_message_registration_password_too_long);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_EQUALS_USERNAME,
        R.string.error_message_registration_password_equals_username);
    codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS,
        R.string.error_message_registration_password_invalid);
    codeResourceMap.put(ShootrError.ERROR_NEW_PASSWORD_EQUALS_CURRENT_PASSWORD,
        R.string.error_message_change_password_new_password_same_current_password);
    codeResourceMap.put(ShootrError.ERROR_NEW_PASSWORD_NOT_EQUALS_NEW_PASSWORD_AGAIN,
        R.string.error_message_change_password_new_password_and_again_must_match);
    codeResourceMap.put(ShootrError.ERROR_CODE_NEW_PASSWORD_EQUALS_USERNAME,
        R.string.error_message_registration_password_equals_username);
  }

  public String getMessageForError(ShootrError shootrError) {
    return getMessageForCode(shootrError.getErrorCode());
  }

  public String getMessageForCode(String errorCode) {
    return context.getString(getResourceStringForCode(errorCode));
  }

  public String getUnknownErrorMessage() {
    return context.getString(R.string.error_message_unknown);
  }

  public String getCommunicationErrorMessage() {
    return context.getString(R.string.communication_error);
  }

  public String getMassiveRegisterErrorMessage() {
    return context.getString(R.string.error_massive_register);
  }

  public String getConnectionNotAvailableMessage() {
    return context.getString(R.string.connection_lost);
  }

  public String getImageUploadErrorMessage() {
    return context.getString(R.string.error_image_upload);
  }

  @StringRes public Integer getResourceStringForCode(String errorCode) {
    Integer errorStringResource = codeResourceMap.get(errorCode);
    if (errorStringResource == null || errorStringResource <= 0) {
      errorStringResource = RESOURCE_ERROR_UNKNOWN;
      Timber.w("No string resource mapping found for code %s. Using default unknown message",
          errorCode);
    }
    return errorStringResource;
  }

  public String getMessageForError(ShootrException error) {
    if (error instanceof ServerCommunicationException
        || error.getCause() instanceof ServerCommunicationException) {
      return getCommunicationErrorMessage();
    } else if (error instanceof EmailInUseException) {
      return context.getString(R.string.email_already_registered);
    } else if (error instanceof MassiveRegisterErrorException) {
      return context.getString(R.string.error_massive_register);
    } else if (error instanceof StreamIsAlreadyInFavoritesException) {
      return context.getString(R.string.error_stream_already_favorites);
    } else if (error instanceof ShotNotFoundException) {
      return context.getString(R.string.error_deleted_shot);
    } else if (error instanceof UserNotFoundException) {
      return context.getString(R.string.user_not_found);
    } else if (error instanceof LoginException) {
      return context.getString(R.string.error_login_credentials_invalid);
    } else if (error instanceof InvalidLoginMethodForShootrException) {
      return context.getString(R.string.error_login_shootr_method);
    } else if (error instanceof InvalidLoginMethodForFacebookException) {
      return context.getString(R.string.error_login_facebook_method);
    } else if (error instanceof ChangePasswordInvalidException) {
      return context.getString(R.string.error_message_invalid_change_password);
    } else if (error instanceof CannotFollowBlockedUserException) {
      return context.getString(R.string.error_following_user_blocked);
    } else if (error instanceof CannotAddContributorException) {
      return context.getString(R.string.error_adding_contributor);
    } else if (error instanceof UserCannotVoteException) {
      return context.getString(R.string.error_user_cannot_vote);
    } else if (error instanceof UserHasVotedException) {
      return context.getString(R.string.error_has_voted);
    } else if (error instanceof PollHasBeenDeletedException) {
      return context.getString(R.string.error_poll_has_been_deleted);
    } else if (error instanceof UserAlreadyCheckedException) {
      return context.getString(R.string.error_user_already_checked);
    } else if (error instanceof UserCannotCheckInException) {
      return context.getString(R.string.error_user_cannot_check);
    } else {
      return getUnknownErrorMessage();
    }
  }

  public String getStreamReadOnlyError() {
    return context.getString(R.string.error_stream_read_only);
  }
}
