package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Marcador;
import model.LeerMarcadoresDAO; // Importamos el modelo aislado de lectura

import java.io.IOException;
import java.util.List;

// Mapeo de la URL. Tu JavaScript llamará a esta dirección mediante un fetch()
@WebServlet("/LeerMarcadoresController")
public class LeerMarcadoresController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Definimos las cabeceras obligatorias para el intercambio de datos en JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 1b. Extraemos el nombre de usuario de los parámetros (si existe)
        String username = request.getParameter("username");

        // 2. Invocamos al modelo aislado para traernos los datos reales de MySQL
        LeerMarcadoresDAO leerDAO = new LeerMarcadoresDAO();
        List<Marcador> marcadores = leerDAO.obtenerTodosLosMarcadores(username);

        // 3. PARSEO MANUAL A JSON (Regra estricta: Sin librerías externas)
        // Vamos a construir una cadena que simule la estructura de un array: [{}, {}, {}]
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < marcadores.size(); i++) {
            Marcador m = marcadores.get(i);
            
            // Inyectamos las variables de Java en un formato de texto estructurado como JSON
            jsonBuilder.append(String.format(
                "{\"idMarcador\":%d,\"nombre\":\"%s\",\"categoria\":\"%s\",\"coordX\":%s,\"coordY\":%s,\"tachado\":%b}",
                m.getIdMarcador(),
                m.getNombre(),
                m.getCategoria(),
                Float.toString(m.getCoordX()),
                Float.toString(m.getCoordY()),
                m.isTachado()
            ));

            // Si no es el último elemento de la lista, añadimos una coma para separar los objetos
            if (i < marcadores.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        
        jsonBuilder.append("]");
        String jsonFinal = jsonBuilder.toString();

        // 4. Enviamos el String formateado de vuelta al navegador del cliente
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 200: Éxito
        response.getWriter().write(jsonFinal);
    }
}