package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ejemplo.model.ConexionBD;

public class CrearMarcadorDAO {

    // Método aislado para insertar un nuevo marcador en el mapa
    public boolean insertarMarcador(Marcador marcador) {
        
        // Sentencia SQL preparada para evitar inyección
        String sql = "INSERT INTO marcadores (nombre, categoria, coord_x, coord_y) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Asignamos los valores del objeto Marcador a la consulta
            pstmt.setString(1, marcador.getNombre());
            pstmt.setString(2, marcador.getCategoria());
            pstmt.setFloat(3, marcador.getCoordX());
            pstmt.setFloat(4, marcador.getCoordY());
            
            // Ejecutamos y comprobamos si se insertó al menos 1 fila
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar el marcador: " + e.getMessage());
            return false;
        }
    }
}