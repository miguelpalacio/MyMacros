package com.miguelpalacio.mymacros.datatypes;

import java.util.List;

public class MacrosConsumed {

        private List<Double> proteinConsumed;
        private List<Double> carbsConsumed;
        private List<Double> fatConsumed;
        private List<Long> dateLogs;

        public MacrosConsumed(List<Double> proteinConsumed, List<Double> carbsConsumed,
                              List<Double> fatConsumed, List<Long> dateLogs) {

            this.proteinConsumed = proteinConsumed;
            this.carbsConsumed = carbsConsumed;
            this.fatConsumed = fatConsumed;
            this.dateLogs = dateLogs;
        }

        public List<Double> getProteinConsumed() {
            return proteinConsumed;
        }

        public List<Double> getCarbsConsumed() {
            return carbsConsumed;
        }

        public List<Double> getFatConsumed() {
            return fatConsumed;
        }

        public List<Long> getDateLogs() {
            return dateLogs;
        }
}
