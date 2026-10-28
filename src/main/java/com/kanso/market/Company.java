package com.kanso.market;

import java.util.Arrays;
import java.util.Optional;

/** The four (fictional) companies the API knows. The slug is the URL part. */
public enum Company {
    SHIZUKA("shizuka", "Shizuka Ceramics", "SZK", "Plain stoneware", 1240),
    MOKUME("mokume", "Mokume Furniture", "MKM", "Untreated wood furniture", 3180),
    KINARI("kinari", "Kinari Textiles", "KNR", "Undyed linen", 860),
    SUMI("sumi", "Sumi Stationery", "SUM", "Ink and paper goods", 2050);

    private final String slug;
    private final String displayName;
    private final String symbol;
    private final String sells;
    private final double basePrice;

    Company(String slug, String displayName, String symbol, String sells, double basePrice) {
        this.slug = slug;
        this.displayName = displayName;
        this.symbol = symbol;
        this.sells = sells;
        this.basePrice = basePrice;
    }

    public static Optional<Company> fromSlug(String slug) {
        return Arrays.stream(values()).filter(c -> c.slug.equalsIgnoreCase(slug)).findFirst();
    }

    public String slug() { return slug; }
    public String displayName() { return displayName; }
    public String symbol() { return symbol; }
    public String sells() { return sells; }
    public double basePrice() { return basePrice; }
}
