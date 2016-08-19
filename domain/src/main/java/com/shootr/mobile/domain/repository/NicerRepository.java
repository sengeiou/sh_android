package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.shot.Nicer;
import java.util.List;

public interface NicerRepository {

    List<Nicer> getNicers(String idShot);

    List<Nicer> getNicersWithUser(String idShot);

    List<Nicer> getNices(String idUser);
}
