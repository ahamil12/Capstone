package com.cognixia.jump;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.cognixia.jump.connection.ConnectionManager;
import com.cognixia.jump.dao.Dao;
import com.cognixia.jump.shows_DaoImpl.Shows;
import com.cognixia.jump.shows_DaoImpl.Shows_daoImpl;
import com.cognixia.jump.user_DaoImpl.User;
import com.cognixia.jump.user_DaoImpl.UserDaoImpl;

public class Menu {
    private Scanner sc;
    private Dao<Shows> showsDao;
    private Dao<User> userDao;
    private User loggedInUser;
    private Connection connection;

    public Menu() {
        sc = new Scanner(System.in);
        showsDao = new Shows_daoImpl();
        userDao = new UserDaoImpl();
    }

    public void establishConnections() throws ClassNotFoundException, SQLException {
        showsDao.establishConnection();
        userDao.establishConnection();
        connection = ConnectionManager.getConnection();
        checkConnection();
    }

    private void checkConnection() {
        if (connection != null) {
            System.out.println("Connection to database established successfully.");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }

    public void close() throws SQLException {
        if (showsDao != null) {
            showsDao.closeConnection();
        }
        if (userDao != null) {
            userDao.closeConnection();
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }

    public void run() {
        boolean applicationExit = false;
    
        while (!applicationExit) {
            try {
                establishConnections();
                showInitialMenu();
                close();
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Failed to connect to the database.");
                e.printStackTrace();
            } finally {
                try {
                    close();
                } catch (SQLException e) {
                    System.out.println("Failed to close the connection.");
                    e.printStackTrace();
                }
            }
                applicationExit = true;
            
        }
    }
    

    private void showInitialMenu() {
        boolean exit = false;
    
        while (!exit) {
            System.out.println("Do you have an account?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.println("3. Exit");
    
            try {
                int choice = sc.nextInt();
                sc.nextLine();
    
                switch (choice) {
                    case 1:
                        loggedInUser = null; // Reset the loggedInUser variable
                        login();
                        if (loggedInUser != null) {
                            mainMenu();
                        }
                        break;
                    case 2:
                        createNewAccount();
                        break;
                    case 3:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1, 2, or 3.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
    }
    
    

    public void login() {
        while (loggedInUser == null) {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            try {
                loggedInUser = authenticateUser(username, password);
                if (loggedInUser == null) {
                    System.out.println("Invalid username or password. Please try again.");
                    System.out.println("1. Retry");
                    System.out.println("2. Back");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 2) {
                        return;
                    }
                } else {
                    System.out.println("Login successful. Welcome, " + loggedInUser.getName() + "!");
                }
            } catch (SQLException e) {
                System.out.println("ERROR: Please try again.");
                e.printStackTrace();
            }
        }
    }

    private User authenticateUser(String username, String password) throws SQLException {
        User user = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT user_id, name, username, password, email, role FROM users WHERE username = ? AND password = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
    
            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role"); // Retrieve role
                user = new User(user_id, name, username, password, email, role); // Include role
            } else {
                System.out.println("Debug: No matching user found in database.");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return user;
    }

    private void mainMenu() {
        boolean exit = false;
    
        while (!exit) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. List all shows and details");
            System.out.println("2. List shows by status");
            System.out.println("3. Update show status");
            System.out.println("4. Update current episode");
            System.out.println("5. Show current episode");
            System.out.println("6. Rate a show");
            System.out.println("7. List show ratings");
    
            if ("admin".equals(loggedInUser.getRole())) {
                System.out.println("8. Add a show");
                System.out.println("9. Edit a show");
                System.out.println("10. Remove a show");
            }
    
            System.out.println("11. Logout");    
            try {
                int input = sc.nextInt();
                sc.nextLine(); // Consume the newline character
    
                switch (input) {
                    case 1:
                        getAllShows();
                        break;
                    case 2:
                        listShowsByStatus();
                        break;
                    case 3:
                        updateShowStatus();
                        break;
                    case 4:
                        updateCurrentEpisode();
                        break;
                    case 5:
                        showCurrentEpisode();
                        break;
                    case 6:
                        rateShow();
                        break;
                    case 7:
                        listShowRatings();
                        break;

                    case 8:
                        if ("admin".equals(loggedInUser.getRole())) addShow();
                        break;
                    case 9:
                        if ("admin".equals(loggedInUser.getRole())) editShow(); 
                        break;
                    case 10:
                        if ("admin".equals(loggedInUser.getRole())) removeShow();
                        break;
                    case 11:
                        loggedInUser = null; 
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 12.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); 
            }
        }
    }
    
    
    
    private void addShow() {
        System.out.print("Enter show name: ");
        String name = sc.nextLine();
        System.out.print("Enter genre: ");
        String genre = sc.nextLine();
        System.out.print("Enter director: ");
        String director = sc.nextLine();
        System.out.print("Enter status: ");
        String status = sc.nextLine();
        System.out.print("Enter current episode: ");
        int currentEpisode = sc.nextInt();
        System.out.print("Enter total episodes: ");
        int totalEpisodes = sc.nextInt();
        sc.nextLine(); 
    
        Shows newShow = new Shows(0, name, genre, director, status, currentEpisode, totalEpisodes);
        try {
            boolean success = showsDao.create(newShow);
            if (success) {
                System.out.println("Show added successfully.");
            } else {
                System.out.println("Failed to add show.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add show.");
            e.printStackTrace();
        }
    }
    

    

    private void removeShow() {
        System.out.print("Enter show ID to remove: ");
        int showId = sc.nextInt();
        sc.nextLine(); 

        try {
            showsDao.delete(showId);
            System.out.println("Show removed successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to remove show.");
            e.printStackTrace();
        }
    }

    private void getAllShows() {
        try {
            List<Shows> showsList = showsDao.getAll();
            if (showsList.isEmpty()) {
                System.out.println("No shows available.");
            } else {
                System.out.println("List of all shows:");
                for (Shows show : showsList) {
                    System.out.println("ID: " + show.getShowId() + ", Name: " + show.getShowName() + ", Genre: " + show.getGenre() + 
                                       ", Director: " + show.getDirector() + ", Status: " + show.getStatus() + 
                                       ", Total Episodes: " + show.getTotalEpisodes());
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve shows from the database.");
            e.printStackTrace();
        }
    }
    

    private void listShowsByStatus() {
        System.out.println("Select status:");
        System.out.println("1. Currently Watching");
        System.out.println("2. Formerly Watched");
        System.out.println("3. Watch in the Future");

        try {
            int choice = sc.nextInt();
            sc.nextLine(); 
            String status = null;

            switch (choice) {
                case 1:
                    status = "Currently Watching";
                    break;
                case 2:
                    status = "Formerly Watched";
                    break;
                case 3:
                    status = "Watch in the Future";
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
                    return;
            }

            listShowsByStatus(status);
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); 
        }
    }

    private void listShowsByStatus(String status) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM shows WHERE status = ?");
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                if (!hasResults) {
                    System.out.println("Shows with status: " + status);
                    hasResults = true;
                }
                int show_id = rs.getInt("show_id");
                String show_name = rs.getString("show_name");
                String genre = rs.getString("genre");
                String director = rs.getString("director");
                int current_episode = rs.getInt("current_episode");
                int total_episode = rs.getInt("total_episodes");

                System.out.println("ID: " + show_id + ", Name: " + show_name + ", Genre: " + genre + ", Director: " + director + ", Status: " + status + ", Total Episodes: " + total_episode + ", Current Episodes: " + current_episode);
            }

            if (!hasResults) {
                System.out.println("No shows found with status: " + status);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to retrieve shows by status.");
            e.printStackTrace();
        }
    }
    private void updateCurrentEpisode() {
        try {
            System.out.print("Enter show ID: ");
            int showId = sc.nextInt();
            System.out.print("Enter the new current episode: ");
            int currentEpisode = sc.nextInt();
            sc.nextLine(); 
    
            System.out.println("showId = " + showId + ", currentEpisode = " + currentEpisode + ", userId = " + loggedInUser.getId());
    
            
            ensureUserShowRecordExists(showId, loggedInUser.getId());
    
            boolean success = showsDao.updateCurrentEpisode(showId, loggedInUser.getId(), currentEpisode);
            if (success) {
                System.out.println("Current episode updated successfully.");
            } else {
                System.out.println("Failed to update current episode.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); // Clear the invalid input
            e.printStackTrace();
        }
    }
    
    private void ensureUserShowRecordExists(int showId, int userId) throws SQLException {
        String checkQuery = "SELECT * FROM users_shows WHERE show_id = ? AND user_id = ?";
        String insertQuery = "INSERT INTO users_shows (user_id, show_id, current_episode, user_rating) VALUES (?, ?, 0, 0.0)";
    
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, showId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();
    
            if (!rs.next()) {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, showId);
                    insertStmt.executeUpdate();
                    System.out.println("Debug: Inserted new record into users_shows.");
                }
            } else {
                System.out.println("Debug: Record already exists in users_shows.");
            }
        }
    }
    
    

    private void showCurrentEpisode() {
        try {
            List<Shows> showsList = showsDao.getAll();
            if (showsList.isEmpty()) {
                System.out.println("No shows available.");
                return;
            }
    
            System.out.println("Select a show to view the current episode:");
            for (int i = 0; i < showsList.size(); i++) {
                Shows show = showsList.get(i);
                System.out.println((i + 1) + ". " + show.getShowName());
            }
    
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline
    
            if (choice < 1 || choice > showsList.size()) {
                System.out.println("Invalid choice. Please select a valid show number.");
                return;
            }
    
            Shows selectedShow = showsList.get(choice - 1);
            showCurrentEpisodeForShow(selectedShow.getShowId());
        } catch (SQLException e) {
            System.out.println("Failed to retrieve shows from the database.");
            e.printStackTrace();
        }
    }
    
    private void showCurrentEpisodeForShow(int showId) {
        try {
            String query = "SELECT COALESCE(us.current_episode, s.current_episode) AS current_episode " +
                           "FROM shows s " +
                           "LEFT JOIN users_shows us ON s.show_id = us.show_id AND us.user_id = ? " +
                           "WHERE s.show_id = ?";
    
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, loggedInUser.getId());
                pstmt.setInt(2, showId);
                ResultSet rs = pstmt.executeQuery();
    
                if (rs.next()) {
                    int currentEpisode = rs.getInt("current_episode");
                    System.out.println("Current episode for show ID " + showId + ": " + currentEpisode);
                } else {
                    System.out.println("Show not found or no episodes available.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve the current episode.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); // Clear the invalid input
        }
    }
    
    
    


    private void rateShow() {
        System.out.print("Enter the show ID you want to rate: ");
        int showId = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.print("Enter your rating (0.0 to 5.0): ");
        double rating = sc.nextDouble();
        sc.nextLine(); // Consume newline

        try {
            if (showsDao.updateShowRating(showId, loggedInUser.getId(), rating)) {
                System.out.println("Rating updated successfully.");
            } else {
                System.out.println("Failed to update rating. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating rating.");
            e.printStackTrace();
        }
    }

    private void updateShowStatus() {
        try {
            List<Shows> showsList = showsDao.getAll();
            if (showsList.isEmpty()) {
                System.out.println("No shows available to update.");
                return;
            }
    
            System.out.println("Select the show to update status:");
            for (int i = 0; i < showsList.size(); i++) {
                Shows show = showsList.get(i);
                System.out.println((i + 1) + ". " + show.getShowName());
            }
    
            int choice = sc.nextInt();
            sc.nextLine();
    
            if (choice < 1 || choice > showsList.size()) {
                System.out.println("Invalid choice. Please select a valid show number.");
                return;
            }
    
            Shows selectedShow = showsList.get(choice - 1);
    
            System.out.println("Select new status:");
            System.out.println("1. Currently Watching");
            System.out.println("2. Formerly Watched");
            System.out.println("3. Watch in the Future");
    
            int statusChoice = sc.nextInt();
            sc.nextLine();
            String status = null;
    
            switch (statusChoice) {
                case 1:
                    status = "Currently Watching";
                    break;
                case 2:
                    status = "Formerly Watched";
                    break;
                case 3:
                    status = "Watch in the Future";
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
                    return;
            }
    
            boolean success = updateShowStatusInDB(selectedShow.getShowId(), status);
            if (success) {
                System.out.println("Show status updated successfully.");
            } else {
                System.out.println("Failed to update show status. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve shows from the database.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); 
        }
    }
    
    private boolean updateShowStatusInDB(int showId, String status) {
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("UPDATE shows SET status = ? WHERE show_id = ?");
            pstmt.setString(1, status);
            pstmt.setInt(2, showId);
    
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update show status in the database.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

    private void createNewAccount() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter a username: ");
        String username = sc.nextLine();
        System.out.print("Enter a password: ");
        String password = sc.nextLine();
        System.out.print("Enter your email: ");
        String email = sc.nextLine();
        System.out.print("Enter your role (user/admin): "); // Prompt for role
        String role = sc.nextLine(); // Declare and initialize the role variable
    
        try {
            if (isUsernameTaken(username)) {
                System.out.println("Username is already taken. Please choose a different username.");
                return;
            }
    
            if (isEmailTaken(email)) {
                System.out.println("Email is already taken. Please use a different email.");
                return;
            }
    
            User newUser = new User(0, name, username, password, email, role); // Use the role variable
            boolean success = userDao.create(newUser);
            System.out.println("Debug: User creation status: " + success);
            if (success) {
                System.out.println("Account created successfully. You can now log in.");
            } else {
                System.out.println("Failed to create account. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while creating the account. Please try again.");
            e.printStackTrace();
        }
    }
    

    private boolean isUsernameTaken(String username) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return false;
    }

    private boolean isEmailTaken(String email) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?");
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return false;
    }

    private void addRating() {
        System.out.print("Enter show ID to rate: ");
        int showId = sc.nextInt();
        sc.nextLine(); 

        System.out.print("Enter your rating (0.0 to 5.0): ");
        double rating = sc.nextDouble();
        sc.nextLine(); // Consume newline
        boolean success = showsDao.addRating(showId, rating);
             if (success) {
        System.out.println("Rating added successfully.");
        }      else {
        System.out.println("Failed to add rating.");
        }
    }

    private void showAverageRating() {
        System.out.print("Enter the show ID to view the average rating: ");
        int showId = sc.nextInt();
        sc.nextLine(); 

        try {
            double averageRating = showsDao.getAverageRating(showId);
            System.out.println("The average rating for the show is: " + averageRating);
        } catch (SQLException e) {
            System.out.println("Error retrieving average rating.");
            e.printStackTrace();
        }
    }

    private void listShowRatings() {
        try {
            Shows_daoImpl showsDaoImpl = (Shows_daoImpl) showsDao;
            List<Shows> showsList = showsDaoImpl.getAllShowsWithRatings();
            if (showsList.isEmpty()) {
                System.out.println("No shows available.");
            } else {
                System.out.println("List of all shows and their ratings:");
                for (Shows show : showsList) {
                    double averageRating = show.getAverageRating();
                    System.out.println("ID: " + show.getShowId() + ", Name: " + show.getShowName() + ", Average Rating: " + averageRating);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve show ratings from the database.");
            e.printStackTrace();
        }
    }
    
    private void editShow() {
        try {
            System.out.print("Enter show ID to edit: ");
            int showId = sc.nextInt();
            sc.nextLine(); // Consume the newline character
    
            Shows show = showsDao.getById(showId);
            if (show == null) {
                System.out.println("Show not found.");
                return;
            }
    
            System.out.print("Enter new show name (" + show.getShowName() + "): ");
            String name = sc.nextLine();
            if (!name.trim().isEmpty()) {
                show.setShowName(name);
            }
    
            System.out.print("Enter new genre (" + show.getGenre() + "): ");
            String genre = sc.nextLine();
            if (!genre.trim().isEmpty()) {
                show.setGenre(genre);
            }
    
            System.out.print("Enter new director (" + show.getDirector() + "): ");
            String director = sc.nextLine();
            if (!director.trim().isEmpty()) {
                show.setDirector(director);
            }
    
            System.out.print("Enter new status (" + show.getStatus() + "): ");
            String status = sc.nextLine();
            if (!status.trim().isEmpty()) {
                show.setStatus(status);
            }
    
            System.out.print("Enter new current episode (" + show.getCurrentEpisode() + "): ");
            int currentEpisode = sc.nextInt();
            if (currentEpisode > 0) {
                show.setCurrentEpisode(currentEpisode);
            }
    
            System.out.print("Enter new total episodes (" + show.getTotalEpisodes() + "): ");
            int totalEpisodes = sc.nextInt();
            if (totalEpisodes > 0) {
                show.setTotalEpisodes(totalEpisodes);
            }
            sc.nextLine(); // Consume the newline character
    
            boolean success = showsDao.update(show);
            if (success) {
                System.out.println("Show updated successfully.");
            } else {
                System.out.println("Failed to update show.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to edit show.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid details.");
            sc.nextLine(); // Clear the invalid input
        }
    }
        
    

}
