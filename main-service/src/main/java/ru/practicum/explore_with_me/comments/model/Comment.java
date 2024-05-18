package ru.practicum.explore_with_me.comments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 512)
    private String text;
    @ManyToOne
    @JoinColumn(nullable = false, name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(nullable = false, name = "event_id")
    private Event event;
    @Column(nullable = false, name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "edit_time")
    private LocalDateTime editTime;
}
