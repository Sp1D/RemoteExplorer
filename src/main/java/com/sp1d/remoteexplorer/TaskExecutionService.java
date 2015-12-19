/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sp1d
 */
public class TaskExecutionService {
    
    
    final static private ExecutorCompletionService ecs = new ExecutorCompletionService(
            Executors.newSingleThreadExecutor());
    
    static private int tasks;

    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    enum Pane {

        LEFT, RIGHT
    }

    enum TaskType {

        COPY, MOVE
    }

    enum ErrorType {

        NOERROR, FILEEXISTS
    }

    void addTask(TaskType taskType, HttpServletRequest request, Path pLeft, Path pRight) {

        Future f = ecs.submit(createCallable(taskType, request, pLeft, pRight));
        if (f != null) {
            tasks++;
        }
    }

    Callable<ErrorType> createCallable(TaskType taskType, HttpServletRequest request, Path pLeft, Path pRight) {
        if (taskType == TaskType.COPY) {
            return new copyCallable(request, pLeft, pRight);
        } else {
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
        System.out.println(copyFrom);
        System.out.println(copyTo);

        Files.copy(copyFrom, copyTo, option);
    }

}
