package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ejemplo.model.ConexionBD;

@WebServlet("/BorrarUsuario")
public class BorrarUsuarioController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        boolean exito = false;

        if (username != null && !username.trim().isEmpty()) {
            String sql = "DELETE FROM usuarios WHERE username = ?";

            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    exito = true;
                }

            } catch (SQLException e) {
                System.err.println("Error al borrar el usuario: " + e.getMessage());
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\": " + exito + "}");
    }
}