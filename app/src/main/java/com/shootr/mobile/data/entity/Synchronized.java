package com.shootr.mobile.data.entity;

import java.util.Date;

public class Synchronized extends LocalSynchronized {

    protected Date birth;
    protected Date modified;
    protected Date deleted;
    protected Integer revision;

    public Synchronized() {

    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getModified() {
        return modified != null ? modified : getBirth();
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Integer getRevision() {
        return revision != null ? revision : 0;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}
