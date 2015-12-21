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
public class CreateServlet extends HttpServlet{
    private static final long serialVersionUID = 1526973095086629686L;
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("RUNNING POST IN CREATE SERVLET");
        
        AppService as = AppService.inst(req.getSession(), AppService.class);
        TaskExecutionService tes = AppService.inst(req.getSession(), TaskExecutionService.class);        
        
        tes.addTask(TaskExecutionService.TaskType.CREATE, req, as.leftPath, as.rightPath);
        
        as.sendTasksJSON(req, resp);        
    }
    
    
}
