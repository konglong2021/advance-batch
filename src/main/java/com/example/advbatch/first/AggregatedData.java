package com.example.advbatch.first;

import com.thoughtworks.xstream.security.ExplicitTypePermission;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

public class AggregatedData {
    private final String date;
    private final double min;
    private final double avg;
    private final double max;

    public AggregatedData(String date, double min, double avg, double max) {
        this.date = date;
        this.min = min;
        this.avg = avg;
        this.max = max;
    }

    public static final String ITEM_ROOT_ELEMENT_NAME = "daily-data";

    // Static method which returns XML marshaller for the record
    public static XStreamMarshaller getMarshaller() {
        XStreamMarshaller marshaller = new XStreamMarshaller();

        Map<String, Class> aliases = new HashMap<>();
        aliases.put(ITEM_ROOT_ELEMENT_NAME, AggregatedData.class);
        aliases.put("date", String.class);
        aliases.put("min", Double.class);
        aliases.put("avg", Double.class);
        aliases.put("max", Double.class);

        ExplicitTypePermission typePermission = new ExplicitTypePermission(new Class[] { AggregatedData.class });

        marshaller.setAliases(aliases);
        marshaller.setTypePermissions(typePermission);

        return marshaller;
    }

    public String getDate() {
        return date;
    }

    public double getMin() {
        return min;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }
}
