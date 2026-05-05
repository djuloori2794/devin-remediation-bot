package com.kd.devinbot.controller;

import com.kd.devinbot.task.TaskRecord;
import com.kd.devinbot.task.TaskStore;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

  private final TaskStore taskStore;

  public TaskController(TaskStore taskStore) {
    this.taskStore = taskStore;
  }

  @GetMapping("/tasks")
  public Collection<TaskRecord> tasks() {
    return taskStore.allTasks();
  }

}