package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class TacharMarcadorDAO {

    // Método aislado para modificar (Update del CRUD) el estado de un marcador
    public boolean alternarTachado(int idMarcador, boolean nuevoEstado) {
        
        // Sentencia SQL para actualizar (U del CRUD)
        String sql = "UPDATE marcadores SET tachado = ? WHERE id_marcador = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Inyectamos los valores: el nuevo estado (true/false) y el ID
            pstmt.setBoolean(1, nuevoEstado);
            pstmt.setInt(2, idMarcador);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar el marcador: " + e.getMessage());
            return false;
        }
    }
}