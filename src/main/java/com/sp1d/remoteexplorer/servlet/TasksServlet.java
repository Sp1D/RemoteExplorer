
package com.sp1d.remoteexplorer.servlet;

import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.TaskExecutionService;
import com.sp1d.remoteexplorer.json.Tasks;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет возвращает список задач в виде объекта JSON, сформированного из экземпляра
 * Tasks
 * 
 * @author sp1d
 */
public class TasksServlet extends HttpServlet {
    private static final long serialVersionUID = -4498186076678730257L;
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppService as = AppService.inst(req.getSession(), AppService.class);
        TaskExecutionService tes = AppService.inst(req.getSession(), TaskExecutionService.class);        
        Tasks tasks = AppService.inst(req.getSession(), Tasks.class);
        
        as.sendJSON(resp, tasks.getJSON());
    }
    
    
    
}