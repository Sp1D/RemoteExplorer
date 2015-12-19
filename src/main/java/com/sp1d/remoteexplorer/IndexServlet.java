/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sp1d
 */
public class IndexServlet extends HttpServlet{
    private static final long serialVersionUID = -6641542318173215645L;
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppService.setupPanePaths(req);
        System.out.println("RUNNING DOGET IN INDEX SERVLET");
        req.getRequestDispatcher("mainpage.jsp").forward(req, resp);        
        
    }
    
}
