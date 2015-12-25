package com.sp1d.remoteexplorer.json;

import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.AppService.Pane;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Класс предназначен для формирования JSON сообщения, формируемого из
 * экземпляра. Содержит листинг файлов некоей директории
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

    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    public DirectoryListing(HttpSession sess, Pane pane, List<Path> unsortedList) {
        list = new ArrayList<>();

        this.pane = pane.toString().toLowerCase();
        as = AppService.inst(sess, AppService.class);
//        leftPath = as.leftPath.toString();
//        rightPath = as.rightPath.toString();
        leftPath = as.panePaths.get(Pane.LEFT).toString();
        rightPath = as.panePaths.get(Pane.RIGHT).toString();

        tempList = new ArrayList<>(unsortedList);
        Collections.sort(tempList, new dirComparator());

        if (!as.rootPath.equals(as.panePaths.get(pane))) {
            this.addParent(as.panePaths.get(pane).getParent());
        }
        
//        if (pane == Pane.RIGHT) {
//            if (!as.rightPath.equals(as.rootPath)) {
//                this.addParent(as.rightPath.getParent());
//            }
//        } else if (pane == Pane.LEFT) {
//            if (!as.leftPath.equals(as.rootPath)) {
//                this.addParent(as.leftPath.getParent());
//            }
//        }
        for (Path path : tempList) {
            this.add(path);
        }
    }

    /*
     * Название от Path Formatting. Формирует строку из указанного пути Path
     * согласно указанному стилю info
     */
    String pf(Path path, Info info) {

        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

            switch (info) {
                case FILENAME:
                    return path.getFileName().toString();
                case PARENT:
                    return path.normalize().toString();
                case DATE:
                    LocalDateTime ldt = LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());
                    return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                case SIZE:
                    return attr.isDirectory() ? "&lt;DIR&gt;" : String.valueOf(attr.size()) + " b";
                case ATTRIBUTES:
                    if (path.getFileSystem().supportedFileAttributeViews().contains("PosixFileAttributeView"))  {
                        return PosixFilePermissions.toString(Files.getPosixFilePermissions(path));
                    } else if (path.getFileSystem().supportedFileAttributeViews().contains("DosFileAttributeView")) {                        
                        DosFileAttributes dfa = Files.readAttributes(path, DosFileAttributes.class);
                        return dfa.isHidden() ? "hidden" : "";
                    }
                default:
                    return "";
            }
        } catch (IOException e) {
            return "";
        }
    }

    /*
     * Добавляет Path к листингу
     */

    public void add(Path path) {

        list.add(new File()
                .addName(pf(path, Info.FILENAME))
                .addDate(pf(path, Info.DATE))
                .addSize(pf(path, Info.SIZE))
                .addPerm(pf(path, Info.ATTRIBUTES)));

    }

    /*
     * Добавляет Path к листингу, подразумевая, что это ссылка на корневую
     * директорию для текущего пути. В отличие от add c использованием
     * Info.FILENAME, addParent с Info.PARENT сформирует полный путь к файлу, а
     * не только название самого файла
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
