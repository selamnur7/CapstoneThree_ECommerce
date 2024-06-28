package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    ShoppingCartItem create(int userId, Product product);

    void update(int userId, ShoppingCartItem shoppingCartItem);

    void delete(int userId);
    // add additional method signatures here
}
