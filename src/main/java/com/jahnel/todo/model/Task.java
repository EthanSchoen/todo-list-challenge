package com.jahnel.todo.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long taskId;

    private String task;

    private boolean complete;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TaskList list;

    public Task() {
    }

    public Task(Long id, String task, boolean complete) {
        this.taskId = id;
        this.task = task;
        this.complete = complete;
    }

    public Task(String task, boolean complete, TaskList list) {
        this.task = task;
        this.complete = complete;
        this.list = list;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long id) {
        this.taskId = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public TaskList getTaskList() {
        return list;
    }

    public void setTaskList(TaskList l) {
        this.list = l;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.taskId);
        hash = 79 * hash + Objects.hashCode(this.task);
        hash = 79 * hash + (this.complete ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (this.complete != other.complete) {
            return false;
        }
        if (!Objects.equals(this.task, other.task)) {
            return false;
        }
        return Objects.equals(this.taskId, other.taskId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("taskId=").append(taskId);
        sb.append(", task='").append(task).append('\'');
        sb.append(", complete=").append(complete);
        sb.append('}');
        return sb.toString();
    }
}