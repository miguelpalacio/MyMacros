package com.miguelpalacio.mymacros.database.datatypes;

import java.util.List;

public class WeeklyConsumption {

        private List<Double> caloriesConsumed;
        private List<String> weeks;

        public WeeklyConsumption(List<Double> caloriesConsumed, List<String> weeks) {

            this.caloriesConsumed = caloriesConsumed;
            this.weeks = weeks;
        }

        public List<Double> getCaloriesConsumed() {
            return caloriesConsumed;
        }

        public List<String> getWeeks() {
            return weeks;
        }
}
