package com.knowitall.myFmp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
