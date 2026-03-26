package com.example.eventmanager.model;

public class Venue {
    private int id;
    private String name;
    private String address;
    private String imageUrl;
    private double rating;
    private int capacity;
    private double area;
    private double price;
    private boolean isPremium;

    public Venue(int id, String name, String address, double rating, int capacity, double area, double price, boolean isPremium) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.capacity = capacity;
        this.area = area;
        this.price = price;
        this.isPremium = isPremium;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
    public int getCapacity() { return capacity; }
    public double getArea() { return area; }
    public double getPrice() { return price; }
    public boolean isPremium() { return isPremium; }
}
