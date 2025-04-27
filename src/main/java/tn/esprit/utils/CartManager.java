package tn.esprit.utils;

import tn.esprit.models.Artwork;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<Artwork> cartItems = new ArrayList<>();

    public static void addToCart(Artwork artwork) {
        cartItems.add(artwork);
    }

    public static List<Artwork> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
    }
} 