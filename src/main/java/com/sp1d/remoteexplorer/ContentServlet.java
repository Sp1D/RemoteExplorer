/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import static com.sp1d.remoteexplorer.AppService.gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sp1d
 */
public class ContentServlet extends HttpServlet {
    private static final long serialVersionUID = -5750499331621265964L;
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Attributes a = AppService.inst(req.getSession(), Attributes.class);
        DirectoryListingJSON listing = new DirectoryListingJSON(req.getSession());
        
        for (Path path : Files.newDirectoryStream(a.leftPath)) {
            listing.add(path);
        }
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(listing.getList()));       
    }
    
    
    
}
