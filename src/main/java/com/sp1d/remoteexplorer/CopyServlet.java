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
        
        TaskExecutionService tes = new TaskExecutionService();
//        tes.addTask(TaskType.COPY, req, pLeft, pRight);
        
        resp.setContentType("application/json");
        resp.getWriter().write(AppService.gson.toJson(new TasksJSON()));        
    }

}
