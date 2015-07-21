package com.shootr.android.domain.repository;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import java.util.List;

public interface StreamRepository {

    Event getStreamById(String idStream);

    List<Event> getStreamsByIds(List<String> streamIds);

    Event putStream(Event event);

    Event putStream(Event event, boolean notify);

    Integer getListingCount(String idUser);

    void deleteStream(String idEvent) throws DeleteEventNotAllowedException;
}
