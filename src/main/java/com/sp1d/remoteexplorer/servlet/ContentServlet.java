package com.sp1d.remoteexplorer.servlet;

import com.google.gson.Gson;
import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.AppService.Pane;
import com.sp1d.remoteexplorer.json.DirectoryListing;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Возвращает содержимое панели, указанной в качестве параметра запроса (right
 * или left) в виде сообщения JSON
 *
 * @author sp1d
 */
public class ContentServlet extends HttpServlet {

    private static final long serialVersionUID = -5750499331621265964L;
    private static final Gson gson = new Gson();
    private static final Logger LOG = LogManager.getLogger(ContentServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Entering servlet CONTENT for {}", req.getQueryString());

        AppService as = AppService.inst(req.getSession(), AppService.class);
        DirectoryListing listing = null;
        List<Path> set = new LinkedList<Path>();

        if (req.getParameter(Pane.RIGHT.toString().toLowerCase()) != null) {
            try {
                for (Path path : Files.newDirectoryStream(as.panePaths.get(Pane.RIGHT))) {
                    set.add(path);
                }
            } catch (IOException e) {
                LOG.error("Wrong path {}", as.panePaths.get(Pane.RIGHT).toString());
            }
            listing = new DirectoryListing(req.getSession(), Pane.RIGHT, set);
        } else if (req.getParameter(Pane.LEFT.toString().toLowerCase()) != null) {
            try {
                for (Path path : Files.newDirectoryStream(as.panePaths.get(Pane.LEFT))) {
                    set.add(path);
                }
            } catch (IOException e) {
                LOG.error("Wrong path {}", as.panePaths.get(Pane.LEFT).toString());
            }
            listing = new DirectoryListing(req.getSession(), Pane.LEFT, set);
        }

        if (listing != null) {
            as.sendJSON(resp, gson.toJson(listing));
        }
    }

}
