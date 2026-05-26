package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import model.UsuarioDAO;

import java.io.BufferedReader;
import java.io.IOException;

// Esta etiqueta es vital: enlaza la URL del fetch de JS con este archivo Java
@WebServlet("/RegisterController")
public class RegisterController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Configurar la respuesta que le enviaremos de vuelta a JavaScript (en formato JSON)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 2. Leer el JSON en bruto que envía el Frontend en el cuerpo de la petición (fetch)
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonFronend = sb.toString(); 
        // El JSON se ve así: {"username":"pepe", "email":"pepe@mail.com", "password":"123"}

        // 3. Como no podemos usar librerías como Gson, extraemos los datos manualmente
        // Limpiamos las comillas y llaves para sacar los valores a lo bruto
        String username = extractJsonValue(jsonFronend, "username");
        String email = extractJsonValue(jsonFronend, "email");
        String password = extractJsonValue(jsonFronend, "password");

        // 4. Creamos el objeto Usuario con los datos extraídos
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPasswordHash(password);

        // 5. Llamamos al Modelo para que lo guarde en MySQL
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean guardadoConExito = usuarioDAO.registrarUsuario(nuevoUsuario);

        // 6. Respondemos al Frontend según el resultado
        if (guardadoConExito) {
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 200: Todo bien
            response.getWriter().write("{\"mensaje\": \"Usuario registrado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400: Error (ej. usuario ya existe)
            response.getWriter().write("{\"error\": \"No se pudo registrar el usuario. Es posible que el correo o nombre ya existan.\"}");
        }
    }

    // Método auxiliar (chuleta) para extraer valores de un JSON plano sin usar librerías externas
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }
}