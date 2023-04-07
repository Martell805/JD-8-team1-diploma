package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ads")
public class AdsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "title")
    @NotNull
    private String title;
    @OneToOne
    @JoinColumn(name = "image_id")
    private PosterEntity image;
    @Column(name = "price")
    @PositiveOrZero
    @NotNull
    private Integer price;
    @Column(name = "description")
    @NotNull
    private String description;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @NotNull
    private UserEntity author;
    @OneToMany(mappedBy = "ads")
    @ToString.Exclude
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<CommentEntity> results;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdsEntity adsEntity = (AdsEntity) o;
        return getId() != null && Objects.equals(getId(), adsEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
