package com.example.advbatch.first;

import org.springframework.batch.item.ItemProcessor;

public class RawAggregatedDataProcessor implements ItemProcessor<DailyData,AggregatedData> {
    @Override
    public AggregatedData process(DailyData item) throws Exception {
        double min = item.getMeasurements().get(0);
        double max = min;
        double sum = 0;

        for (double measurement : item.getMeasurements()) {
            min = Math.min(min, measurement);
            max = Math.max(max, measurement);
            sum += measurement;
        }

        double avg = sum / item.getMeasurements().size();

        return new AggregatedData(item.getDate(), convertToCelsius(min), convertToCelsius(avg), convertToCelsius(max));
    }
    private static double convertToCelsius(double fahT) {
        return (5 * (fahT - 32)) / 9;
    }
}
