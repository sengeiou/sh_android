package com.shootr.mobile.data.repository.datasource.stream;

    import com.shootr.mobile.data.entity.RecentSearchEntity;
    import com.shootr.mobile.data.entity.SearchItemEntity;
    import com.shootr.mobile.data.entity.StreamEntity;
    import com.shootr.mobile.data.entity.UserEntity;
    import java.util.List;

public interface RecentSearchDataSource {

    void putRecentStream(StreamEntity stream, long currentTime);

    void putRecentUser(UserEntity user, long currentTime);

    List<RecentSearchEntity> getRecentSearches();

    void putRecentSearchItems(List<SearchItemEntity> searchItemEntities);

    boolean isRecentSearchEmpty();
}
