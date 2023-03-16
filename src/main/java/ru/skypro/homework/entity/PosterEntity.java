package ru.skypro.homework.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posters")
public class PosterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name="ads_id")
    private AdsEntity ads;

    private String path;

    public PosterEntity(String path) {
        this.path = path;
    }
}
