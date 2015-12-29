package com.sp1d.remoteexplorer.json;

import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.AppService.Pane;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс предназначен для формирования JSON сообщения, формируемого из
 * экземпляра. Содержит листинг файлов некоей директории.
 *
 * @author sp1d
 */
public class DirectoryListing {

    private final List<File> list;

    private final String leftPath, rightPath;
    private final String pane;
    private final String separator = AppService.SEPARATOR;

    private final transient AppService as;
    private transient List<Path> tempList;
    private transient Logger log = LogManager.getLogger(DirectoryListing.class);
    private transient StringBuilder sb = new StringBuilder();

    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    public DirectoryListing(HttpSession sess, Pane pane, List<Path> unsortedList) {
        list = new ArrayList<>();

        this.pane = pane.toString().toLowerCase();
        as = AppService.inst(sess, AppService.class);
        leftPath = as.panePaths.get(Pane.LEFT).toString();
        rightPath = as.panePaths.get(Pane.RIGHT).toString();

        tempList = new ArrayList<>(unsortedList);
        Collections.sort(tempList, new dirComparator());

        if (!as.rootPath.equals(as.panePaths.get(pane))) {
            this.addParent(as.panePaths.get(pane).getParent());
        }

        for (Path path : tempList) {
            this.add(path);
        }
    }

    /*
      Название от Path Formatting. Формирует строку из указанного пути Path
      согласно указанному стилю info
     */
    String pf(Path path, Info info) {
       
        
        try {
            BasicFileAttributes attr = null;
            DosFileAttributes attrDos = null;
            boolean posix = false;

            if (path.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                posix = true;
                attr = Files.readAttributes(path, BasicFileAttributes.class);
            } else if (path.getFileSystem().supportedFileAttributeViews().contains("dos")) {
                posix = false;
                attrDos = Files.readAttributes(path, DosFileAttributes.class);
                attr = attrDos;
            } else {
                log.fatal("Filesystem is not supported");
                throw new IOException("Filesystem is not supported".intern());
            }

            switch (info) {
                case FILENAME:
                    return path.getFileName().toString();
                case PARENT:
                    return "..";
                case DATE:
                    LocalDateTime ldt = LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());
                    return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                case SIZE:
                    
                    return attr.isDirectory() ? "&lt;DIR&gt;" : String.format("%,d",attr.size()) + " B";
                    
                case ATTRIBUTES:
                    if (posix) {
                        return PosixFilePermissions.toString(Files.getPosixFilePermissions(path));
                    } else {                        
                        if (attrDos != null) {
                            sb.append(attrDos.isHidden() ? "H/" : "")
                                    .append(attrDos.isReadOnly() ? "R/" : "")
                                    .append(attrDos.isArchive() ? "A/" : "")
                                    .append(attrDos.isSystem() ? "S/" : "");
                            String attrString = "";
                            if (sb.length() > 0) {
                                sb = sb.deleteCharAt(sb.length()-1);
                                attrString = sb.toString();
                                sb = sb.delete(0, sb.length());                                
                            }
                            return attrString;                            
                        } else return "";
                    }
                default:
                    return "";
            }
        } catch (IOException e) {
            return "";
        }
    }
    
    private String getIcon(Path path) {
        String filename = path.getFileName().toString();
        sb.append(filename.substring(filename.lastIndexOf(".")+1));
        String result;
        if (as.icons.contains(sb.toString())) {
            result = sb.append(".png").toString().intern();
            sb.delete(0, sb.length());
            return result;
        } else {
            sb.delete(0, sb.length());
            return "_blank.png".intern();
        }
    }

    /*
      Добавляет Path к листингу
     */
    public void add(Path path) {

        list.add(new File()
                .addName(pf(path, Info.FILENAME))
                .addDate(pf(path, Info.DATE))
                .addSize(pf(path, Info.SIZE))
                .addPerm(pf(path, Info.ATTRIBUTES))
                .addIcon(getIcon(path)));

    }

    /*
      Добавляет Path к листингу, форматируя в виде перехода в вышестоящую 
    директорию
     */
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

    private class dirComparator implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            if (o1.toFile().isDirectory() && !o2.toFile().isDirectory()) {
                return -1;
            } else if (!o1.toFile().isDirectory() && o2.toFile().isDirectory()) {
                return 1;
            } else {
                int compare = o1.getFileName().toString().compareToIgnoreCase(o2.getFileName().toString());
                return compare > 0 ? 1 : compare == 0 ? 0 : -1;
            }
        }

    }

}
