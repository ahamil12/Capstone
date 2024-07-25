package com.cognixia.jump.dao;

import java.sql.SQLException;
import java.util.List;

import com.cognixia.jump.shows_DaoImpl.Shows;

public interface Dao<T> {
    void establishConnection() throws ClassNotFoundException, SQLException;
    void closeConnection() throws SQLException;
    boolean create(T obj) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    boolean update(T obj) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean updateShowRating(int showId, int userId, double rating) throws SQLException;
    boolean updateCurrentEpisode(int showId, int userId, int currentEpisode) throws SQLException;
    List<T> getAllByUserId(int userId) throws SQLException;
    boolean addRating(int showId, double rating);
    double getAverageRating(int showId) throws SQLException;
    List<Shows> getByStatus(String status) throws SQLException;
}
