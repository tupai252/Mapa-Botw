package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TacharMarcadorDAO;

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
            // 2. Extraemos el ID y el booleano
            int idMarcador = 0;
            java.util.regex.Matcher mId = java.util.regex.Pattern.compile("\"idMarcador\"\\s*:\\s*(\\d+)").matcher(jsonFronend);
            if (mId.find()) {
                idMarcador = Integer.parseInt(mId.group(1));
            }

            boolean tachado = java.util.regex.Pattern.compile("\"tachado\"\\s*:\\s*true").matcher(jsonFronend).find();

            if (idMarcador == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"ID de marcador inválido\"}");
                return;
            }

            // 3. Llamamos al DAO
            TacharMarcadorDAO tacharDAO = new TacharMarcadorDAO();
            boolean exito = tacharDAO.alternarTachado(idMarcador, tachado);

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
}