package com.kanso.market;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Served under /api because the platform routes the /api path to this app (the prefix is not stripped).
 */
@RestController
@RequestMapping("/api")
public class MarketController {

    static final int DEFAULT_COUNT = 60;
    static final int MAX_COUNT = 500;

    private final PriceGenerator prices;
    private final String version;

    public MarketController(PriceGenerator prices, @Value("${app.version:dev}") String version) {
        this.prices = prices;
        this.version = version;
    }

    /** Health check for Kubernetes, and the running version. */
    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("status", "ok", "version", version);
    }

    /** The companies the API knows, so callers can find the slugs for the stocks call. */
    @GetMapping("/companies")
    public List<CompanyInfo> companies() {
        return Arrays.stream(Company.values())
                .map(c -> new CompanyInfo(c.slug(), c.displayName(), c.symbol(), c.sells()))
                .toList();
    }

    /**
     * A series of prices for one company, new on every call. The frontend fetches it once and
     * cycles through it, so it doesn't need to call every second.
     */
    @GetMapping("/company/{companyName}/stocks")
    public StockSeries stocks(@PathVariable String companyName,
                              @RequestParam(defaultValue = "" + DEFAULT_COUNT) int count) {
        Company company = Company.fromSlug(companyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown company: " + companyName));
        if (count < 1 || count > MAX_COUNT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "count must be 1.." + MAX_COUNT);
        }
        return new StockSeries(company.slug(), company.displayName(), company.symbol(), company.sells(),
                "JPY", prices.walk(company.basePrice(), count));
    }

    public record CompanyInfo(String slug, String name, String symbol, String sells) {
    }

    public record StockSeries(String company, String name, String symbol, String sells,
                              String currency, double[] prices) {
    }
}
