package com.ottice.ottice.models;

/**
 * TODO: Add a class header comment!
 */

public class BookingsBeanClass {

    private String toDate;
    private String fromDate;
    private String spaceName;
    private String transactionId;
    private String status;
    private String imageUri;
    private String rating;


    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSpaceName() {
        return spaceName;
    }
    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getFromDate() {
        return fromDate;
    }
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }


    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
}
