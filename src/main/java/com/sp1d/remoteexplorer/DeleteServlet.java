/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.TaskExecutionService.TaskType;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sp1d
 */
public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 7622379705277351324L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        TaskExecutionService tes = new TaskExecutionService();
        Attributes a = Attributes.getInstance();
        
        tes.addTask(TaskType.DELETE, req, a.leftPath, a.rightPath);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(AppService.gson.toJson(TasksJSON.getInstance()));        
    }

}
