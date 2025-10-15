package com.robuschi;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a product in the online sales system.
 * A product has a name, price, and unique identifier.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final double price;
    private final int identifier;

    /**
     * Creates a new Product instance.
     *
     * @param name the product name
     * @param price the product price
     * @param identifier the unique product identifier
     */
    public Product(String name, double price, int identifier) {
        this.name = name;
        this.price = price;
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | €%.2f", identifier, name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return identifier == product.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}

