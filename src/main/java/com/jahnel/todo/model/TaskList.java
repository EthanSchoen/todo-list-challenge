package com.jahnel.todo.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "lists")
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long listId;

    private String name;

    private String user;

    public TaskList(){

    }

    public TaskList( String nm, String usr ){
        this.name = nm;
        this.user = usr;
    }

    public TaskList( Long id, String nm, String usr ){
        this.listId = id;
        this.name = nm;
        this.user = usr;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long id) {
        this.listId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nm) {
        this.name = nm;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String usr) {
        this.user = usr;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.listId);
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.user);
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
        final TaskList other = (TaskList) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.listId, other.listId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("List{");
        sb.append("listId=").append(listId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", user=").append(user);
        sb.append('}');
        return sb.toString();
    }
}