package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionBillModel {
    @JsonProperty("subtotal")
    private Double subtotal;

    @JsonProperty("tax")
    private Double tax;

    @JsonProperty("tip")
    private Double tip;

    @JsonProperty("discount")
    private Double discount;

    @JsonProperty("offers")
    private Double offers;

    @JsonProperty("total")
    private Double total;

    private Double discountPercentage;

    public SessionBillModel() {
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getTax() {
        return tax;
    }

    public Double getTip() {
        return tip;
    }

    public Double getDiscount() {
        return discount;
    }

    @JsonProperty("discount")
    public void setDiscount(Double discount) {
        this.discount = (discount > 0) ? discount : null;
    }

    public Double getOffers() {
        return offers;
    }

    @JsonProperty("offers")
    public void setOffers(Double offers) {
        this.offers = (offers > 0) ? offers : null;
    }

    public String formatSubTotal() {
        return String.valueOf(subtotal);
    }

    public String formatTax() {
        return String.valueOf(tax);
    }

    public String formatTip() {
        return String.valueOf(tip);
    }

    public String formatDiscount() {
        return String.valueOf(discount);
    }

    public String formatTotal() {
        return String.valueOf(total);
    }

    public Double getTotal() {
        return total;
    }

    public void calculateDiscount(Double percent) {
        discountPercentage = percent;

        if (discount != null) {
            double oldDiscount = discount;
            discount = (subtotal * percent) / 100;
            total -= (discount - oldDiscount);
        }

    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void giveTip(double tip) {
        this.tip = tip;
        calculateTotal();
    }

    private void calculateTotal() {
        this.total = 0d;
        if (this.subtotal != null)
            this.total += this.subtotal;
        if (this.tip != null)
            this.total += this.tip;
        if (this.tax != null)
            this.total += this.tax;
        if (this.discount != null)
            this.total -= this.discount;
        if (this.offers != null)
            this.total -= this.offers;
    }

    public enum PAYMENT_MODES {
        CASH("csh"), PAYTM("ptm"), GOOGLE_PAY("gpay"), CREDIT_DEBIT("credit_debit"), NET_BANKING("net_banking");

        public String tag;

        PAYMENT_MODES(String tag) {
            this.tag = tag;
        }

        public static PAYMENT_MODES getByTag(String tag) {
            for (PAYMENT_MODES mode : PAYMENT_MODES.values()) {
                if (mode.tag.contentEquals(tag))
                    return mode;
            }
            return CASH;
        }
    }
}
