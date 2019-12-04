package com.raghdak.wardm.smartcourier.protocol.response;

/**
 * Created by wardm on 23/11/2017.
 */


import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.Delivery;

import java.util.ArrayList;


public class RegionResponse {
    String text;
    private ArrayList<Delivery> deliveries;
    public RegionResponse(String text) {
        super();
        this.text = text;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public ArrayList<Delivery> getDeliveries() {
        return deliveries;
    }
    public void setDeliveries(ArrayList<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
    public static RegionResponse NO_Delivery() {
        return new RegionResponse("No deliveries found!");
    }
    public static RegionResponse OK() {
        return new RegionResponse("OK");
    }

    @Override
    public String toString() {
        return "RegionResponse{" +
                "text='" + text + '\'' +
                ", deliveries=" + deliveries.toString() +
                '}';
    }
}
