package ru.skypro.homework.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name="adsId")
    private AdsEntity ads;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private UserEntity author;
}
