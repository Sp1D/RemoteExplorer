package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.json.Task;
import com.sp1d.remoteexplorer.json.Tasks.TaskType;
import java.io.IOException;
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
 *  Занимается непосредственно запуском задач по копированию, перемещению, удалению
 * файлов и созданию новых директорий
 * @author sp1d
 */
public class TaskExecutionService {
    private final ExecutorCompletionService ecs;
    

    public enum ErrorType {
        NOERROR, FILEEXISTS, DIRNOTEMPTY, IOERROR
    }

    public TaskExecutionService(HttpSession session) {
        ecs = new ExecutorCompletionService(Executors.newSingleThreadExecutor());        
    }

    public void addTask(Task task) {
        ecs.submit(newCallable(task));
    }
/*
 * Собирает и возвращает у ExecutorCompletionService список завершенных заданий
 * Вызывается соответствующим методом PollTasks в классе Tasks
 * 
 * 
 */
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
    
    /*
     * Фабрика объектов Callable
     */

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
    
    private Task setError(Task task, IOException exception) {
        if (exception instanceof FileAlreadyExistsException) {            
            task.setError(ErrorType.FILEEXISTS);
            return task;
        } else if (exception instanceof DirectoryNotEmptyException) {
            task.setError(ErrorType.DIRNOTEMPTY);
            return task;
        } else {
            task.setError(ErrorType.IOERROR);
            return task;
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
            } catch (IOException e) {
                task = setError(task, e);
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
            } catch (IOException e) {
                task = setError(task, e);
            }
            finally {
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
            } catch (IOException e) {
                task = setError(task, e);
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
            } catch (IOException e) {
                task = setError(task, e);
            } finally {
                return task;
            }
        }
    }
    
    

}
