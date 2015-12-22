/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class TasksJSON {

    int count = 0;
    List<Task> tasks;
    Set<Task> finished;
    Set<Task> errors;

    private final transient TaskExecutionService tes;
    private final transient HttpSession session;

    public TasksJSON(HttpSession session) {
        this.tasks = new CopyOnWriteArrayList<Task>();
        this.finished = new CopyOnWriteArraySet<Task>();
        this.session = session;
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
                finished.add(task);
            }
        }        
    }
    
    public String getJSON() {
        pollTasks();
        return AppService.gson.toJson(this);
    }

}
