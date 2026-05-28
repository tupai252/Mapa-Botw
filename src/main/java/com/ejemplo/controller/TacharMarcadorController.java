package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TacharMarcadorDAO;
import model.Marcador; // Importamos el modelo de actualización

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/TacharMarcadorController")
public class TacharMarcadorController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 1. Leemos el cuerpo de la petición
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonFronend = sb.toString(); 

        try {
            int idMarcador = 0;
            java.util.regex.Matcher mId = java.util.regex.Pattern.compile("\"idMarcador\"\\s*:\\s*(\\d+)").matcher(jsonFronend);
            if (mId.find()) {
                idMarcador = Integer.parseInt(mId.group(1));
            }

            boolean tachado = java.util.regex.Pattern.compile("\"tachado\"\\s*:\\s*true").matcher(jsonFronend).find();
            
            String username = null;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"username\"\\s*:\\s*\"([^\"]*)\"").matcher(jsonFronend);
            if (m.find()) {
                username = m.group(1);
            }

            System.out.println("DEBUG TACHAR: JSON recibido = " + jsonFronend);
            System.out.println("DEBUG TACHAR: idMarcador=" + idMarcador + ", tachado=" + tachado + ", username=" + username);

            if (username == null || username.isEmpty() || idMarcador == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Datos insuficientes o usuario no autenticado\"}");
                return;
            }

            // 3. Llamamos al DAO aislado de actualización
            TacharMarcadorDAO tacharDAO = new TacharMarcadorDAO();
            boolean exito = tacharDAO.alternarTachado(idMarcador, username, tachado);

            // 4. Respondemos
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"mensaje\": \"Marcador actualizado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"No se pudo actualizar\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Formato de datos incorrecto\"}");
        }
    }

    // Método auxiliar (chuleta)
    private String extractJsonValueNumber(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "0";
        startIndex += searchKey.length();
        int endComma = json.indexOf(",", startIndex);
        int endBrace = json.indexOf("}", startIndex);
        int endIndex = (endComma != -1 && endComma < endBrace) ? endComma : endBrace;
        return json.substring(startIndex, endIndex).trim();
    }
}