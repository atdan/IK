package com.example.root.ik.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Post {
    String title, price, description, category, name, phone, userid, postid, imageurl, location;

    public Post(String title, String price, String description, String category,
                String name, String phone, String userid, String postid, String location,
                String imageurl) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.name = name;
        this.phone = phone;
        this.userid = userid;
        this.postid = postid;
        this.location = location;
        this.imageurl = imageurl;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Post() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("title",title);
        result.put("price",price);
        result.put("description",description);
        result.put("name",name);
        result.put("phone",phone);
        result.put("postid", postid);
        result.put("category",category);
        result.put("userid", userid);
        result.put("location", location);
        result.put("imageurl", imageurl);


        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(getTitle(), post.getTitle()) &&
                Objects.equals(getPrice(), post.getPrice()) &&
                Objects.equals(getDescription(), post.getDescription()) &&
                Objects.equals(getCategory(), post.getCategory()) &&
                Objects.equals(getName(), post.getName()) &&
                Objects.equals(getPhone(), post.getPhone()) &&
                Objects.equals(getUserid(), post.getUserid()) &&
                Objects.equals(getPostid(), post.getPostid()) &&
                Objects.equals(getLocation(),post.getLocation()) &&
                Objects.equals(getImageurl(), post.getImageurl());

    }

    @Override
    public int hashCode() {

        return Objects.hash(getTitle(), getPrice(), getDescription(), getCategory(), getName(), getPhone(), getUserid(), getPostid(),getLocation(), getImageurl());
    }
}
