package com.example.advbatch.first;

import org.springframework.batch.item.ItemProcessor;

public class DataAnomalyProcessor implements ItemProcessor<AggregatedData, DataAnomaly> {
    private static final double THRESHOLD = 0.9;
    @Override
    public DataAnomaly process(AggregatedData item) throws Exception {
        if ((item.getMin() / item.getAvg()) < THRESHOLD) {
            return new DataAnomaly(item.getDate(), AnomalyType.MINIMUM, item.getMin());
        } else if ((item.getAvg() / item.getMax()) < THRESHOLD) {
            return new DataAnomaly(item.getDate(), AnomalyType.MAXIMUM, item.getMax());
        } else {
            // Convention is to return null to filter item out and not pass it to the writer
            return null;
        }
    }
}
