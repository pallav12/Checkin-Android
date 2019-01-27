package com.checkin.app.checkin.Session.Model;

import android.support.annotation.Nullable;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class RestaurantTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

    @Nullable
    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("event")
    private EventBriefModel event;

    @JsonProperty("created")
    private Date created;

    public RestaurantTableModel() {}

    public RestaurantTableModel(long pk, String table, @Nullable BriefModel host, EventBriefModel event) {
        this.pk = pk;
        this.table = table;
        this.host = host;
        this.event = event;
    }

    public long getPk() {
        return pk;
    }

    public String getTable() {
        return table;
    }

    @Nullable
    public BriefModel getHost() {
        return host;
    }

    public EventBriefModel getEvent() {
        return event;
    }

    public Date getCreated() {
        return created;
    }
}