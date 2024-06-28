package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        String sql = "SELECT p.*, quantity " +
                " FROM products AS p " +
                " JOIN shopping_cart AS s " +
                "   ON p.product_id = s.product_id " +
                " WHERE user_id = ?";

        try(
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setInt(1, userId);

            try(ResultSet row = statement.executeQuery())
            {
                while(row.next())
                {
                    Product product = MySqlProductDao.mapRow(row);

                    int quantity = row.getInt("quantity");

                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

                    shoppingCartItem.setProduct(product);
                    shoppingCartItem.setQuantity(quantity);

                    shoppingCart.add(shoppingCartItem);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }


@Override
    public void update(int userId, ShoppingCartItem shoppingCartItem) {
        String sql = "UPDATE shopping_cart" +
                " SET quantity = ? " +
                " WHERE user_id = ? " +
                " AND WHERE product_id = ?; ";


        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);

            shoppingCartItem.setQuantity(shoppingCartItem.getQuantity()+ 1);
            int quantity = shoppingCartItem.getQuantity();
            int productId = shoppingCartItem.getProductId();

            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int userId) {
        String sql = "DELETE FROM shopping_cart " +
                " WHERE user_Id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
@Override
    public ShoppingCartItem create(int userId, Product product) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

        shoppingCartItem.setProduct(product);
        int productId = shoppingCartItem.getProductId();

        String sql = "INSERT INTO shopping_cart(user_id, product_id) " +
                " VALUES (?, ?);";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return shoppingCartItem;
    }
}
