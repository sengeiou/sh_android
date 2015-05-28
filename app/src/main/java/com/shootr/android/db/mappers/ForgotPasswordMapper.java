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
        dto.put(DatabaseContract.ForgotPasswordMongo.EMAIL_ENCRYPTED, forgotPasswordResultEntity == null ? null : forgotPasswordResultEntity.getEmailEncripted());
        dto.put(DatabaseContract.ForgotPasswordMongo.ID, forgotPasswordResultEntity == null ? null : forgotPasswordResultEntity.getIdUser());
        dto.put(DatabaseContract.ForgotPasswordMongo.USER_NAME, forgotPasswordResultEntity == null ? null : forgotPasswordResultEntity.getUserName());
        return dto;
    }

    public ForgotPasswordResultEntity fromDto(Map<String, Object> dataItem) {
        ForgotPasswordResultEntity forgotPasswordResultEntity = new ForgotPasswordResultEntity();
        forgotPasswordResultEntity.setIdUser(
          dataItem.containsKey(DatabaseContract.ForgotPasswordMongo.ID) ? (String) dataItem.get(DatabaseContract.UserTable.ID) : null);
        forgotPasswordResultEntity.setUserName(
          dataItem.containsKey(DatabaseContract.ForgotPasswordMongo.USER_NAME) ? (String) dataItem.get(DatabaseContract.UserTable.USER_NAME) : null);
        forgotPasswordResultEntity.setEmailEncripted(
          dataItem.containsKey(DatabaseContract.ForgotPasswordMongo.EMAIL_ENCRYPTED) ? (String) dataItem.get(DatabaseContract.UserTable.EMAIL) : null);
        return forgotPasswordResultEntity;
    }
}
