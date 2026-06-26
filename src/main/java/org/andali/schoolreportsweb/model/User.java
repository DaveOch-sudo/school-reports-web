package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.model.enums.UserRole;

@Entity
@Table(name = "app_user",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /** Null for SUPER_ADMIN. Required for SCHOOL_ADMIN and TEACHER. */
    @ManyToOne
    private School school;

    /** Null unless role is TEACHER — scopes mark entry to this class */
    @ManyToOne
    private SchoolClass assignedClass;
}
