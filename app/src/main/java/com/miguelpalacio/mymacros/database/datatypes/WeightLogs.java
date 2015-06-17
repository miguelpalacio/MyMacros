package com.miguelpalacio.mymacros.database.datatypes;

import java.util.List;

public class WeightLogs {

        private List<Double> weights;
        private List<Long> dateLogs;

        public WeightLogs(List<Double> weights, List<Long> dateLogs) {

            this.weights = weights;
            this.dateLogs = dateLogs;
        }

        public List<Double> getWeights() {
            return weights;
        }

        public List<Long> getDateLogs() {
            return dateLogs;
        }
}
