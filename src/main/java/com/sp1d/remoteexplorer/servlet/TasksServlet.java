
package com.sp1d.remoteexplorer.servlet;

import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.json.Tasks;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервлет возвращает список задач в виде объекта JSON, сформированного из экземпляра
 * Tasks
 * 
 * @author sp1d
 */
public class TasksServlet extends HttpServlet {
    private static final long serialVersionUID = -4498186076678730257L;
    private static final Logger LOG = LogManager.getLogger(TasksServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Entering servlet TASKS");
        AppService as = AppService.inst(req.getSession(), AppService.class);
        Tasks tasks = AppService.inst(req.getSession(), Tasks.class);
        
        as.sendJSON(resp, tasks.getJSON());
    }
    
    
    
}
