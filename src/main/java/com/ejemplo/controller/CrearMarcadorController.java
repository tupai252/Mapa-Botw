package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Marcador;
import model.CrearMarcadorDAO; // Importamos el modelo aislado

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/CrearMarcadorController")
public class CrearMarcadorController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 1. Leemos el JSON enviado por el fetch
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonFronend = sb.toString(); 

        try {
            // 2. Extraemos los valores manualmente (sin librerías prohibidas)
            String nombre = extractJsonValueString(jsonFronend, "nombre");
            String categoria = extractJsonValueString(jsonFronend, "categoria");
            
            // Las coordenadas vienen como números en el JSON, el parseo manual es distinto
            float coordX = Float.parseFloat(extractJsonValueNumber(jsonFronend, "coordX"));
            float coordY = Float.parseFloat(extractJsonValueNumber(jsonFronend, "coordY"));

            // 3. Creamos el objeto Marcador
            Marcador nuevoMarcador = new Marcador();
            nuevoMarcador.setNombre(nombre);
            nuevoMarcador.setCategoria(categoria);
            nuevoMarcador.setCoordX(coordX);
            nuevoMarcador.setCoordY(coordY);

            // 4. Llamamos a nuestro DAO aislado
            CrearMarcadorDAO crearDAO = new CrearMarcadorDAO();
            boolean exito = crearDAO.insertarMarcador(nuevoMarcador);

            // 5. Devolvemos la respuesta
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"mensaje\": \"Marcador guardado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Error al guardar en la base de datos\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Formato de datos incorrecto\"}");
        }
    }

    // Método para extraer textos (Strings con comillas en el JSON)
    private String extractJsonValueString(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }

    // Método para extraer números (Sin comillas en el JSON)
    private String extractJsonValueNumber(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "0";
        startIndex += searchKey.length();
        
        // Buscamos dónde termina el número (puede ser una coma o la llave de cierre)
        int endComma = json.indexOf(",", startIndex);
        int endBrace = json.indexOf("}", startIndex);
        
        int endIndex;
        if (endComma != -1 && endComma < endBrace) {
            endIndex = endComma;
        } else {
            endIndex = endBrace;
        }
        
        return json.substring(startIndex, endIndex).trim();
    }
}