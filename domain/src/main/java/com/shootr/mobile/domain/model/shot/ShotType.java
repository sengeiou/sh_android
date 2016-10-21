package com.shootr.mobile.domain.model.shot;

public interface ShotType {

    String COMMENT = "COMMENT";
    String CTACHECKIN = "CTA_CHECKIN";
    String WAKEUP = "WAKE_UP";
    String CTAGENERICLINK = "CTA_GENERIC_LINK";

    String[] TYPES_SHOWN = { COMMENT, CTACHECKIN, WAKEUP, CTAGENERICLINK };
}
