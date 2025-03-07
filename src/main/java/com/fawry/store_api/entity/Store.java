package com.fawry.store_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "stores")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id", nullable = false)
    private Long id;

    @Column(name = "store_name", nullable = false, unique = true)
    @NotBlank(message = "Store name is mandatory")
    private String name;

    @Column(name = "store_address", nullable = false)
    @NotBlank(message = "Store address is mandatory")
    private String address;
}
