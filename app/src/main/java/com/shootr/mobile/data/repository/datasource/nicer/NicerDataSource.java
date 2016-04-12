package com.shootr.mobile.data.repository.datasource.nicer;

import com.shootr.mobile.data.entity.NicerEntity;
import java.util.List;

public interface NicerDataSource {

    List<NicerEntity> getNicers(String idShot);

    List<NicerEntity> getNicersWithUser(String idShot);
}
