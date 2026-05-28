package controller;

import java.io.IOException;
import java.io.BufferedReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ejemplo.model.BorrarUsuarioDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/BorrarUsuario")
public class BorrarUsuarioController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        String username = null;
        if (sb.length() > 0) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(sb.toString(), JsonObject.class);
            if (jsonObject != null && jsonObject.has("username")) {
                username = jsonObject.get("username").getAsString();
            }
        }

        boolean exito = false;
        if (username != null) {
            BorrarUsuarioDAO dao = new BorrarUsuarioDAO();
            exito = dao.borrarUsuario(username);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\": " + exito + "}");
    }
}