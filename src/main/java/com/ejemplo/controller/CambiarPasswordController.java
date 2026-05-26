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

@WebServlet("/CambiarPassword")
public class CambiarPasswordController extends HttpServlet {

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    String username = request.getParameter("username");
    String nuevaPassword = request.getParameter("new_password");
    
    // ESTA LÍNEA ES LA CLAVE: Mira la consola de Docker al probar
    System.out.println("DEBUG: Recibido usuario [" + username + "] y nueva clave [" + nuevaPassword + "]");
    
    boolean exito = false;
    // ... resto de tu código ...

        if (username != null && nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            String sql = "UPDATE usuarios SET password_hash = ? WHERE username = ?";

            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nuevaPassword);
                pstmt.setString(2, username);
                
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    exito = true;
                }

            } catch (SQLException e) {
                System.err.println("Error al cambiar contraseña: " + e.getMessage());
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\": " + exito + "}");
    }
}