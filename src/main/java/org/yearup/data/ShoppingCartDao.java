package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    ShoppingCart create(int userId, int productId);

    void update(int userId, int productId);

    void delete(int productId);
    // add additional method signatures here
}
