/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author sp1d
 */
public class Tasks {

    int count = 0;
    List<Task> tasks;
    BlockingQueue<Task> finished;
    Set<Task> errors;

    private final transient TaskExecutionService tes;
    private final transient HttpSession session;
    private final transient Gson gson;

    public enum TaskType {

        COPY, MOVE, DELETE, CREATE
    }
    
    public Tasks(HttpSession session) {
        this.tasks = new CopyOnWriteArrayList<Task>();
        this.finished = new ArrayBlockingQueue<>(5);        
        this.errors = new CopyOnWriteArraySet<Task>();
        this.session = session;
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
