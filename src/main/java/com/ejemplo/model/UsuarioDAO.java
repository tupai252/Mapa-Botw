package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class UsuarioDAO {

    // Método aislado para insertar un usuario (Create del CRUD)
    public boolean registrarUsuario(Usuario usuario) {
        // La consulta SQL con '?' para evitar ataques de Inyección SQL
        String sql = "INSERT INTO usuarios (username, email, password_hash) VALUES (?, ?, ?)";

        // Usamos try-with-resources para que la conexión se cierre sola al terminar
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Sustituimos las interrogaciones por los datos reales del objeto Java
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getPasswordHash()); // Nota: En un proyecto real esto iría encriptado (Bcrypt)

            // Ejecutamos la consulta. Si devuelve más de 0 filas afectadas, se guardó bien
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            // Si el usuario o el email ya existen (por la regla UNIQUE), saltará este error
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            return false;
        }
    }
}