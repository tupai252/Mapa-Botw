package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Marcador;
import com.ejemplo.model.ConexionBD;

public class LeerMarcadoresDAO {

    // Método aislado para leer (Read del CRUD) todos los marcadores del mapa
    public List<Marcador> obtenerTodosLosMarcadores(String username) {
        // Inicializamos la lista vacía donde guardaremos los datos
        List<Marcador> listaMarcadores = new ArrayList<>();
        
        String sql;
        if (username != null && !username.isEmpty()) {
            sql = "SELECT m.id_marcador, m.nombre, m.categoria, m.coord_x, m.coord_y, " +
                  "(um.id_marcador IS NOT NULL) AS tachado " +
                  "FROM marcadores m " +
                  "LEFT JOIN usuario_marcador um ON m.id_marcador = um.id_marcador AND um.username = ?";
        } else {
            sql = "SELECT m.id_marcador, m.nombre, m.categoria, m.coord_x, m.coord_y, " +
                  "FALSE AS tachado " +
                  "FROM marcadores m";
        }

        // Establecemos la conexión usando el bloque try-with-resources
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            if (username != null && !username.isEmpty()) {
                pstmt.setString(1, username);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Recorremos el ResultSet fila por fila
                while (rs.next()) {
                    // Instanciamos el objeto de entidad mapeando los datos de la columna
                    Marcador m = new Marcador();
                    m.setIdMarcador(rs.getInt("id_marcador"));
                    m.setNombre(rs.getString("nombre"));
                    m.setCategoria(rs.getString("categoria"));
                    m.setCoordX(rs.getFloat("coord_x"));
                    m.setCoordY(rs.getFloat("coord_y"));
                    m.setTachado(rs.getBoolean("tachado"));

                    // Añadimos el marcador relleno a nuestra lista
                    listaMarcadores.add(m);
                }
            }

        } catch (SQLException e) {
            // Captura de errores en consola por si la consulta falla
            System.err.println("Error al leer los marcadores de la base de datos: " + e.getMessage());
        }

        // Devolvemos la lista (si hubo error o está vacía, devolverá una lista con 0 elementos)
        return listaMarcadores;
    }
}