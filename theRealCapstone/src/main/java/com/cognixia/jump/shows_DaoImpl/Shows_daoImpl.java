package com.cognixia.jump.shows_DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cognixia.jump.connection.ConnectionManager;
import com.cognixia.jump.dao.Dao;

public class Shows_daoImpl implements Dao<Shows> {
    private Connection connection;

    public Shows_daoImpl() {
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
    public Shows getById(int id) throws SQLException {
        String query = "SELECT * FROM shows WHERE show_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int showId = rs.getInt("show_id");
            String showName = rs.getString("show_name");
            String genre = rs.getString("genre");
            String director = rs.getString("director");
            String status = rs.getString("status");
            int currentEpisode = rs.getInt("current_episode");
            int totalEpisodes = rs.getInt("total_episodes");

            return new Shows(showId, showName, genre, director, status, currentEpisode, totalEpisodes);
        }

        return null;
    }

    @Override
    public List<Shows> getAll() throws SQLException {
        List<Shows> showsList = new ArrayList<>();

        String query = "SELECT show_id, show_name, genre, director, status, current_episode, total_episodes FROM shows";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int showId = rs.getInt("show_id");
                String showName = rs.getString("show_name");
                String genre = rs.getString("genre");
                String director = rs.getString("director");
                String status = rs.getString("status");
                int currentEpisode = rs.getInt("current_episode");
                int totalEpisodes = rs.getInt("total_episodes");

                Shows show = new Shows(showId, showName, genre, director, status, currentEpisode, totalEpisodes);
                showsList.add(show);
            }
        }

        return showsList;
    }

    @Override
    public boolean create(Shows show) throws SQLException {
        String sql = "INSERT INTO shows (show_name, genre, director, status, current_episode, total_episodes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, show.getShowName());
            pstmt.setString(2, show.getGenre());
            pstmt.setString(3, show.getDirector());
            pstmt.setString(4, show.getStatus());
            pstmt.setInt(5, show.getCurrentEpisode());
            pstmt.setInt(6, show.getTotalEpisodes());
            return pstmt.executeUpdate() > 0;
        }
    }
    

@Override
public boolean update(Shows show) throws SQLException {
    String sql = "UPDATE shows SET show_name = ?, genre = ?, director = ?, status = ?, current_episode = ?, total_episodes = ? WHERE show_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, show.getShowName());
        pstmt.setString(2, show.getGenre());
        pstmt.setString(3, show.getDirector());
        pstmt.setString(4, show.getStatus());
        pstmt.setInt(5, show.getCurrentEpisode());
        pstmt.setInt(6, show.getTotalEpisodes());
        pstmt.setInt(7, show.getShowId());
        return pstmt.executeUpdate() > 0;
    }
}


    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM shows WHERE show_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Method to update the rating of a show by a user
    public boolean updateShowRating(int showId, int userId, double rating) throws SQLException {
        String updateRatingQuery = "INSERT INTO users_shows (user_id, show_id, user_rating) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE user_rating = VALUES(user_rating)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateRatingQuery)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, showId);
            pstmt.setDouble(3, rating);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Method to calculate the average rating of a show
    public double getAverageRating(int showId) throws SQLException {
        String averageRatingQuery = "SELECT AVG(user_rating) as avg_rating FROM users_shows WHERE show_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(averageRatingQuery)) {
            pstmt.setInt(1, showId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }

    // Method to update the current episode of a show for a user
    @Override
    public boolean updateCurrentEpisode(int showId, int userId, int currentEpisode) throws SQLException {
        String query = "UPDATE users_shows SET current_episode = ? WHERE show_id = ? AND user_id = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentEpisode);
            pstmt.setInt(2, showId);
            pstmt.setInt(3, userId);
    
            System.out.println("Debug: Executing query: " + pstmt.toString());
    
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Debug: rowsAffected = " + rowsAffected);
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    

    public List<Shows> getAllByUserId(int userId) throws SQLException {
        List<Shows> showsList = new ArrayList<>();
    
        String query = "SELECT s.show_id, s.show_name, s.genre, s.director, s.status, s.current_episode, s.total_episodes " +
                       "FROM shows s " +
                       "JOIN users_shows us ON s.show_id = us.show_id " +
                       "WHERE us.user_id = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
    
            while (rs.next()) {
                int showId = rs.getInt("show_id");
                String showName = rs.getString("show_name");
                String genre = rs.getString("genre");
                String director = rs.getString("director");
                String status = rs.getString("status");
                int currentEpisode = rs.getInt("current_episode");
                int totalEpisodes = rs.getInt("total_episodes");
    
                Shows show = new Shows(showId, showName, genre, director, status, currentEpisode, totalEpisodes);
                showsList.add(show);
            }
        }
    
        return showsList;
    }
    
    
    public List<Shows> getByStatus(String status) throws SQLException {
        List<Shows> showsList = new ArrayList<>();
    
        String query = "SELECT show_id, show_name, genre, director, status, current_episode, total_episodes FROM shows WHERE status = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
    
            while (rs.next()) {
                int showId = rs.getInt("show_id");
                String showName = rs.getString("show_name");
                String genre = rs.getString("genre");
                String director = rs.getString("director");
                String statusResult = rs.getString("status");
                int currentEpisode = rs.getInt("current_episode");
                int totalEpisodes = rs.getInt("total_episodes");
    
                Shows show = new Shows(showId, showName, genre, director, statusResult, currentEpisode, totalEpisodes);
                showsList.add(show);
            }
        }
    
        return showsList;
    }

    @Override
    public boolean addRating(int showId, double rating) {
        String query = "INSERT INTO shows_ratings (show_id, rating) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, showId);
            pstmt.setDouble(2, rating);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Shows> getAllShowsWithDetails() throws SQLException {
        List<Shows> showsList = new ArrayList<>();
        
        String query = "SELECT s.show_id, s.show_name, s.genre, s.director, s.status, s.total_episodes, " +
                       "COALESCE(us.current_episode, s.current_episode) AS current_episode " +
                       "FROM shows s " +
                       "LEFT JOIN users_shows us ON s.show_id = us.show_id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int showId = rs.getInt("show_id");
                String showName = rs.getString("show_name");
                String genre = rs.getString("genre");
                String director = rs.getString("director");
                String status = rs.getString("status");
                int currentEpisode = rs.getInt("current_episode");
                int totalEpisodes = rs.getInt("total_episodes");
                
                Shows show = new Shows(showId, showName, genre, director, status, currentEpisode, totalEpisodes);
                showsList.add(show);
            }
        }
        
        return showsList;
    }

    public List<Shows> getAllShowsWithRatings() throws SQLException {
        List<Shows> showsList = new ArrayList<>();
    
        String query = "SELECT s.show_id, s.show_name, s.genre, s.director, s.status, s.current_episode, s.total_episodes, " +
                       "(SELECT AVG(us.user_rating) FROM users_shows us WHERE us.show_id = s.show_id) AS average_rating " +
                       "FROM shows s";
    
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                int showId = rs.getInt("show_id");
                String showName = rs.getString("show_name");
                String genre = rs.getString("genre");
                String director = rs.getString("director");
                String status = rs.getString("status");
                int currentEpisode = rs.getInt("current_episode");
                int totalEpisodes = rs.getInt("total_episodes");
                double averageRating = rs.getDouble("average_rating");
    
                Shows show = new Shows(showId, showName, genre, director, status, currentEpisode, totalEpisodes);
                show.setAverageRating(averageRating);
                showsList.add(show);
            }
        }
    
        return showsList;
    }
    
}

