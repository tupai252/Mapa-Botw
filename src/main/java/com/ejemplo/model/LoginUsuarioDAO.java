package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class LoginUsuarioDAO {

    // Método único de este modelo: Buscar un usuario en la BD (Read del CRUD)
    public Usuario buscarUsuarioPorCredenciales(String username, String password) {
        
        // Sentencia SQL para leer (SELECT)
        String sql = "SELECT id_usuario, username, email, rol FROM usuarios WHERE username = ? AND password_hash = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setRol(rs.getString("rol"));
                    return u; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario (Login): " + e.getMessage());
        }
        
        return null; 
    }
}