package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Nicer;
import java.util.List;

public interface NicerRepository {

    List<Nicer> getNicers(String idShot);

}
