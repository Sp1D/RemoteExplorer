/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class AppService {

    public Path leftPath, rightPath;
    public List<Path> leftListing, rightListing;
    public final Path rootPath = Paths.get("/tmp");
//    final static Path rootPath = Paths.get("/tmp");
//    final static String PATH_PARAM = "path";
    final PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + rootPath.toFile().getAbsolutePath() + "/**");
//    static Path leftPath, rightPath;
//    static List<Path> leftListing, rightListing;
    static Gson gson = new Gson();
        

    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    public enum Pane {

        LEFT, RIGHT, BOTH
    }
    
//    public static AppService instance(HttpSession sess) {
//        AppService as = (AppService)sess.getAttribute("AppService");
//        if (as == null) {
//            as = new AppService();
//        }
//        return as;
//    }
    
    public static <T> T inst(HttpSession sess, Class clazz) {
        T inst = (T)sess.getAttribute(clazz.getSimpleName());
        if (inst == null) {
            try {
                inst = (T)clazz.newInstance();
                sess.setAttribute(clazz.getSimpleName(), inst);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        return inst;
    }

    Path createSecuredPath(String p) {
        
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

    Path getPanePath(Pane pane, HttpServletRequest request) {
        String param = request.getParameter(pane.toString().toLowerCase());
//        Path path = (Path) request.getSession().getAttribute(pane.toString().toLowerCase());  
        Path path = pane == Pane.LEFT ? leftPath : 
                pane == Pane.RIGHT ? rightPath : null;
        
        if (param != null) {
            path = createSecuredPath(param);
        } else if (path == null) {
            path = createSecuredPath("");
        }
//        request.getSession().setAttribute(pane.toString().toLowerCase(), path);        
        return path;
    }

    List<Path> getPaneListings(Pane pane) throws IOException {
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
    
//    void setSessionAttributes(HttpSession sess) {
//        sess.setAttribute("attributes", a);
//    }
    
    void setupPanes(HttpServletRequest request, Pane pane) throws IOException {
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
        
//        setSessionAttributes(request.getSession());

    }

    

    public Path getpLeft() {
        return leftPath;
    }

    public Path getpRight() {
        return rightPath;
    }

}
