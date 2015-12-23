
package com.sp1d.remoteexplorer.json;

import com.google.gson.Gson;
import com.sp1d.remoteexplorer.AppService;
import com.sp1d.remoteexplorer.TaskExecutionService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.http.HttpSession;

/**
 * Обслуживает задачи Task, а также содержит списки задач выполняемых 
 * (Когда TaskType задачи еще не задан), выполненных,
 * ошибочных (где ErrorType отличается от NOERROR)
 * 
 * Также служит для формирования объекта JSON
 * 
 * @author sp1d
 */
public class Tasks {

    int count = 0;
    List<Task> tasks;
    BlockingQueue<Task> finished;
    Set<Task> errors;

    private final transient TaskExecutionService tes;
    private final transient Gson gson;

    public enum TaskType {

        COPY, MOVE, DELETE, CREATE
    }
    
    public Tasks(HttpSession session) {
        this.tasks = new CopyOnWriteArrayList<Task>();
        this.finished = new ArrayBlockingQueue<>(5);        
        this.errors = new CopyOnWriteArraySet<Task>();
        gson = new Gson();
        tes = AppService.inst(session, TaskExecutionService.class);        
    }

    public void addTask(Task task) {
        tasks.add(task);
        count = tasks.size();

//        TaskExecutionService will be working on this task;
        tes.addTask(task);
    }

    public void pollTasks() {
        errors.clear();
        
        for (Task task : tes.pollTasks()) {
            tasks.remove(task);            
            if (task.getError() != TaskExecutionService.ErrorType.NOERROR) {
                errors.add(task);
            } else {
                if(!finished.offer(task)) {
                    finished.poll();
                    finished.offer(task);
                }
            }
        } 
        count = tasks.size();
    }
    
    public String getJSON() {
        pollTasks();
        return gson.toJson(this);
    }

}
