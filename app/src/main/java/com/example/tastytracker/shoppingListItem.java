package com.example.tastytracker;

//Object shoppingListItem which is a child of foodItem
public class shoppingListItem extends foodItem{
    private boolean shopped; //Value that stores whether or not an item has been shopped

    //Constructor
    public shoppingListItem(String name, double quantity, String unit, boolean shopped){
        super(name, quantity, unit); //Call super constructor
        this.shopped = shopped;
    }

    //Getter method for shopped
    public boolean getShopped(){
        return shopped;
    }

    //Setter method for shopped
    public void setShopped(boolean newShopped){
        this.shopped = newShopped;
    }
}
