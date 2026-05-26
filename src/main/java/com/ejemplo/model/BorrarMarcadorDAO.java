package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class BorrarMarcadorDAO {

    // Método aislado para eliminar (Delete del CRUD) un marcador por su ID
    public boolean eliminarMarcador(int idMarcador) {
        
        // Sentencia SQL preparada para borrar un registro específico
        String sql = "DELETE FROM marcadores WHERE id_marcador = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Inyectamos el ID que queremos borrar
            pstmt.setInt(1, idMarcador);
            
            // Si affectedRows es mayor que 0, es que el marcador existía y se borró
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al borrar el marcador: " + e.getMessage());
            return false;
        }
    }
}