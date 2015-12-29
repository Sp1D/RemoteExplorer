package com.sp1d.remoteexplorer.json;

import com.sp1d.remoteexplorer.AppService.Pane;
import com.sp1d.remoteexplorer.TaskExecutionService.ErrorType;
import com.sp1d.remoteexplorer.json.Tasks.TaskType;
import java.nio.file.Path;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

/**
 * Класс формирует объект задачи (копирования, удаления и т.д.), который
 * передается по цепочке исполнителей и после возращается с установленным
 * значением поля error (которое ссылается на null после формирования
 * конструктором)
 *
 * Также используется для формирования объекта JSON
 *
 * @author sp1d
 */
public class Task {

    private TaskType type;
    private ErrorType error;
//    From and To in string for json 
    private String f, t;

//    Source path in copy, move operations. Source panel (left or right) in create and delete operations
    private transient Path from;

//    Destination path in copy, move, create, delete operations
    private transient Path to;

    private final transient long creationTime;
    private final static transient String RESTRICTED_CHARS_POSIX = "(\\n|\\r|\\\\|\\/)";

    public Task(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        this.type = type;

        switch (type) {
            case COPY:
                initCopyMoveTask(type, req, lePanePath, riPanePath);
                break;
            case MOVE:
                initCopyMoveTask(type, req, lePanePath, riPanePath);
                break;
            case CREATE:
                initCreateDeleteTask(type, req, lePanePath, riPanePath);
                break;
            case DELETE:
                initCreateDeleteTask(type, req, lePanePath, riPanePath);
                break;
        }
        creationTime = System.currentTimeMillis();
        this.f = from != null ? from.toString() : "";
        this.t = to != null ? to.toString() : "";
    }

    private void initCopyMoveTask(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        Path fromL = null, toL = null;

        if (req.getParameter("to").equalsIgnoreCase(Pane.RIGHT.toString())) {
            toL = riPanePath;
            fromL = lePanePath.resolve(req.getParameter("from"));
        } else if (req.getParameter("to").equalsIgnoreCase(Pane.LEFT.toString())) {
            toL = lePanePath;
            fromL = riPanePath.resolve(req.getParameter("from"));
        }

        if (fromL == null || toL == null || !toL.toFile().isDirectory()) {
            return;
        }
        toL = toL.resolve(fromL.getFileName());

        this.from = fromL;
        this.to = toL;
    }

    private void initCreateDeleteTask(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        Path localTo = null;

        String filename;
        if (type == TaskType.CREATE) {
            filename = req.getParameter("to").replaceAll(RESTRICTED_CHARS_POSIX, "_");
        } else {
            filename = req.getParameter("to");
        }

        if (req.getParameter("from").equalsIgnoreCase(Pane.RIGHT.toString())) {
            localTo = riPanePath.resolve(filename);
        } else if (req.getParameter("from").equalsIgnoreCase(Pane.LEFT.toString())) {
            localTo = lePanePath.resolve(filename);
        }

        if (localTo == null) {
            return;
        }

        this.to = localTo;
    }
   
    public TaskType getType() {
        return type;
    }

    public ErrorType getError() {
        return error;
    }

    public void setError(ErrorType error) {
        this.error = error;
    }

    public Path getFrom() {
        return from;
    }

    public Path getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + Objects.hashCode(this.from);
        hash = 59 * hash + Objects.hashCode(this.to);
        hash = 59 * hash + (int) (this.creationTime ^ (this.creationTime >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (this.creationTime != other.creationTime) {
            return false;
        }
        return true;
    }

}
