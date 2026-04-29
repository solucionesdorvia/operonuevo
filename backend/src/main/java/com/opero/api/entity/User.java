package com.opero.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true) // UK en el DER
    private String emailUade;

    @Column(nullable = false)
    private String passwordHash;

    // Relación: Muchos usuarios pueden tener un mismo rol
    // EAGER porque el filtro JWT siempre necesita el role para autenticación
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Relación: Muchos usuarios pertenecen a un departamento (nullable para alumnos/profesores)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Relación bidireccional: Incidentes reportados por este usuario
    // mappedBy indica que el lado "dueño" está en Incident.reporter
    // Útil para: GET /api/incidents?reporterId={USER_ID}
    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY)
    private List<Incident> reportedIncidents = new ArrayList<>();

    // Relación bidireccional: Incidentes asignados a este usuario (como trabajador)
    // mappedBy indica que el lado "dueño" está en Incident.worker
    // Útil para: GET /api/incidents?assignedTo={USER_ID}
    @OneToMany(mappedBy = "worker", fetch = FetchType.LAZY)
    private List<Incident> assignedIncidents = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Métodos para gestionar automáticamente las fechas de auditoría
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public User() {
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailUade() {
        return emailUade;
    }

    public void setEmailUade(String emailUade) {
        this.emailUade = emailUade;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Incident> getReportedIncidents() {
        return reportedIncidents;
    }

    public void setReportedIncidents(List<Incident> reportedIncidents) {
        this.reportedIncidents = reportedIncidents;
    }

    public List<Incident> getAssignedIncidents() {
        return assignedIncidents;
    }

    public void setAssignedIncidents(List<Incident> assignedIncidents) {
        this.assignedIncidents = assignedIncidents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}