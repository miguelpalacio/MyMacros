package com.miguelpalacio.mymacros.datatypes;

import com.miguelpalacio.mymacros.datatypes.Food;

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

    public MealFood(Food food) {
        super(food.getId(), food.getName(), food.getPortionQuantity(), food.getPortionUnits(),
                food.getProtein(), food.getCarbs(), food.getFat(), food.getFiber());
    }

    // Getters and Setters.
    public double getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(double foodQuantity) {
        this.foodQuantity = foodQuantity;
    }
}
