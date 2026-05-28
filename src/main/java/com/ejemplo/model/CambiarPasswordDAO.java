package com.ejemplo.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class CambiarPasswordDAO {

    public boolean cambiarPassword(String username, String nuevaPassword) {
        boolean exito = false;
        if (username != null && nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            String sql = "UPDATE usuarios SET password_hash = ? WHERE username = ?";

            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nuevaPassword);
                pstmt.setString(2, username);
                
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    exito = true;
                }

            } catch (SQLException e) {
                System.err.println("Error al cambiar contraseña: " + e.getMessage());
            }
        }
        return exito;
    }
}
