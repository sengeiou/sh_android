package gm.mobi.android.db.model.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.model.objects.AppAdvice;
import gm.mobi.android.db.provider.GMContract.AppAdviceColumns;
import gm.mobi.android.db.provider.GMContract.SyncColumns;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class AppAdviceMapper {

    /**
     * Method which retrieve AppAdvice from AppAdviceDto
     * **/
    public AppAdvice appAdviceDtoToAppAdvice(Map<String,Object> mAppAdviceDto){
        AppAdvice mAppAdvice = new AppAdvice();
        mAppAdvice.setIdAppAdvice(mAppAdviceDto==null ? null : (Long)mAppAdviceDto.get(AppAdviceColumns.ID_APPADVICE));
        mAppAdvice.setPath(mAppAdviceDto == null ? null : (String)mAppAdviceDto.get(AppAdviceColumns.PATH));
        mAppAdvice.setIdMessage(mAppAdviceDto==null ? null :(Long)mAppAdviceDto.get(AppAdviceColumns.ID_MESSAGE));
        mAppAdvice.setPlatform(mAppAdviceDto == null ? null : (Long)mAppAdviceDto.get(AppAdviceColumns.PLATFORM));
        mAppAdvice.setStatus(mAppAdviceDto==null ? null :(Long)mAppAdviceDto.get(AppAdviceColumns.STATUS));
        mAppAdvice.setVisibleButton(mAppAdviceDto==null ? null :(Long)mAppAdviceDto.get(AppAdviceColumns.VISIBLE_BUTTON));
        mAppAdvice.setButtonAction(mAppAdviceDto == null ? null : (String)mAppAdviceDto.get(AppAdviceColumns.BUTTON_ACTION));
        mAppAdvice.setButtonTextId(mAppAdviceDto == null ? null : (Long)mAppAdviceDto.get(AppAdviceColumns.BUTTON_TEXT_ID));
        mAppAdvice.setButtonData(mAppAdviceDto == null ? null : (String)mAppAdviceDto.get(AppAdviceColumns.BUTTON_DATA));
        mAppAdvice.setStartVersion(mAppAdviceDto == null ? null : (Long)mAppAdviceDto.get(AppAdviceColumns.START_VERSION));
        mAppAdvice.setEndVersion(mAppAdviceDto == null ? null : (Long)mAppAdviceDto.get(AppAdviceColumns.END_VERSION));
        mAppAdvice.setStartDate(mAppAdviceDto == null ? null : (Date)mAppAdviceDto.get(AppAdviceColumns.START_DATE));
        mAppAdvice.setEndDate(mAppAdviceDto == null ? null : (Date)mAppAdviceDto.get(AppAdviceColumns.END_DATE));
        mAppAdvice.setWeight(mAppAdviceDto == null ? null : (Long)mAppAdviceDto.get(AppAdviceColumns.WEIGHT));
        mAppAdvice.setCsys_birth(mAppAdviceDto == null ? null : (Date)mAppAdviceDto.get(SyncColumns.CSYS_BIRTH));
        mAppAdvice.setCsys_modified(mAppAdviceDto == null ? null : (Date)mAppAdviceDto.get(SyncColumns.CSYS_MODIFIED));
        mAppAdvice.setCsys_deleted(mAppAdviceDto == null ? null : (Date)mAppAdviceDto.get(SyncColumns.CSYS_DELETED));
        mAppAdvice.setCsys_revision(mAppAdviceDto == null ? null : (Integer)mAppAdviceDto.get(SyncColumns.CSYS_REVISION));
        return mAppAdvice;
    }


    /**
     * Method which retrieve mAppAdviceDto from AppAdvice
     * **/
    public static Map<String,Object> appAdviceToAppAdviceDto(AppAdvice mAppAdvice){
        Map<String,Object>  mAppAdviceDto = new HashMap<>();
        mAppAdviceDto.put(AppAdviceColumns.ID_APPADVICE, mAppAdvice == null ? null : mAppAdvice.getIdAppAdvice());
        mAppAdviceDto.put(AppAdviceColumns.PATH, mAppAdvice == null ? null : mAppAdvice.getPath());
        mAppAdviceDto.put(AppAdviceColumns.ID_MESSAGE, mAppAdvice == null ? null : mAppAdvice.getIdMessage());
        mAppAdviceDto.put(AppAdviceColumns.PLATFORM, mAppAdvice == null ? null : mAppAdvice.getPlatform());
        mAppAdviceDto.put(AppAdviceColumns.STATUS, mAppAdvice == null ? null : mAppAdvice.getStatus());
        mAppAdviceDto.put(AppAdviceColumns.VISIBLE_BUTTON, mAppAdvice == null ? null : mAppAdvice.getVisibleButton());
        mAppAdviceDto.put(AppAdviceColumns.BUTTON_ACTION, mAppAdvice == null ? null : mAppAdvice.getButtonAction());
        mAppAdviceDto.put(AppAdviceColumns.BUTTON_TEXT_ID, mAppAdvice == null ? null : mAppAdvice.getButtonTextId());
        mAppAdviceDto.put(AppAdviceColumns.BUTTON_DATA, mAppAdvice == null ? null : mAppAdvice.getButtonData());
        mAppAdviceDto.put(AppAdviceColumns.START_VERSION, mAppAdvice == null ? null : mAppAdvice.getStartVersion());
        mAppAdviceDto.put(AppAdviceColumns.END_VERSION, mAppAdvice == null ? null : mAppAdvice.getEndVersion());
        mAppAdviceDto.put(AppAdviceColumns.START_DATE, mAppAdvice == null ? null : mAppAdvice.getStartDate());
        mAppAdviceDto.put(AppAdviceColumns.END_DATE, mAppAdvice == null ? null : mAppAdvice.getEndDate());
        mAppAdviceDto.put(AppAdviceColumns.WEIGHT, mAppAdvice == null ? null : mAppAdvice.getWeight());
        mAppAdviceDto.put(SyncColumns.CSYS_BIRTH, mAppAdvice == null ? null : mAppAdvice.getCsys_birth());
        mAppAdviceDto.put(SyncColumns.CSYS_MODIFIED, mAppAdvice == null ? null : mAppAdvice.getCsys_modified());
        mAppAdviceDto.put(SyncColumns.CSYS_DELETED, mAppAdvice == null ? null : mAppAdvice.getCsys_deleted());
        mAppAdviceDto.put(SyncColumns.CSYS_REVISION, mAppAdvice == null ? null : mAppAdvice.getCsys_revision());

        return mAppAdviceDto;
    }


    /**
     * Method which retrieve ContentValues from AppAdviceDto
     * **/
    public static ContentValues getContentValuesFromAppAdviceDto(Map<String,Object> mAppAdviceDto){
        if(mAppAdviceDto == null){
            return null;
        }
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(AppAdviceColumns.ID_APPADVICE, (Long)mAppAdviceDto.get(AppAdviceColumns.ID_APPADVICE));
        mContentValues.put(AppAdviceColumns.PATH, (String)mAppAdviceDto.get(AppAdviceColumns.PATH));
        mContentValues.put(AppAdviceColumns.ID_MESSAGE, (Long)mAppAdviceDto.get(AppAdviceColumns.ID_MESSAGE));
        mContentValues.put(AppAdviceColumns.PLATFORM, (Long)mAppAdviceDto.get(AppAdviceColumns.PLATFORM));
        mContentValues.put(AppAdviceColumns.STATUS,(Integer)mAppAdviceDto.get(AppAdviceColumns.STATUS));
        mContentValues.put(AppAdviceColumns.VISIBLE_BUTTON, (Long)mAppAdviceDto.get(AppAdviceColumns.VISIBLE_BUTTON));
        mContentValues.put(AppAdviceColumns.BUTTON_ACTION, (String)mAppAdviceDto.get(AppAdviceColumns.BUTTON_ACTION));
        mContentValues.put(AppAdviceColumns.BUTTON_TEXT_ID, (Integer) mAppAdviceDto.get(AppAdviceColumns.BUTTON_TEXT_ID));
        mContentValues.put(AppAdviceColumns.BUTTON_DATA, (String)mAppAdviceDto.get(AppAdviceColumns.BUTTON_DATA));
        mContentValues.put(AppAdviceColumns.START_VERSION, (Long)mAppAdviceDto.get(AppAdviceColumns.START_VERSION));
        mContentValues.put(AppAdviceColumns.END_VERSION, (Long)mAppAdviceDto.get(AppAdviceColumns.END_VERSION));
        mContentValues.put(AppAdviceColumns.START_DATE, (Long)mAppAdviceDto.get(AppAdviceColumns.START_DATE));
        mContentValues.put(AppAdviceColumns.END_DATE, (Long)mAppAdviceDto.get(AppAdviceColumns.END_DATE));
        mContentValues.put(AppAdviceColumns.WEIGHT, (Long)mAppAdviceDto.get(AppAdviceColumns.WEIGHT));
        mContentValues.put(SyncColumns.CSYS_BIRTH, (Long)mAppAdviceDto.get(SyncColumns.CSYS_BIRTH));
        mContentValues.put(SyncColumns.CSYS_MODIFIED, (Long)mAppAdviceDto.get(SyncColumns.CSYS_MODIFIED));
        mContentValues.put(SyncColumns.CSYS_DELETED, (Long)mAppAdviceDto.get(SyncColumns.CSYS_DELETED));
        mContentValues.put(SyncColumns.CSYS_REVISION, (Long)mAppAdviceDto.get(SyncColumns.CSYS_REVISION));
        return mContentValues;
    }

    /**
     * Method which retrieve AppAdvice from Cursor
     * **/
    public static AppAdvice getAppAdviceFromCursor(Cursor c){
        AppAdvice appAdvice = new AppAdvice();
        appAdvice.setIdAppAdvice(c.getLong(c.getColumnIndex(AppAdviceColumns.ID_APPADVICE)));
        appAdvice.setPath(c.getString(c.getColumnIndex(AppAdviceColumns.PATH)));
        appAdvice.setIdMessage(c.getLong(c.getColumnIndex(AppAdviceColumns.ID_MESSAGE)));
        appAdvice.setPlatform(c.getLong(c.getColumnIndex(AppAdviceColumns.PLATFORM)));
        appAdvice.setStatus(c.getLong(c.getColumnIndex(AppAdviceColumns.STATUS)));
        appAdvice.setVisibleButton(c.getLong(c.getColumnIndex(AppAdviceColumns.VISIBLE_BUTTON)));
        appAdvice.setButtonAction(c.getString(c.getColumnIndex(AppAdviceColumns.BUTTON_ACTION)));
        appAdvice.setButtonTextId(c.getLong(c.getColumnIndex(AppAdviceColumns.BUTTON_TEXT_ID)));
        appAdvice.setButtonData(c.getString(c.getColumnIndex(AppAdviceColumns.BUTTON_DATA)));
        appAdvice.setStartVersion(c.getLong(c.getColumnIndex(AppAdviceColumns.START_VERSION)));
        appAdvice.setEndVersion(c.getLong(c.getColumnIndex(AppAdviceColumns.END_VERSION)));
        appAdvice.setStartDate(new Date(c.getLong(c.getColumnIndex(AppAdviceColumns.START_DATE))));
        appAdvice.setEndDate(new Date(c.getLong(c.getColumnIndex(AppAdviceColumns.END_DATE))));
        appAdvice.setWeight(c.getLong(c.getColumnIndex(AppAdviceColumns.WEIGHT)));
        appAdvice.setCsys_birth(new Date(c.getLong(c.getColumnIndex(SyncColumns.CSYS_BIRTH))));
        appAdvice.setCsys_modified(new Date(c.getLong(c.getColumnIndex(SyncColumns.CSYS_MODIFIED))));
        appAdvice.setCsys_deleted(new Date(c.getLong(c.getColumnIndex(SyncColumns.CSYS_DELETED))));
        appAdvice.setCsys_revision(c.getInt(c.getColumnIndex(SyncColumns.CSYS_REVISION)));
        return appAdvice;
    }

 }
