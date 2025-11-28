package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.DatabaseUtil;
import se.sprinto.hakan.chatapp.model.User;

import java.sql.*;

public class UserDatabaseDAO implements UserDAO {

    @Override
    public User register(User user) {
        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
                    return user;
                } else {
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or throw a custom exception
        }
    }

    @Override
    public User login(String username, String password) {
        String sql = "SELECT id, username, password FROM user WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    return user;
                } else {
                    return null;  // no match
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null; // or throw a custom exception
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or throw a custom exception
        }
    }

    // valfritt: för testsyfte
    public void seedUsers() {
        register(new User("test", "123"));
        register(new User("hakan", "password"));
    }
}

