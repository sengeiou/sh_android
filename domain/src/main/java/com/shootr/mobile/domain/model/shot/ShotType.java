package com.shootr.mobile.domain.model.shot;

public interface ShotType {

    String COMMENT = "COMMENT";
    String CTACHECKIN = "CTA_CHECKIN";
    String WAKEUP = "WAKE_UP";
    String CTAGENERICLINK = "CTA_GENERIC_LINK";
    String POLL = "POLL";

    String[] TYPES_SHOWN = { COMMENT, WAKEUP, CTAGENERICLINK, POLL };
}
