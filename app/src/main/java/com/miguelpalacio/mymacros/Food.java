package com.miguelpalacio.mymacros;

/**
 * Food class. Matches the definition of Foods in the database.
 */
public class Food {

    private long id;
    private String name;
    private double portionQuantity;
    private String portionUnits;
    private double protein;
    private double carbohydrates;
    private double fat;
    private double fiber;

    // Constructors.

    public Food() {
        this.id = -1;
        this.name = "";
        this.portionQuantity = 0;
        this.portionUnits = "";
        this.protein = 0;
        this.carbohydrates = 0;
        this.fat = 0;
        this.fiber = 0;
    }

    public Food(long id, String name, double portionQuantity, String portionUnits,
                double protein, double carbohydrates, double fat, double fiber) {
        this.id = id;
        this.name = name;
        this.portionQuantity = portionQuantity;
        this.portionUnits = portionUnits;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.fiber = fiber;
    }

/*    public Food(String name, double portionQuantity, String portionUnits,
                double protein, double carbohydrates, double fat, double fiber) {
        this.id = -1;
        this.name = name;
        this.portionQuantity = portionQuantity;
        this.portionUnits = portionUnits;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.fiber = fiber;
    }*/

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

    public double getPortionQuantity() {
        return portionQuantity;
    }
    public void setPortionQuantity(double portionQuantity) {
        this.portionQuantity = portionQuantity;
    }

    public String getPortionUnits() {
        return portionUnits;
    }
    public void setPortionUnits(String portionUnits) {
        this.portionUnits = portionUnits;
    }

    public double getProtein() {
        return protein;
    }
    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }
    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
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
}
