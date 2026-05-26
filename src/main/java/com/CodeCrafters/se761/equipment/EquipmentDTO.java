package com.CodeCrafters.se761.equipment;

import jakarta.persistence.Lob;

import java.sql.Timestamp;

/**
 * This class is a data Transfer Object (DTO) representing equipment details for API interactions.
 * The purpose of this test is store intermediary information for the JSON when an existing piece
 * of equipment is being edited.
 * Author: Jonathon Lee
 */
public class EquipmentDTO {
    private Long systemid;
    private String description;
    private String brand;
    private String building;
    private String depthMM;
    private String heightMM;
    @Lob
    private String imageURL;
    private String maxUseDay;
    private String model;
    private String normalPosition;
    @Lob
    private String notes;
    private String phase1or3;
    private Boolean pickupAllowed;
    private String powerRating;
    private String room;
    private String serialNumber;
    private String uoaID;
    private String widthMM;
    private Timestamp lastEditTime;
    public String safetyRequirements;
    public String operatingRequirements;

    public EquipmentDTO(Long systemid,
                        String description,
                        String brand,
                        String building,
                        String depthMM,
                        String heightMM,
                        String imageURL,
                        String maxUseDay,
                        String model,
                        String normalPosition,
                        String notes,
                        String phase1or3,
                        Boolean pickupAllowed,
                        String powerRating,
                        String room,
                        String serialNumber,
                        String uoaID,
                        String widthMM,
                        Timestamp lastEditTime,
                        String safetyRequirements,
                        String operatingRequirements) {
        this.systemid = systemid;
        this.description = description;
        this.brand = brand;
        this.building = building;
        this.depthMM = depthMM;
        this.heightMM = heightMM;
        this.imageURL = imageURL;
        this.maxUseDay = maxUseDay;
        this.model = model;
        this.normalPosition = normalPosition;
        this.notes = notes;
        this.phase1or3 = phase1or3;
        this.pickupAllowed = pickupAllowed;
        this.powerRating = powerRating;
        this.room = room;
        this.serialNumber = serialNumber;
        this.uoaID = uoaID;
        this.widthMM = widthMM;
        this.lastEditTime = lastEditTime;
        this.safetyRequirements = safetyRequirements;
        this.operatingRequirements = operatingRequirements;
    }

    public EquipmentDTO() {
    }

    public Long getSystemid() {
        return systemid;
    }

    public void setSystemid(Long systemid) {
        this.systemid = systemid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDepthMM() {
        return depthMM;
    }

    public void setDepthMM(String depthMM) {
        this.depthMM = depthMM;
    }

    public String getHeightMM() {
        return heightMM;
    }

    public void setHeightMM(String heightMM) {
        this.heightMM = heightMM;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMaxUseDay() {
        return maxUseDay;
    }

    public void setMaxUseDay(String maxUseDay) {
        this.maxUseDay = maxUseDay;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNormalPosition() {
        return normalPosition;
    }

    public void setNormalPosition(String normalPosition) {
        this.normalPosition = normalPosition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhase1or3() {
        return phase1or3;
    }

    public void setPhase1or3(String phase1or3) {
        this.phase1or3 = phase1or3;
    }

    public Boolean getPickupAllowed() {
        return pickupAllowed;
    }

    public void setPickupAllowed(Boolean pickupAllowed) {
        this.pickupAllowed = pickupAllowed;
    }

    public String getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(String powerRating) {
        this.powerRating = powerRating;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUoaID() {
        return uoaID;
    }

    public void setUoaID(String uoaID) {
        this.uoaID = uoaID;
    }

    public String getWidthMM() {
        return widthMM;
    }

    public void setWidthMM(String widthMM) {
        this.widthMM = widthMM;
    }

    public Timestamp getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Timestamp lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public String getSafetyRequirements() {
        return safetyRequirements;
    }

    public void setSafetyRequirements(String safetyRequirements) {
        this.safetyRequirements = safetyRequirements;
    }

    public String getOperatingRequirements() {
        return operatingRequirements;
    }

    public void setOperatingRequirements(String operatingRequirements) {
        this.operatingRequirements = operatingRequirements;
    }
}