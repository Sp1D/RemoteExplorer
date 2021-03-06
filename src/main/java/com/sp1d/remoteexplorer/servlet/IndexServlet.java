package com.sp1d.remoteexplorer.servlet;

import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.AppService.Pane;
import com.sp1d.remoteexplorer.TaskExecutionService;
import com.sp1d.remoteexplorer.json.Task;
import com.sp1d.remoteexplorer.json.Tasks;
import com.sp1d.remoteexplorer.json.Tasks.TaskType;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Универсальный сервлет, который отслеживает переходы пользователя по структуре
 * каталогов или принимает к исполнению задачи Task
 *
 * @author sp1d
 */
public class IndexServlet extends HttpServlet {

    private static final long serialVersionUID = -6641542318173215645L;
    private static final Logger LOG = LogManager.getLogger(IndexServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Entering doGET in servlet INDEX for {}", req.getQueryString());
        AppService as = AppService.inst(req.getSession(), AppService.class);

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
            req.getRequestDispatcher("content?" + pane.toString().toLowerCase()).forward(req, resp);
        }
    }

    /*
     * Принимает задачи на выполнение
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Entering doPOST in servlet INDEX for {}", req.getQueryString());
        int s = req.getRequestURI().lastIndexOf("/");
        String command = req.getRequestURI().substring(s + 1);

        AppService as = AppService.inst(req.getSession(), AppService.class);
        TaskExecutionService tes = AppService.inst(req.getSession(), TaskExecutionService.class);
        Tasks tasks = AppService.inst(req.getSession(), Tasks.class);

        tasks.addTask(new Task(TaskType.valueOf(command.toUpperCase()), req,
                as.panePaths.get(Pane.LEFT), as.panePaths.get(Pane.RIGHT)));

        as.sendJSON(resp, tasks.getJSON());

    }

}
