/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.TaskExecutionService.ErrorType;
import com.sp1d.remoteexplorer.TaskExecutionService.TaskType;
import java.nio.file.Path;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sp1d
 */
public class Task {

    private TaskType type;
    private ErrorType error;

//    Source path in copy, move operations. Source panel (left or right) in create and delete operations
    private Path from;

//    Destination path in copy, move, create, delete operations
    private Path to;
    
    private final transient long creationTime;
    private final static transient String RESTRICTED_CHARS_POSIX = "(\\n|\\r|\\\\|\\/)";

        
    public Task(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        if (type == TaskType.COPY) {
            initCopyTask(type, req, lePanePath, riPanePath);
        } else if (type == TaskType.MOVE) {

        } else if (type == TaskType.CREATE) {
            initCreateTask(type, req, lePanePath, riPanePath);
        } else if (type == TaskType.DELETE) {
            initDeleteTask(type, req, lePanePath, riPanePath);
        } 
        creationTime = System.currentTimeMillis();
    }

    private void initCopyTask(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        Path copyFrom = null, copyTo = null;

        if (req.getParameter("to").equalsIgnoreCase("right")) {
            copyTo = riPanePath;
            copyFrom = lePanePath.resolve(req.getParameter("from"));
        } else if (req.getParameter("to").equalsIgnoreCase("left")) {
            copyTo = lePanePath;
            copyFrom = riPanePath.resolve(req.getParameter("from"));
        }

        if (copyFrom == null || copyTo == null || !copyTo.toFile().isDirectory()) {
            return;
        }
        copyTo = copyTo.resolve(copyFrom.getFileName());

        this.type = type;
        this.from = copyFrom;
        this.to = copyTo;
    }

    private void initCreateTask(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        Path createPath = null;

        String filename = req.getParameter("to").replaceAll(RESTRICTED_CHARS_POSIX, "_");

        if (req.getParameter("from").equalsIgnoreCase("right")) {
            createPath = riPanePath.resolve(filename);
        } else if (req.getParameter("from").equalsIgnoreCase("left")) {
            createPath = lePanePath.resolve(filename);
        }

        if (createPath == null) {
            return;
        }

        this.to = createPath;
    }

    private void initDeleteTask(TaskType type, HttpServletRequest req, Path lePanePath, Path riPanePath) {
        Path deleteFrom = null;
        
        if (req.getParameter("from").equalsIgnoreCase("right")) {
            deleteFrom = riPanePath.resolve(req.getParameter("to"));
        } else if (req.getParameter("from").equalsIgnoreCase("left")) {
            deleteFrom = lePanePath.resolve(req.getParameter("to"));
        }

        if (deleteFrom == null) {
            return;
        }
        this.to = deleteFrom;
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
