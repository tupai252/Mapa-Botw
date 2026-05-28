package controller;

import java.io.IOException;
import java.io.BufferedReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ejemplo.model.BorrarUsuarioDAO;

@WebServlet("/BorrarUsuario")
public class BorrarUsuarioController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");

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