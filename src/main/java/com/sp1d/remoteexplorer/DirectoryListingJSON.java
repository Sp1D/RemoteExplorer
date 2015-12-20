/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class DirectoryListingJSON {
    private List<FileJSON> list = new ArrayList<>();
    HttpSession sess;
    Attributes a = AppService.inst(sess,Attributes.class);                

        
    
    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    enum Pane {

        LEFT, RIGHT
    }

    public DirectoryListingJSON(HttpSession sess) {
        this.sess = sess;
    }
    
    

//  Path formatting
    String pf(Path path, Info info) {
        return pf(path, info, null, null);
    }

//  Path formatting
//  HttpServletRequest and Pane only needed for url resolution
    String pf(Path path, Info info, HttpServletRequest request, Pane pane) {

        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

            switch (info) {
                case FILENAME:
                    if (!attr.isDirectory() || request == null || pane == null) {
                        return path.getName(path.getNameCount() - 1).normalize().toString();

                    } else {
                        return "<a href=\""
                                + request.getContextPath() + "?" + pane.toString().toLowerCase() + "=" + path.normalize().toString().replaceFirst(a.rootPath.toString() + "/", "")
                                + "\">" + path.getName(path.getNameCount() - 1).normalize().toString() + "</a>";
                    }
                case PARENT:
                    if (!attr.isDirectory() || request == null || pane == null) {
                        return "#";

                    } else {
                        return "<a href=\""
                                + request.getContextPath() + "?" + pane.toString().toLowerCase() + "=" + path.normalize().toString().replaceFirst(a.rootPath.toString() + "/", "")
                                + "\">..</a>";
                    }
                case DATE:
                    LocalDateTime ldt = LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());
                    return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                case SIZE:
                    return attr.isDirectory() ? "&lt;DIR&gt;" : String.valueOf(attr.size() / 1000) + " Kb";
                case ATTRIBUTES:
                    return PosixFilePermissions.toString(Files.getPosixFilePermissions(path));
                default:
                    return "";
            }
        } catch (IOException e) {
            return "";
        }
    }

    String pLink(Path path, String text) {
        if (!path.toFile().isDirectory()) {
            return path.normalize().toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<a");

        return sb.toString();
    }

    
    public void add(Path path) {
        list.add(new FileJSON(path.getFileName().toString()));
    }

    public List<FileJSON> getList() {
        return list;
    }
    
    
}
