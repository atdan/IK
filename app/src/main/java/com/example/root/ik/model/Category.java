package com.example.root.ik.model;

public class Category {
    private String Name, Price;
    private String Image, Location;

    public Category(){

    }

    public Category(String name, String price,String location, String image) {
        Name = name;
        Price = price;
        Image = image;
        Location = location;

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
