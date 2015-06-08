package com.shootr.android.db.mappers;

import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.db.DatabaseContract;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordMapper extends GenericMapper {

    public ForgotPasswordMapper() {

    }

    public Map<String, Object> toDto(ForgotPasswordResultEntity forgotPasswordResultEntity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.ForgotPassword.EMAIL_ENCRYPTED, forgotPasswordResultEntity == null ? null : forgotPasswordResultEntity.getEmailEncripted());
        dto.put(DatabaseContract.ForgotPassword.ID, forgotPasswordResultEntity == null ? null : forgotPasswordResultEntity.getIdUser());
        dto.put(DatabaseContract.ForgotPassword.USER_NAME, forgotPasswordResultEntity == null ? null : forgotPasswordResultEntity.getUserName());
        return dto;
    }

    public ForgotPasswordResultEntity fromDto(Map<String, Object> dataItem) {
        ForgotPasswordResultEntity forgotPasswordResultEntity = new ForgotPasswordResultEntity();
        forgotPasswordResultEntity.setIdUser((String) dataItem.get(DatabaseContract.ForgotPassword.ID));
        forgotPasswordResultEntity.setUserName((String) dataItem.get(DatabaseContract.ForgotPassword.USER_NAME));
        forgotPasswordResultEntity.setEmailEncripted((String) dataItem.get(DatabaseContract.ForgotPassword.EMAIL_ENCRYPTED));
        return forgotPasswordResultEntity;
    }
}
