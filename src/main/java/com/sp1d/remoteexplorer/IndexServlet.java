/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.AppService.Pane;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sp1d
 */
public class IndexServlet extends HttpServlet {

    private static final long serialVersionUID = -6641542318173215645L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("RUNNING DOGET IN INDEX SERVLET");
        AppService as = AppService.inst(req.getSession(),AppService.class);                

        Pane pane;
        if (req.getParameterMap().containsKey(Pane.LEFT.toString().toLowerCase())) {
            pane = Pane.LEFT;
        } else if (req.getParameterMap().containsKey(Pane.RIGHT.toString().toLowerCase())) {
            pane = Pane.RIGHT;
        } else {
            pane = Pane.BOTH;
        }

        as.setupPanes(req, pane);

        if (pane == Pane.BOTH) {
            req.getRequestDispatcher("mainpage.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("content?"+pane.toString().toLowerCase()).forward(req, resp);            
        }
        

    }

}
