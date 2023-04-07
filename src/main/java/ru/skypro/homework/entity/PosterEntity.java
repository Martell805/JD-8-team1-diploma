package ru.skypro.homework.entity;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
@Table(name = "posters")
public class PosterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name="ads_id")
    private AdsEntity ads;

    @Column(name = "path")
    @NotNull
    private String path;

    public PosterEntity(String path) {
        this.path = path;
    }
}
