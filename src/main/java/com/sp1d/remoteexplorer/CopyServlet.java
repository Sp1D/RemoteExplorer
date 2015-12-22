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
public class CopyServlet extends HttpServlet {

    private static final long serialVersionUID = 7622379705277351324L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("RUNNING POST IN COPY SERVLET");
        
        AppService as = AppService.inst(req.getSession(), AppService.class);
        TaskExecutionService tes = AppService.inst(req.getSession(), TaskExecutionService.class);
        TasksJSON tasks = AppService.inst(req.getSession(), TasksJSON.class);
                
        tasks.addTask(new Task(TaskExecutionService.TaskType.COPY, req, as.leftPath, as.rightPath));
        
        as.sendJSON(resp, tasks.getJSON());
    }

}
