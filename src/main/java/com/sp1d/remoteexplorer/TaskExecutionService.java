package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.json.Task;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  Занимается непосредственно запуском задач по копированию, перемещению, удалению
 * файлов и созданию новых директорий
 * @author sp1d
 */
public class TaskExecutionService {
    private final ExecutorCompletionService ecs;
    private final static Logger LOG = LogManager.getLogger(TaskExecutionService.class);
    

    public enum ErrorType {
        NOERROR, FILEEXISTS, DIRNOTEMPTY, IOERROR
    }

    public TaskExecutionService(HttpSession session) {
        LOG.debug("New instance initialized");
        ecs = new ExecutorCompletionService(Executors.newSingleThreadExecutor());        
    }

    public void addTask(Task task) {
        LOG.debug("New task added for execution, {} from {} to {}",task.getType(),
                task.getFrom(), task.getTo());
//        ecs.submit(newCallable(task));
        ecs.submit(new taskCallable(task));
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
            LOG.debug("Gathering tasks job is interrupted", e);
        }
        return tasks;
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
    
    class taskCallable implements Callable<Task> {

        private Task task;

        taskCallable(Task task) {
            this.task = task;
        }

        @Override
        public Task call() throws Exception {
            try {
                switch (task.getType()) {
                    case COPY : Files.copy(task.getFrom(), task.getTo());
                        break;
                    case MOVE : Files.move(task.getFrom(), task.getTo());
                        break;
                    case DELETE : Files.delete(task.getTo());
                        break;
                    case CREATE : Files.createDirectory(task.getTo());
                        break;
                }
                task.setError(ErrorType.NOERROR);
            } catch (IOException e) {
                task = setError(task, e);
            } finally {
                return task;
            }
        }
    }
    
}
