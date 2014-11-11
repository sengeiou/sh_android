package com.shootr.android.db.objects;

public class TableSync {

    private Integer order;
    private String entity;
    private Long frequency;
    private Long max_timestamp;
    private Long min_timestamp;
    private Integer minRows;
    private Integer maxRows;
    private String direction;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public Long getMaxTimestamp() {
        return max_timestamp;
    }

    public void setMaxTimestamp(Long max_timestamp) {
        this.max_timestamp = max_timestamp;
    }

    public Long getMinTimestamp() {
        return min_timestamp;
    }

    public void setMinTimestamp(Long min_timestamp) {
        this.min_timestamp = min_timestamp;
    }

    public Integer getMinRows() {
        return minRows;
    }

    public void setMinRows(Integer minRows) {
        this.minRows = minRows;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
