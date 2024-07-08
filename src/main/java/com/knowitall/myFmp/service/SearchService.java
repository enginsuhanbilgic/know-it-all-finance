package com.knowitall.myFmp.service;

import com.knowitall.myFmp.model.CryptoCurrency;
import com.knowitall.myFmp.repository.CryptoCurrencyRepository;
import com.mongodb.client.internal.Crypt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.data.mongodb.core.MongoTemplate;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final MongoTemplate mongoTemplate;
    private final CryptoCurrencyRepository cryptoCurrencyRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${fmp.api.key}")
    private String apiKey;

    @Autowired
    public SearchService(MongoTemplate mongoTemplate, CryptoCurrencyRepository cryptoCurrencyRepository, WebClient.Builder webClientBuilder, @Value("${fmp.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://financialmodelingprep.com/api/v3/").build();
        this.mongoTemplate = mongoTemplate;
        this.cryptoCurrencyRepository = cryptoCurrencyRepository;
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    public Mono<JsonNode> getAllMatching(String searchText) {

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
            Criteria.where("symbol").regex(".*"+Pattern.quote(searchText)+".*", "i"),
            Criteria.where("name").regex(".*"+Pattern.quote(searchText)+".*", "i")
        ));

        logger.info("Search text is {}", searchText);
        logger.info("Query: {}", query.toString());

        List<CryptoCurrency> allMatching = mongoTemplate.find(query, CryptoCurrency.class, "available-crypto-currencies");
        int searchReturnSize = allMatching.size();
        logger.info("Search return size: ", searchReturnSize);

        if(searchReturnSize==0){
            return Mono.just(objectMapper.createObjectNode().put("message", "The term you've entered nothing, try searching something else."));
        }

        if(searchReturnSize>40){
            return Mono.just(objectMapper.createObjectNode().put("message", "The term you've entered more than 40 results, try narrowing down your search."));
        }

        List<String> symbols = new ArrayList<>();
        for(CryptoCurrency c : allMatching){
            symbols.add(c.getSymbol());
        }

        Collections.sort(symbols, Comparator.comparingInt(String::length));

        JsonNode jsonResponse = objectMapper.valueToTree(symbols);
        return Mono.just(jsonResponse);
    }

    public Mono<JsonNode> searchCryptoCurrencies(String searchText) {
        String currentDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Construct the query
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("symbol").regex("^" + searchText + "$", "i"),
                Criteria.where("name").regex("^" + searchText + "$", "i")
        ));

        logger.info("Search text is {}", searchText);
        logger.info("Query: {}", query.toString());

        // Find the existing data
        CryptoCurrency existingData = mongoTemplate.findOne(query, CryptoCurrency.class, "available-crypto-currencies");
        if (existingData == null) {
            return Mono.just(objectMapper.createObjectNode().put("message", "Term you entered returned nothing"));
        } else {
            logger.info("Found data for symbol: {}", existingData.getSymbol());
            if (existingData.getHistorical() != null) {
                boolean hasCurrentDateData = existingData.getHistorical().stream()
                        .anyMatch(data -> data.getDate().equals(currentDateString));
                if (hasCurrentDateData) {
                    logger.info("Found current date data for symbol: {}", existingData.getSymbol());
                    CryptoCurrency modifiedData = new CryptoCurrency();
                    modifiedData.setId(existingData.getId());
                    modifiedData.setSymbol(existingData.getSymbol());
                    modifiedData.setName(existingData.getName());
                    modifiedData.setCurrency(existingData.getCurrency());
                    modifiedData.setStockExchange(existingData.getStockExchange());
                    modifiedData.setExchangeShortName(existingData.getExchangeShortName());
                    List<CryptoCurrency.HistoricalData> newHistoricalData = new ArrayList<>();
                    newHistoricalData.add(existingData.getHistorical().get(existingData.getHistorical().size()-1));
                    modifiedData.setHistorical(newHistoricalData);
                    return Mono.just(objectMapper.valueToTree(modifiedData));
                }
            } else {
                logger.info("No historical data found for symbol: {}", existingData.getSymbol());
            }
        }

        logger.info("Existing data is null or no current date data found");
        // If not found, fetch from API and save to database
        return getCurrentDayCryptoPrice(existingData.getSymbol())
                .flatMap(jsonNode -> {
                    logger.info("Fetching data from API for symbol: {}", searchText);
                    // Check if the API returned data
                    if (jsonNode.has("historical") && jsonNode.get("historical").size() > 0) {
                        // Map the API response to CryptoCurrency and save it to MongoDB
                        CryptoCurrency cryptoCurrency = mapToCryptoCurrency(jsonNode);
                        saveOrUpdateCryptoCurrency(cryptoCurrency);
                        return Mono.just(objectMapper.valueToTree(cryptoCurrency));
                    } else {
                        logger.info("No data found from API for {}", searchText);
                        return Mono.just(objectMapper.createObjectNode().put("message", "Term you entered returned nothing"));
                    }
                });
    }

    private Mono<JsonNode> getCurrentDayCryptoPrice(String symbol) {
        String currentDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return webClient.get()
                .uri("/historical-price-full/{symbol}?apikey={apiKey}&timeseries=1&from={date}&to={date}",
                        symbol, apiKey, currentDateString, currentDateString)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    private CryptoCurrency mapToCryptoCurrency(JsonNode jsonNode) {
        // Map the JSON response to CryptoCurrency class
        String symbol = jsonNode.get("symbol").asText();
        JsonNode historicalNode = jsonNode.get("historical").get(0);
        CryptoCurrency.HistoricalData historicalData = new CryptoCurrency.HistoricalData(
                historicalNode.get("date").asText(),
                historicalNode.get("open").asDouble(),
                historicalNode.get("high").asDouble(),
                historicalNode.get("low").asDouble(),
                historicalNode.get("close").asDouble(),
                historicalNode.get("adjClose").asDouble(),
                historicalNode.get("volume").asLong(),
                historicalNode.get("unadjustedVolume").asLong(),
                historicalNode.get("change").asDouble(),
                historicalNode.get("changePercent").asDouble(),
                historicalNode.get("vwap").asDouble(),
                historicalNode.get("label").asText(),
                historicalNode.get("changeOverTime").asDouble()
        );

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("symbol").regex("^" + symbol + "$", "i"),
                Criteria.where("name").regex("^" + symbol + "$", "i")
        ));

        CryptoCurrency cryptoCurrency = mongoTemplate.findOne(query, CryptoCurrency.class, "available-crypto-currencies");
        cryptoCurrency.setHistorical(List.of(historicalData));
        return cryptoCurrency;
    }

    private void saveOrUpdateCryptoCurrency(CryptoCurrency cryptoCurrency) {
        Query query = new Query();
        query.addCriteria(Criteria.where("symbol").is(cryptoCurrency.getSymbol()));

        Update update = new Update();
        update.push("historical", cryptoCurrency.getHistorical().get(0));

        mongoTemplate.upsert(query, update, CryptoCurrency.class, "available-crypto-currencies");
    }

}
