package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class TacharMarcadorDAO {

    // Método aislado para modificar (Update del CRUD) el estado de un marcador
    public boolean alternarTachado(int id_marcador, String username, boolean nuevoEstado) {
        
        String sql;
        if (nuevoEstado) {
            // Insertamos la relación si se tacha (y evitamos duplicados con IGNORE)
            sql = "INSERT IGNORE INTO usuario_marcador (username, id_marcador) VALUES (?, ?)";
        } else {
            // Borramos la relación si se destacha
            sql = "DELETE FROM usuario_marcador WHERE username = ? AND id_marcador = ?";
        }
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setInt(2, id_marcador);
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar el marcador para el usuario: " + e.getMessage());
            return false;
        }
    }
}