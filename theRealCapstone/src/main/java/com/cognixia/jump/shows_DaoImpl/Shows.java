package com.cognixia.jump.shows_DaoImpl;

public class Shows {
    private int showId;
    private String showName;
    private String genre;
    private String director;
    private String status;
    private int episodes;
    private int currentEpisode;
    private int totalEpisodes;
    private double averageRating;

    public Shows(int showId, String showName, String genre, String director, String status, int currentEpisode, int totalEpisodes) {
        this.showId = showId;
        this.showName = showName;
        this.genre = genre;
        this.director = director;
        this.status = status;
        this.currentEpisode = currentEpisode;
        this.totalEpisodes = totalEpisodes; // Consistent naming
        this.averageRating = averageRating;
    }

    // Getters and setters
    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAverageRating() {
        return averageRating;
    }

}

