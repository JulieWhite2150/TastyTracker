package com.example.tastytracker;

/*
 * Object to store information about a food item.
 * Data fields are food name, quantity, and unit.
 */
public class foodItem {
    private String name;
    private double quantity;
    private String unit;

    //Constructor for foodItem
    public foodItem(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    //Getter method for food name
    public String getName() {
        return name;
    }

    //getter method for food quantity
    public double getQuantity() {
        return quantity;
    }

    //getter method for food unit
    public String getUnit(){
        return unit;
    }

    //Setter method for food name (only here for completeness of code)
    public void setName(String name){
        this.name = name;
    }

    //Setter method for food quantity (only here for completeness of code)
    public void setQuantity(double quantity){
        this.quantity = quantity;
    }

    //Setter method for food unit (only here for completeness of code)
    public void setUnit(String unit){
        this.unit = unit;
    }
}

