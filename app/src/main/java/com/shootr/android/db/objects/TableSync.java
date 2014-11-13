package com.shootr.android.db.objects;

public class TableSync {

    private Integer order;
    private String entity;
    private Long frequency;
    private Long maxTimestamp;
    private Long minTimestamp;
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
        return maxTimestamp;
    }

    public void setMaxTimestamp(Long maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    public Long getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(Long minTimestamp) {
        this.minTimestamp = minTimestamp;
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
