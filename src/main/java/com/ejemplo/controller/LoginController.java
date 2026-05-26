package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import model.LoginUsuarioDAO; // Importamos el modelo aislado

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/LoginController")
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Configuración de respuesta JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 2. Lectura del cuerpo de la petición (POST)
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonFronend = sb.toString(); 

        // 3. Extracción manual de datos
        String username = extractJsonValue(jsonFronend, "username");
        String password = extractJsonValue(jsonFronend, "password");

        // 4. Instanciamos EXCLUSIVAMENTE el modelo aislado de Login
        LoginUsuarioDAO loginDAO = new LoginUsuarioDAO();
        Usuario usuarioAutenticado = loginDAO.buscarUsuarioPorCredenciales(username, password);

        // 5. Devolvemos la respuesta al JavaScript
        if (usuarioAutenticado != null) {
            response.setStatus(HttpServletResponse.SC_OK); 
            String jsonResponse = String.format("{\"username\": \"%s\", \"rol\": \"%s\"}", 
                                                usuarioAutenticado.getUsername(), 
                                                usuarioAutenticado.getRol());
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            response.getWriter().write("{\"error\": \"Usuario o contraseña incorrectos\"}");
        }
    }

    // Método auxiliar para no usar librerías externas
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }
}