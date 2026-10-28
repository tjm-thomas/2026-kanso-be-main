package com.kanso.market;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

/**
 * Random prices that move like a stock: each step is a small change from the previous one
 * (a random walk), not unrelated noise. New numbers on every call; nothing is stored.
 */
@Component
public class PriceGenerator {

    /** Largest move per step, as a fraction of the price (1.5%). */
    private static final double MAX_STEP = 0.015;

    public double[] walk(double basePrice, int count) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double[] prices = new double[count];
        // Start somewhere within 10% of the base price
        double price = basePrice * (0.9 + random.nextDouble() * 0.2);
        for (int i = 0; i < count; i++) {
            price *= 1 + (random.nextDouble() * 2 - 1) * MAX_STEP;
            prices[i] = Math.round(price * 100) / 100.0;
        }
        return prices;
    }
}
