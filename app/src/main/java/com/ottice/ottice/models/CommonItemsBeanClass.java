package com.ottice.ottice.models;

/**
 * TODO: Add a class header comment!
 */
public class CommonItemsBeanClass {

    private int Id;
    private int totalSpaces;

    private String name;
    private String imageUri;
    private String planType;

    private boolean isSelected;


    public CommonItemsBeanClass(){

    }


    // Integer type getter Setter
    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public int getTotalSpaces() {
        return totalSpaces;
    }
    public void setTotalSpaces(int totalSpaces) {
        this.totalSpaces = totalSpaces;
    }


    // String type getter Setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPlanType() {
        return planType;
    }
    public void setPlanType(String planType) {
        this.planType = planType;
    }


    // boolean type getter setter
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
