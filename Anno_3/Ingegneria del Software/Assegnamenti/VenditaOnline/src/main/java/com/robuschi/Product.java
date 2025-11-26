package com.robuschi;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * The {@code Product} class represents a product in the "online" sales system.
 * It servers as the main object of the system, users can buy, return or add products.
 * The server keeps track of all the products.
 * A product has a name, price, and unique identifier.
 * @author Mattia Robuschi Caprara
 * @see Serializable
 **/
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final double price;
    private final int identifier;

    /**
     * Creates a new {@code Product} instance.
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

    /**
     * {@inheritDoc}
     * @return a formatted string containing all the attributes of the class
     */
    @Override
    public String toString() {
        return String.format("ID: %d | %s | €%.2f", identifier, name, price);
    }

    /**
     * {@inheritDoc}
     * @param o   the reference object with which to compare.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return identifier == product.identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}

