package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String address;

    private String phone;

    @Column(unique = true)
    private String email;

    private String Emis_code;

    private String motto;

    private String logoUrl;

    private String registrationNumber;
}
