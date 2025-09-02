/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.sikorski.falfus.WarehouseSecurity.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.deidentifier.arx.DataHandle;
import static pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.anonymizeByChoice;

/**
 *
 * @author sikor
 */
@WebServlet(name = "process", urlPatterns = {"/process"})
public class process extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet process</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet process at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println(req);
        String choice = req.getParameter("choice");
        String lDiv = req.getParameter("useLDiv");
        boolean isLDivOn = (lDiv != null);
        // Generate or locate file based on choice
        DataHandle output = anonymizeByChoice(Integer.parseInt(choice), isLDivOn);
        if (output != null) {
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"anonymized.csv\"");
            output.save(resp.getOutputStream(), ',');
        } else {
            resp.setContentType("text/plain");
            resp.setHeader("Content-Disposition", "attachment; filename=\"result.txt\"");
            try (OutputStream out = resp.getOutputStream()) {
                StringBuilder sb = new StringBuilder();
                String content = "You selected: " + choice + "\n";
                sb.append(content).append("Use l diversity? ").append(isLDivOn).append("\nNo data for you");
                out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        /*try (OutputStream out = resp.getOutputStream()) {
            StringBuilder sb = new StringBuilder();
            String content = "You selected: " + choice + "\n";
            sb.append(content).append("Use l diversity? ").append(isLDivOn);
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }*/
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
