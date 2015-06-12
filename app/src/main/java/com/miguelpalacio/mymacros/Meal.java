package com.miguelpalacio.mymacros;

import java.util.ArrayList;
import java.util.List;

/**
 * Meal class. Matches the definition of Meals in the database.
 */
public class Meal {

    private long id;
    private String name;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;
    private List<MealFood> foods;

    // Constructor.
    public Meal() {
        id = -1;
        name = "";
        protein = 0;
        carbs = 0;
        fat = 0;
        fiber = 0;
        foods = new ArrayList<>();
    }

    // Getters and Setters.

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getProtein() {
        return protein;
    }
    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }
    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }
    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getFiber() {
        return fiber;
    }
    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public List<MealFood> getFoods() {
        return foods;
    }
    public void setFoods(List<MealFood> foods) {
        this.foods = foods;
    }
}
