package com.ottice.ottice.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class ProvidersBeanClass implements Parcelable{

    private int Id;
    private int seats;
    private int typeId;

    private String providerId;
    private String spaceName;
    private String state;
    private String city;
    private String address;
    private String imageData;
    private String typeName;
    private String typeImageData;
    private String spacePlan;

    private double latitude;
    private double longitude;

    private long price;
    private String rating;

    private ArrayList<Amenities> amenitiesList;

    public ProvidersBeanClass(){}


    protected ProvidersBeanClass(Parcel in) {
        Id = in.readInt();
        seats = in.readInt();
        typeId = in.readInt();
        providerId = in.readString();
        spaceName = in.readString();
        state = in.readString();
        city = in.readString();
        address = in.readString();
        imageData = in.readString();
        typeName = in.readString();
        typeImageData = in.readString();
        spacePlan = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        price = in.readLong();
        rating = in.readString();
        amenitiesList = in.createTypedArrayList(Amenities.CREATOR);
    }

    public static final Creator<ProvidersBeanClass> CREATOR = new Creator<ProvidersBeanClass>() {
        @Override
        public ProvidersBeanClass createFromParcel(Parcel in) {
            return new ProvidersBeanClass(in);
        }

        @Override
        public ProvidersBeanClass[] newArray(int size) {
            return new ProvidersBeanClass[size];
        }
    };

    // int type getter setter
    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public int getSeats() {
        return seats;
    }
    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getTypeId() {
        return typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }


    // long type getter setter
    public long getPrice() {
        return price;
    }
    public void setPrice(long price) {
        this.price = price;
    }



    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    // string type getter setter
    public String getProviderId() {
        return providerId;
    }
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getSpaceName() {
        return spaceName;
    }
    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageData() {
        return imageData;
    }
    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeImageData() {
        return typeImageData;
    }
    public void setTypeImageData(String typeImageData) {
        this.typeImageData = typeImageData;
    }

    public String getSpacePlan() {
        return spacePlan;
    }
    public void setSpacePlan(String spacePlan) {
        this.spacePlan = spacePlan;
    }


    // double type getter setter
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



    // arrayList type getter setter
    public ArrayList<Amenities> getAmenitiesList() {
        return amenitiesList;
    }
    public void setAmenitiesList(ArrayList<Amenities> amenitiesList) {
        this.amenitiesList = amenitiesList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeInt(seats);
        parcel.writeInt(typeId);
        parcel.writeString(providerId);
        parcel.writeString(spaceName);
        parcel.writeString(state);
        parcel.writeString(city);
        parcel.writeString(address);
        parcel.writeString(imageData);
        parcel.writeString(typeName);
        parcel.writeString(typeImageData);
        parcel.writeString(spacePlan);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeLong(price);
        parcel.writeString(rating);
        parcel.writeTypedList(amenitiesList);
    }


    public static class Amenities implements Parcelable{
        private String amenitiesImageData;
        private String amenityName;

        public  Amenities(){}


        Amenities(Parcel in) {
            amenitiesImageData = in.readString();
            amenityName = in.readString();
        }

        public static final Creator<Amenities> CREATOR = new Creator<Amenities>() {
            @Override
            public Amenities createFromParcel(Parcel in) {
                return new Amenities(in);
            }

            @Override
            public Amenities[] newArray(int size) {
                return new Amenities[size];
            }
        };

        // string type getter setter
        public String getAmenitiesImageData() {
            return amenitiesImageData;
        }
        public void setAmenitiesImageData(String amenitiesImageData) {
            this.amenitiesImageData = amenitiesImageData;
        }

        public String getAmenityName() {
            return amenityName;
        }
        public void setAmenityName(String amenityName) {
            this.amenityName = amenityName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(amenitiesImageData);
            parcel.writeString(amenityName);
        }
    }
}
