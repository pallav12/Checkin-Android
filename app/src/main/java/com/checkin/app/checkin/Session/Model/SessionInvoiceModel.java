package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SessionInvoiceModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("ordered_items")
    private List<SessionOrderedItemModel> orderedItems;

    @JsonProperty("bill")
    private SessionBillModel bill;

    @JsonProperty("host")
    private BriefModel host;

    public SessionInvoiceModel(){}

    public String getPk() {
        return pk;
    }

    public List<SessionOrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public SessionBillModel getBill() {
        return bill;
    }

    public BriefModel getHost() {
        return host;
    }
}
