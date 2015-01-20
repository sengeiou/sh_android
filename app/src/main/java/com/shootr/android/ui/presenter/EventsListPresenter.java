package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.EventsListView;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class EventsListPresenter implements Presenter {

    //region Dependencies
    private final Bus bus;

    private EventsListView eventsListView;

    @Inject public EventsListPresenter(Bus bus) {
        this.bus = bus;
    }
    //endregion

    public void initialize(EventsListView eventsListView) {
        this.eventsListView = eventsListView;
        this.loadDefaultEventList();
    }

    private void loadDefaultEventList() {
        //TODO real logic invocation
        eventsListView.renderEvents(getMockList());
    }

    //region Mock data
    private List<EventResultModel> getMockList() {
        EventResultModel event1 = mockEvent("Sevilla-Betis", "Today, 19:00", "http://www.yotufutbol.com/contenido/estadios/spain/ramon%20sanchez%20pizjuan/estadio%20ramon%20sanchez%20pizjuan5.jpg", 5);
        EventResultModel event2 = mockEvent("Órbita Laika 6", "Sun 25 Jan, 23:00", "http://img.rtve.es/imagenes/orbita-laika-tercer-programa/1418917659672.jpg", 2);
        EventResultModel event3 = mockEvent("Sabadell-Barcelona B", "Sat 31 Jan, 22:00", null, 1);
        EventResultModel event4 = mockEvent("Red Hot Chili Peppers - Palacio de Congresos - Sevilla - Línea C4 de cercanías, Renfe", "Sat 31 Jan, 22:00", null, 1);

        return Arrays.asList(event1, event2, event3, event4);
    }

    private EventResultModel mockEvent(String title, String date, String picture, int watchers) {
        EventModel event = new EventModel();
        event.setTitle(title);
        event.setPicture(picture);
        event.setDatetime(date);
        EventResultModel model = new EventResultModel();
        model.setEventModel(event);
        model.setWatchers(watchers);
        return model;
    }
    //endregion

    //region Lifecycle
    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
    //endregion
}
