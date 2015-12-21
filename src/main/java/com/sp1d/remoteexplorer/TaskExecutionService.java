/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class TaskExecutionService {

    private final ExecutorCompletionService ecs;
    private final HttpSession session;
    private final TasksJSON t;

    enum Pane {

        LEFT, RIGHT
    }

    enum TaskType {

        COPY, MOVE, DELETE, CREATE
    }

    enum ErrorType {

        NOERROR, FILEEXISTS, DIRNOTEMPTY, DIREXISTS
    }

    public TaskExecutionService(HttpSession session) {
        this.session = session;
        t = AppService.inst(session, TasksJSON.class);
        ecs = new ExecutorCompletionService(Executors.newSingleThreadExecutor());
    }
    
    

    void addTask(TaskType taskType, HttpServletRequest request, Path pLeft, Path pRight) {
        if (ecs.submit(newCallable(taskType, request, pLeft, pRight)) != null) {
            t.tasks++;
        }
    }

    void pollTasks() {
        while (ecs.poll() != null) {
            t.tasks--;
        }

    }

    Callable<ErrorType> newCallable(TaskType taskType, HttpServletRequest request, Path pLeft, Path pRight) {
        if (taskType == TaskType.COPY) {
            return new copyCallable(request, pLeft, pRight);
        } else if (taskType == TaskType.DELETE) {
            return new deleteCallable(request, pLeft, pRight);
        } else if (taskType == TaskType.CREATE) {
            return new createCallable(request, pLeft, pRight);
        } else  {
            return null;
        }
    }

    class copyCallable implements Callable<ErrorType> {

        HttpServletRequest request;
        Path pLeft, pRight;

        copyCallable(HttpServletRequest request, Path pLeft, Path pRight) {
            this.request = request;
            this.pLeft = pLeft;
            this.pRight = pRight;
        }

        @Override
        public ErrorType call() throws Exception {
            try {
                copy(StandardCopyOption.COPY_ATTRIBUTES, request, pLeft, pRight);
                return ErrorType.NOERROR;
            } catch (FileAlreadyExistsException e) {
                return ErrorType.FILEEXISTS;
            }
        }

        void copy(CopyOption option, HttpServletRequest request, Path pLeft, Path pRight) throws IOException {
            Path copyFrom = null, copyTo = null;

            if (request.getParameter("to").equalsIgnoreCase("right")) {
                copyFrom = pLeft.resolve(request.getParameter("from"));
                copyTo = pRight;
            } else if (request.getParameter("to").equalsIgnoreCase("left")) {
                copyFrom = pRight.resolve(request.getParameter("from"));
                copyTo = pLeft;
            }

            if (copyFrom == null || copyTo == null || !copyTo.toFile().isDirectory()) {
                return;
            }
            copyTo = copyTo.resolve(copyFrom.getFileName());

//        Testing output
            System.out.println("copying from "+ copyFrom);
            System.out.println("copying to " + copyTo);

            Files.copy(copyFrom, copyTo, option);
        }
    }

    class createCallable implements Callable<ErrorType> {

        HttpServletRequest request;
        Path pLeft, pRight;

        createCallable(HttpServletRequest request, Path pLeft, Path pRight) {
            this.request = request;
            this.pLeft = pLeft;
            this.pRight = pRight;
        }

        @Override
        public ErrorType call() throws Exception {
            try {
                create(request, pLeft, pRight);
                return ErrorType.NOERROR;
            } catch (FileAlreadyExistsException e) {
                return ErrorType.DIREXISTS;
            }
        }

        void create(HttpServletRequest request, Path pLeft, Path pRight) throws IOException {
            Path createPath = null;
            
            if (request.getParameter("from").equalsIgnoreCase("right")) {
                createPath = pRight.resolve(request.getParameter("to"));
            } else if (request.getParameter("from").equalsIgnoreCase("left")) {
                createPath = pLeft.resolve(request.getParameter("to"));
            }

            if (createPath == null) {
                return;
            }

//        Testing output
            System.out.println("creating " + createPath);
            
            Files.createDirectory(createPath);
        }
    }
    
    class deleteCallable implements Callable<ErrorType> {

        HttpServletRequest request;
        Path pLeft, pRight;

        deleteCallable(HttpServletRequest request, Path pLeft, Path pRight) {
            this.request = request;
            this.pLeft = pLeft;
            this.pRight = pRight;
        }

        @Override
        public ErrorType call() throws Exception {
            try {
                delete(request, pLeft, pRight);
                return ErrorType.NOERROR;
            } catch (DirectoryNotEmptyException e) {
                return ErrorType.FILEEXISTS;
            }
        }

        void delete(HttpServletRequest request, Path pLeft, Path pRight) throws IOException {
            Path deleteFrom = null;
            
            if (request.getParameter("from").equalsIgnoreCase("right")) {
                deleteFrom = pRight.resolve(request.getParameter("to"));
            } else if (request.getParameter("from").equalsIgnoreCase("left")) {
                deleteFrom = pLeft.resolve(request.getParameter("to"));
            }

            if (deleteFrom == null) {
                return;
            }

//        Testing output
            System.out.println("deleting " + deleteFrom);

            Files.delete(deleteFrom);
        }
    }

}
