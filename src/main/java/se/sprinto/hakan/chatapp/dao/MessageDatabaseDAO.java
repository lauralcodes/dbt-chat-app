package se.sprinto.hakan.chatapp.dao;


import se.sprinto.hakan.chatapp.DatabaseUtil;
import se.sprinto.hakan.chatapp.model.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseDAO implements MessageDAO {

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void saveMessage(Message message) {
        String sql = "INSERT INTO message (userId, text, timestamp) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, message.getUserId());
            stmt.setString(2, message.getText());
            stmt.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) {
        String sql = "SELECT id, text, timestamp FROM message WHERE userId = ?";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String text = rs.getString("text");

                    Timestamp ts = rs.getTimestamp("timestamp");
                    LocalDateTime timestamp = (ts != null)
                            ? ts.toLocalDateTime()
                            : null;

                    Message msg = new Message(userId, text, timestamp);
                    messages.add(msg);
                }

                return messages;
            }

        } catch (SQLException e) {
            // Don’t return null — predictable behavior matters
            throw new RuntimeException("Failed to load messages for userId " + userId, e);
        }

    }

    // valfritt: för att testa lättare
    public void seedTestData() {
        messages.add(new Message(1, "Hej!", LocalDateTime.now()));
        messages.add(new Message(2, "Hallå där!", LocalDateTime.now()));
    }
}

