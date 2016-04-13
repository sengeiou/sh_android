package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.NicerEntity;
import com.shootr.mobile.domain.Nicer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class NicerEntityMapper {

    private final UserEntityMapper userEntityMapper;

    @Inject public NicerEntityMapper(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    public Nicer transform(NicerEntity nicerEntity) {
        Nicer nicer = new Nicer();

        nicer.setIdUser(nicerEntity.getIdUser());
        nicer.setIdNice(nicerEntity.getIdNice());
        nicer.setIdShot(nicerEntity.getIdShot());
        nicer.setUserName(nicerEntity.getUserName());
        if (nicerEntity.getUser() != null) {
            nicer.setUser(userEntityMapper.transform(nicerEntity.getUser()));
        }
        return nicer;
    }

    public List<Nicer> transform(List<NicerEntity> nicerEntityList) {
        List<Nicer> nicers = new ArrayList<>(nicerEntityList.size());

        for (NicerEntity nicerEntity : nicerEntityList) {
            nicers.add(transform(nicerEntity));
        }

        return nicers;
    }

    public Nicer transform(NicerEntity nicerEntity, String currentUserId, Boolean follower, Boolean following) {
        Nicer nicer = transform(nicerEntity);
        nicer.setUser(userEntityMapper.transform(nicerEntity.getUser(), currentUserId, follower, following));
        return nicer;
    }
}
