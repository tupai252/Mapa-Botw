package com.ejemplo.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BorrarUsuarioDAO {

    public boolean borrarUsuario(String username) {
        boolean exito = false;
        if (username != null && !username.trim().isEmpty()) {
            String sql = "DELETE FROM usuarios WHERE username = ?";

            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    exito = true;
                }

            } catch (SQLException e) {
                System.err.println("Error al borrar el usuario: " + e.getMessage());
            }
        }
        return exito;
    }
}
