/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.json.Task;
import com.sp1d.remoteexplorer.json.Tasks;
import com.sp1d.remoteexplorer.json.Tasks.TaskType;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class TaskExecutionService {

    private final ExecutorCompletionService ecs;
    private final HttpSession session;
    private final Tasks t;
    private final static String RESTRICTED_CHARS_POSIX = "(\\n|\\r|\\\\|\\/)";
    private final AppService as;
    

    public enum ErrorType {

        NOERROR, FILEEXISTS, DIRNOTEMPTY, DIREXISTS
    }

    public TaskExecutionService(HttpSession session) {
        this.session = session;
        t = AppService.inst(session, Tasks.class);
        ecs = new ExecutorCompletionService(Executors.newSingleThreadExecutor());
        as = AppService.inst(session, AppService.class);
    }

    public void addTask(Task task) {
        ecs.submit(newCallable(task));
    }

    public List<Task> pollTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            Future<Task> f;
            while (true) {
                f = ecs.poll();
                if (f != null) {
                    tasks.add(f.get());
                } else break;
            } 
        } catch (ExecutionException | InterruptedException e) {
        }
        return tasks;
    }

    private Callable<Task> newCallable(Task task) {
        if (task.getType() == TaskType.COPY) {
            return new copyCallable(task);
        } else if (task.getType() == TaskType.MOVE) {
            return new moveCallable(task);
        } else if (task.getType() == TaskType.DELETE) {
            return new deleteCallable(task);
        } else if (task.getType() == TaskType.CREATE) {
            return new createCallable(task);
        } else {
            return null;
        }
    }

    class copyCallable implements Callable<Task> {

        private Task task;

        copyCallable(Task task) {
            this.task = task;
        }

        @Override
        public Task call() throws Exception {
            try {
                Files.copy(task.getFrom(), task.getTo());
                task.setError(ErrorType.NOERROR);
            } catch (FileAlreadyExistsException e) {
                task.setError(ErrorType.FILEEXISTS);
            } finally {
                return task;
            }
        }
    }
    
    class moveCallable implements Callable<Task> {

        private Task task;

        moveCallable(Task task) {
            this.task = task;
        }

        @Override
        public Task call() throws Exception {
            try {
                Files.move(task.getFrom(), task.getTo());
                task.setError(ErrorType.NOERROR);
            } catch (FileAlreadyExistsException e) {
                task.setError(ErrorType.FILEEXISTS);
            } finally {
                return task;
            }
        }
    }

    class createCallable implements Callable<Task> {

        private Task task;

        createCallable(Task task) {
            this.task = task;
        }

        @Override
        public Task call() throws Exception {
            try {
                Files.createDirectory(task.getTo());
                task.setError(ErrorType.NOERROR);
            } catch (FileAlreadyExistsException e) {
                task.setError(ErrorType.DIREXISTS);
            } finally {
                return task;
            }
        }
    }

    class deleteCallable implements Callable<Task> {

        private Task task;

        public deleteCallable(Task task) {
            this.task = task;
        }

        @Override
        public Task call() throws Exception {
            try {
                Files.delete(task.getTo());
                task.setError(ErrorType.NOERROR);
            } catch (DirectoryNotEmptyException e) {
                task.setError(ErrorType.FILEEXISTS);
            } finally {
                return task;
            }
        }
    }

}
