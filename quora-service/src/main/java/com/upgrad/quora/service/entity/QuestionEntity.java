package com.upgrad.quora.service.entity;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name="question")

@NamedQueries(
        {
                @NamedQuery(name = "getAllQuestions", query = "select q from QuestionEntity"),
                @NamedQuery(name = "questionById", query = "select q from QuestionEntity q where q.uuid = :uuid"),
               // @NamedQuery(name = "questionByUserId", query = "select q from QuestionEntity q where u.uuid = :uuid"),
                @NamedQuery(name = "deleteQuestionById", query="delete from QuestionEntity q where q.uuid = :uuid"),
        }
)
public class QuestionEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Size(max = 200)
    private String uuid;

    @Column(name = "content")
    @NotNull
    @Size(max = 500)
    private String content;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }
}