package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("cart")
@CrossOrigin
@PreAuthorize("hasRole('ROLE_USER')")
// convert this class to a REST controller
// only logged in users should have access to these actions
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;

    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }


    // each method in this controller requires a Principal object as a parameter
    @GetMapping("")
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingCartDao to get all items in the cart and return the cart
            var shoppingCart = shoppingCartDao.getByUserId(userId);

            if (shoppingCart == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return shoppingCart;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/products/{productId}")
    public ShoppingCartItem addToCart(@PathVariable int productId, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            var shoppingCart = shoppingCartDao.getByUserId(userId);

            if (!shoppingCart.contains(productId)) {
                Product product = productDao.getById(productId);

                return shoppingCartDao.create(userId, product);
            } else {
                ShoppingCartItem shoppingCartItem = shoppingCart.get(productId);

                shoppingCartDao.update(userId,shoppingCartItem);
                return shoppingCartItem;
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{productId}")
    public void update(@PathVariable int productId,@RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);
            if (shoppingCart.contains(productId)) {
                shoppingCartDao.update(userId, shoppingCartItem);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }
    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

    @DeleteMapping("")
    public void delete(Principal principal) {

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            var cart = shoppingCartDao.getByUserId(userId);

            if (cart == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            shoppingCartDao.delete(userId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
