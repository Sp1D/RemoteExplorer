/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer.json;

import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.AppService.Pane;
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
public class DirectoryListing {

    private final List<File> list;
//    private final String rootPath;
    private final String leftPath;
    private final String rightPath;
    private String pane;

    private final transient HttpSession sess;
    private final transient AppService as;

    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    public DirectoryListing(HttpSession sess, Pane pane) {
        this.sess = sess;
        as = AppService.inst(sess, AppService.class);
        list = new ArrayList<>();
        leftPath = as.leftPath.toString();
        rightPath = as.rightPath.toString();
//        rootPath = as.rootPath.toString();
        this.pane = pane.toString().toLowerCase();
    }

//  Path formatting
//  HttpServletRequest and Pane only needed for url resolution
    String pf(Path path, Info info) {

        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

            switch (info) {
                case FILENAME:
//                        return path.getName(path.getNameCount() - 1).normalize().toString();
                    return path.getFileName().toString();
                case PARENT:
                    return path.normalize().toString();
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

//    String pLink(Path path, String text) {
//        if (!path.toFile().isDirectory()) {
//            return path.normalize().toString();
//        }
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("<a");
//
//        return sb.toString();
//    }
    public void add(Path path) {
        list.add(new File()
                .addName(pf(path, Info.FILENAME))
                .addDate(pf(path, Info.DATE))
                .addSize(pf(path, Info.SIZE))
                .addPerm(pf(path, Info.ATTRIBUTES)));
    }

    public void addParent(Path path) {
        list.add(new File()
                .addName(pf(path, Info.PARENT))
                .addDate("")
                .addSize("&lt;PARENT&gt;")
                .addPerm(""));

    }

    public List<File> getList() {
        return list;
    }

}
