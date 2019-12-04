package com.raghdak.wardm.smartcourier.model;


import java.io.Serializable;

/**
 * Created by wardm on 22/11/2017.
 */
public class Region implements Serializable {
    private String id;
    private String regionName;
    private String threshold;
    private String courier;
    private String delivery;

    public Region(String email, String password) {
        super();
        this.regionName = regionName;
        this.threshold = threshold;
        this.courier = courier;
        this.delivery = delivery;
    }
    public Region(String id, String email, String regionName, String threshold) {
        super();
        this.id = id;
        this.regionName = regionName;
        this.threshold = threshold;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRegionName() {
        return regionName;
    }
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    public String getThreshold() {
        return threshold;
    }
    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }
    public String getCourier() {
        return courier;
    }
    public void setCourier(String courier) {
        this.courier = courier;
    }
    public String getDelivery() {
        return delivery;
    }
    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    @Override
    public String toString() {
        return "Courier [regionName=" + regionName + ", threshold=" + threshold + /*", firstName=" + firstName + ", LastName=" + lastName
                + ", address=" + address + ", */ " id=" + id + "]";
    }

}

