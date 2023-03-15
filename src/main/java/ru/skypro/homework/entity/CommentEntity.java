package ru.skypro.homework.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;


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
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name="ads_id")
    private AdsEntity ads;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentEntity)) return false;
        CommentEntity that = (CommentEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text) && Objects.equals(createdAt, that.createdAt) && Objects.equals(ads, that.ads) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt, ads, author);
    }
}
