package com.example.advbatch.first;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource("classpath:db.properties")
public class TemperatureJobConfig extends DefaultBatchConfiguration {

    @Value("classpath:input/HTE2NP.txt")
    private Resource rawDataFile;

    @Value("file:HTE2NP.xml")
    private WritableResource aggregatedDailyOutputXmlResource;

    @Value("file:HTE2NP-anomalies.csv")
    private WritableResource anomalyDataResource;

    @Qualifier("temperatureJob")
    @Bean
    public Job temperatureJob(JobRepository jobRepository,
                              @Qualifier("processRawDataStep") Step processRawDataStep,
                              @Qualifier("report1AnomaliesStep") Step report1AnomaliesStep)
    {
        return new JobBuilder("temperatureJob",jobRepository)
                .start(processRawDataStep)
                .next(report1AnomaliesStep)
                .build();
    }

    @Qualifier("processRawDataStep")
    @Bean
    public Step processRawDataStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("Process-RawData-Step", jobRepository)
               .<DailyData, AggregatedData> chunk(10,platformTransactionManager)
                .reader(new FlatFileItemReaderBuilder<DailyData>()
                       .name("rawDataFileReader")
                       .resource(rawDataFile)
                       .lineMapper(new TextLineMapper())
                        .build())
                .processor(new RawAggregatedDataProcessor())
                .writer(new StaxEventItemWriterBuilder<>()
                       .name("aggregatedDataFileWriter")
                        .marshaller(AggregatedData.getMarshaller())
                        .resource(aggregatedDailyOutputXmlResource)
                        .rootTagName("data")
                        .overwriteOutput(true)
                        .build())
               .build();
    }

    @Qualifier("report1AnomaliesStep")
    @Bean
    public Step report1AnomaliesStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("report1AnomaliesStep",jobRepository)
                .<AggregatedData,DataAnomaly>chunk(100,platformTransactionManager)
                .reader(new StaxEventItemReaderBuilder<AggregatedData>()
                       .name("aggregatedDataFileReader")
                        .unmarshaller(AggregatedData.getMarshaller())
                        .resource(aggregatedDailyOutputXmlResource)
                        .addFragmentRootElements(AggregatedData.ITEM_ROOT_ELEMENT_NAME)
                        .build())
                .processor(new DataAnomalyProcessor())
                .writer(new FlatFileItemWriterBuilder<DataAnomaly>()
                       .name("anomalyReportWriter")
                       .resource(anomalyDataResource)
                       .delimited()
                       .delimiter(",")
                       .names(new String[]{"date","type","value"})
                       .build())
               .build();
    }
}
