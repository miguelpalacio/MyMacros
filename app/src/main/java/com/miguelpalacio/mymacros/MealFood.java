package com.miguelpalacio.mymacros;

/**
 * This class, inheriting from Food, adds the foodQuantityList property to be used in MyMeals pages.
 */
public class MealFood extends Food {

    private double foodQuantity;

    // Constructors.

    public MealFood() {
        super();
        foodQuantity = 0;
    }

    // Getters and Setters.
    public double getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(double foodQuantity) {
        this.foodQuantity = foodQuantity;
    }
}
