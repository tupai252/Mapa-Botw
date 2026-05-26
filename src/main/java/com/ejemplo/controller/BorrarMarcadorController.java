package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BorrarMarcadorDAO;
import model.Marcador; // Importamos el modelo aislado

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/BorrarMarcadorController")
public class BorrarMarcadorController extends HttpServlet {

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
            // 2. Extraemos el ID del marcador a borrar
            String idString = extractJsonValueNumber(jsonFronend, "idMarcador");
            int idMarcador = Integer.parseInt(idString);

            // 3. Invocamos al DAO aislado
            BorrarMarcadorDAO borrarDAO = new BorrarMarcadorDAO();
            boolean exito = borrarDAO.eliminarMarcador(idMarcador);

            // 4. Respondemos al Frontend
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"mensaje\": \"Marcador eliminado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"No se pudo eliminar el marcador (puede que no exista)\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Formato de datos incorrecto\"}");
        }
    }

    // Método auxiliar para extraer números enteros del JSON
    private String extractJsonValueNumber(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "0";
        startIndex += searchKey.length();
        
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