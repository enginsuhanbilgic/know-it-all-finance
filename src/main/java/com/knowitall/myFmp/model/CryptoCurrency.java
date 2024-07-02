package com.knowitall.myFmp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "available-crypto-currencies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoCurrency {
    @Id
    private ObjectId id;
    private String symbol;
    private String name;
    private String currency;
    private String stockExchange;
    private String exchangeShortName;
    private List<HistoricalData> historical;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoricalData {
        private String date;
        private double open;
        private double high;
        private double low;
        private double close;
        private double adjClose;
        private long volume;
        private long unadjustedVolume;
        private double change;
        private double changePercent;
        private double vwap;
        private String label;
        private double changeOverTime;
    }
}
