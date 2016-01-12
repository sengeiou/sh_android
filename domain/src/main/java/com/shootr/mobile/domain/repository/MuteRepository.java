package com.shootr.mobile.domain.repository;

import java.util.List;

public interface MuteRepository {

    void mute(String idStream);

    void unmute(String idStream);

    List<String> getMutedIdStreams();
}
