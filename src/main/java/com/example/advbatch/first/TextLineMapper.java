package com.example.advbatch.first;

import org.springframework.batch.item.file.LineMapper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextLineMapper implements LineMapper<DailyData> {
    @Override
    public DailyData mapLine(String line, int lineNumber) throws Exception {
        String[] dateAndValue = line.split(":");

        return new DailyData(dateAndValue[0],
                Arrays.stream(dateAndValue[1].split(","))
                       .map(Double::parseDouble)
                        .collect(Collectors.toList()));
    }
}
