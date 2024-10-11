package com.restapi.vinted.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "storages_clothes",
            joinColumns = @JoinColumn(name = "storage_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "clothe_id", referencedColumnName = "id")
    )
    private Set<Clothe> clothes;

    @CreationTimestamp
    private ZonedDateTime addedAt;
}