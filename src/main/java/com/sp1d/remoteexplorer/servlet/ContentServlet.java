
package com.sp1d.remoteexplorer.servlet;

import com.google.gson.Gson;
import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.AppService.Pane;
import com.sp1d.remoteexplorer.json.DirectoryListing;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Возвращает содержимое панели, указанной в качестве параметра запроса (right или
 * left) в виде сообщения JSON
 * 
 * @author sp1d
 */
public class ContentServlet extends HttpServlet {

    private static final long serialVersionUID = -5750499331621265964L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppService as = AppService.inst(req.getSession(), AppService.class);

        DirectoryListing listing = null;
        if (req.getParameter("right") != null) {
            listing = new DirectoryListing(req.getSession(), Pane.RIGHT);
            if (!as.rightPath.equals(as.ROOT_PATH)) {
                listing.addParent(as.rightPath.getParent());
            }
            for (Path path : Files.newDirectoryStream(as.rightPath)) {
                listing.add(path);
            }
        } else if (req.getParameter("left") != null) {
            listing = new DirectoryListing(req.getSession(), Pane.LEFT);
            if (!as.leftPath.equals(as.ROOT_PATH)) {
                listing.addParent(as.leftPath.getParent());
            }
            for (Path path : Files.newDirectoryStream(as.leftPath)) {
                listing.add(path);
            }
        }

        if (listing != null) {            
            as.sendJSON(resp, gson.toJson(listing));
        }
    }

}
