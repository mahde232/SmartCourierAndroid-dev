package com.raghdak.wardm.smartcourier.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wardm on 22/11/2017.
 */
public class Delivery implements Serializable {
    private Long id;
    private String address;
    private Integer isUrgent;
    private Double latitude;
    private Double longitude;
    private Integer type;
    private String status;
    private String claimant;
    private String phone;
    private String box;
    private Date dueDate;
    private Date date;
    private String notFound;
    private String receiverName;
    private String floor;
    private String entrance;
    private String numOfFloors;
    private String privateHouse;
    private String signed;
    private String pastedOnDoor;
    private String text;
    private String courierID;
    private ArrayList<String> images_path;
    private ArrayList<String> images_text;

    public Delivery(Long id, ArrayList<String> images_path, ArrayList<String> images_text) {
        this.id = id;
        this.images_path = images_path;
        this.images_text = images_text;
    }

    public Delivery(Long id, String notFound, String receiverName, String floor, String entrance, String numOfFloors, String privateHouse, String signed, String pastedOnDoor) {
        this.id = id;
        this.notFound = notFound;
        this.receiverName = receiverName;
        this.floor = floor;
        this.entrance = entrance;
        this.numOfFloors = numOfFloors;
        this.privateHouse = privateHouse;
        this.signed = signed;
        this.pastedOnDoor = pastedOnDoor;
    }

    public Delivery(double latitude, double longitude, Integer isUrgent, String claimant, String address, String phone, String box, Date dueDate, String courierID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isUrgent = isUrgent;
        this.claimant = claimant;
        this.address = address;
        this.phone = phone;
        this.box = box;
        this.dueDate = dueDate;
        this.courierID = courierID;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id='" + id + '\'' +
                ", lat='" + latitude + '\'' +
                ", lng='" + longitude + '\'' +
                ", status='" + status + '\'' +
                ", isUrgent='" + isUrgent + '\'' +
                ", claimant='" + claimant + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", box='" + box + '\'' +
                ", dueDate=" + dueDate +
                ", date=" + date +
                ", notFound='" + notFound + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", floor='" + floor + '\'' +
                ", entrance='" + entrance + '\'' +
                ", numOfFloors='" + numOfFloors + '\'' +
                ", privateHouse='" + privateHouse + '\'' +
                ", signed='" + signed + '\'' +
                ", pastedOnDoor='" + pastedOnDoor + '\'' +
                ", text='" + text + '\'' +
                ", courierID='" + courierID + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsUrgent() {
        return isUrgent;
    }

    public void setisUrgent(Integer isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        this.claimant = claimant;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotFound() {
        return notFound;
    }

    public void setNotFound(String notFound) {
        this.notFound = notFound;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getNumOfFloors() {
        return numOfFloors;
    }

    public void setNumOfFloors(String numOfFloors) {
        this.numOfFloors = numOfFloors;
    }

    public String getPrivateHouse() {
        return privateHouse;
    }

    public void setPrivateHouse(String privateHouse) {
        this.privateHouse = privateHouse;
    }

    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public String getPastedOnDoor() {
        return pastedOnDoor;
    }

    public void setPastedOnDoor(String pastedOnDoor) {
        this.pastedOnDoor = pastedOnDoor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCourierID() {
        return courierID;
    }

    public void setCourierID(String courierID) {
        this.courierID = courierID;
    }

    public ArrayList<String> getImages_path() {
        return images_path;
    }

    public void setImages_path(ArrayList<String> images_path) {
        this.images_path = images_path;
    }

    public ArrayList<String> getImages_text() {
        return images_text;
    }

    public void setImages_text(ArrayList<String> images_text) {
        this.images_text = images_text;
    }
}


