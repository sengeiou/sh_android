package com.shootr.android.data.repository.sync;

public abstract class SyncableEntityFactory<Domain, Entity> {


    public Entity currentOrNewEntity(Domain domain) {
        Entity currentEntity = currentEntity(domain);
        if (currentEntity == null) {
            return createNewEntity(domain);
        } else {
            return updateValues(currentEntity, domain);
        }
    }

    protected abstract Entity currentEntity(Domain domain);

    protected abstract Entity updateValues(Entity currentEntity, Domain watch);

    protected abstract Entity createNewEntity(Domain domain);
}
