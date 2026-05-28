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
    public List<Marcador> obtenerTodosLosMarcadores() {
        // Inicializamos la lista vacía donde guardaremos los datos
        List<Marcador> listaMarcadores = new ArrayList<>();

        String sql = "SELECT id_marcador, nombre, categoria, coord_x, coord_y, tachado FROM marcadores";

        // Establecemos la conexión usando el bloque try-with-resources
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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

        } catch (SQLException e) {
            // Captura de errores en consola por si la consulta falla
            System.err.println("Error al leer los marcadores de la base de datos: " + e.getMessage());
        }

        // Devolvemos la lista (si hubo error o está vacía, devolverá una lista con 0 elementos)
        return listaMarcadores;
    }
}