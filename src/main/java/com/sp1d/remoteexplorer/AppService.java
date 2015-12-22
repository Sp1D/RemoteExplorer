/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.json.Tasks;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class AppService {

    public Path leftPath, rightPath;
    public List<Path> leftListing, rightListing;
    public final Path rootPath = Paths.get("/tmp");
    final PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + rootPath.toFile().getAbsolutePath() + "/**");
    static Gson gson = new Gson();
        
    public enum Pane {

        LEFT, RIGHT, BOTH;        
    }
    
    
    public static <T> T inst(HttpSession sess, Class clazz) {
        T inst = (T) sess.getAttribute(clazz.getSimpleName());
        if (inst == null) {
            try {
                if (clazz.equals(TaskExecutionService.class) || clazz.equals(Tasks.class)) {
                    inst = (T) clazz.getConstructor(HttpSession.class).newInstance(sess);
                } else {
                    inst = (T) clazz.newInstance();
                }
                sess.setAttribute(clazz.getSimpleName(), inst);
            } catch (InstantiationException | IllegalAccessException |
                    NoSuchMethodException | InvocationTargetException ex) {
                
            }
        }
        return inst;
    }

    private Path createSecuredPath(String p) {

        Path secured = rootPath;
        if (p != null) {
            try {
                Path path = Paths.get(p);
                if (!path.isAbsolute()) {
                    Path temp = rootPath.resolve(path);
                    if (!pm.matches(temp) || !temp.toFile().exists()) {
                        secured = rootPath;
                    } else {
                        secured = temp;
                    }
                }
            } catch (InvalidPathException e) {
                return rootPath;
            }
        }

        return secured;
    }

    private Path getPanePath(Pane pane, HttpServletRequest request) {
        String param = request.getParameter(pane.toString().toLowerCase());
//        Path path = (Path) request.getSession().getAttribute(pane.toString().toLowerCase());  
        Path path = pane == Pane.LEFT ? leftPath
                : pane == Pane.RIGHT ? rightPath : null;

        if (param != null) {
            path = createSecuredPath(param);
        } else if (path == null) {
            path = createSecuredPath("");
        }
//        request.getSession().setAttribute(pane.toString().toLowerCase(), path);        
        return path;
    }

    private List<Path> getPaneListings(Pane pane) throws IOException {
        List<Path> result = new ArrayList<>();
        Path scanPath = pane == Pane.LEFT ? leftPath
                : pane == Pane.RIGHT ? rightPath : null;
        if (scanPath != null) {
            for (Path path : Files.newDirectoryStream(scanPath)) {
                result.add(path);
            }
        }
        return result;
    }

    public void setupPanes(HttpServletRequest request, Pane pane) throws IOException {
        System.out.println("TRYING TO SETUP PANES PATHS, PANE: " + pane.toString());

        switch (pane) {
            case LEFT:
                leftPath = getPanePath(pane, request);
                leftListing = getPaneListings(pane);
                break;
            case RIGHT:
                rightPath = getPanePath(pane, request);
                rightListing = getPaneListings(pane);
                break;
            case BOTH:
                leftPath = getPanePath(Pane.LEFT, request);
                rightPath = getPanePath(Pane.RIGHT, request);
                leftListing = getPaneListings(Pane.LEFT);
                rightListing = getPaneListings(Pane.RIGHT);
        }
        System.out.println("left: "+leftPath);
        System.out.println("right: "+rightPath);
        System.out.println("root: "+rootPath);
    }
    
    public void sendJSON(HttpServletResponse resp, String json) throws IOException{
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    public Path getpLeft() {
        return leftPath;
    }

    public Path getpRight() {
        return rightPath;
    }

}
