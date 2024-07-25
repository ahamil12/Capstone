package com.cognixia.jump.user_DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cognixia.jump.connection.ConnectionManager;
import com.cognixia.jump.dao.Dao;
import com.cognixia.jump.shows_DaoImpl.Shows;

public class UserDaoImpl implements Dao<User> {

    private Connection connection;

    public UserDaoImpl() {
        try {
            connection = ConnectionManager.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void establishConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            connection = ConnectionManager.getConnection();
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("user_id");
            String name = rs.getString("name");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            String role = rs.getString("role");

            return new User(userId, name, username, password, email, role);
        }

        return null;
    }

    @Override
    public boolean create(User user) throws SQLException {
        String query = "INSERT INTO users (name, username, password, email, role) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getUsername());
        pstmt.setString(3, user.getPassword());
        pstmt.setString(4, user.getEmail());
        pstmt.setString(5, user.getRole());

        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String query = "UPDATE users SET name = ?, username = ?, password = ?, email = ?, role = ? WHERE user_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getUsername());
        pstmt.setString(3, user.getPassword());
        pstmt.setString(4, user.getEmail());
        pstmt.setString(5, user.getRole());
        pstmt.setInt(6, user.getId());

        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public List<User> getAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public boolean updateShowRating(int showId, int userId, double rating) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateShowRating'");
    }

    @Override
    public boolean updateCurrentEpisode(int showId, int userId, int currentEpisode) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCurrentEpisode'");
    }

    @Override
    public List<User> getAllByUserId(int userId) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    @Override
    public boolean addRating(int showId, double rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRating'");
    }

    @Override
    public double getAverageRating(int showId) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAverageRating'");
    }

    @Override
    public List<Shows> getByStatus(String status) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByStatus'");
    }
}
