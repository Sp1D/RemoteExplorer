/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.google.gson.Gson;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sp1d
 */
public class AppService {
    
    final static Path pRoot = Paths.get("/tmp");
    final static String PATH_PARAM = "path";
    final static PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + pRoot.toFile().getAbsolutePath() + "/**");
    static Path pLeft, pRight;
    static Gson gson = new Gson();
    
    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    public enum Pane {

        LEFT, RIGHT
    }
    
    static Path createSecuredPath(String p) {
        Path secured = pRoot;
        if (p != null) {
            try {
                Path path = Paths.get(p);
                if (!path.isAbsolute()) {
                    Path temp = pRoot.resolve(path);
                    if (!pm.matches(temp) || !temp.toFile().exists()) {
                        secured = pRoot;
                    } else {
                        secured = temp;
                    }
                }
            } catch (InvalidPathException e) {
                return pRoot;
            }
        }

        return secured;
    }
    
    static Path setPanePath(Pane pane, HttpServletRequest request) {
        String param = request.getParameter(pane.toString().toLowerCase());
        Path path = (Path) request.getSession().getAttribute(pane.toString().toLowerCase());

        if (param != null) {
            path = createSecuredPath(param);
        } else if (path == null) {
            path = createSecuredPath("");
        }
        request.getSession().setAttribute(pane.toString().toLowerCase(), path);
        return path;
    }
    
    static void setupPanePaths(HttpServletRequest request) {
        System.out.println("TRYING TO SETUP PANES PATHS");
        pLeft = setPanePath(Pane.LEFT, request);
        pRight = setPanePath(Pane.RIGHT, request);        
    }

    public static Path getpLeft() {
        return pLeft;
    }

    public static Path getpRight() {
        return pRight;
    }
    
    
}
