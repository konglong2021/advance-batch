package com.example.advbatch.first;

import java.util.List;

public class DailyData {
    private final String date;
    private final List<Double> measurements;

    public DailyData(String date, List<Double> measurements) {
        this.date = date;
        this.measurements = measurements;
    }

    public String getDate() {
        return date;
    }

    public List<Double> getMeasurements() {
        return measurements;
    }
}
