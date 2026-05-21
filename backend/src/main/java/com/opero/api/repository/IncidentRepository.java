package com.opero.api.repository;

// Esta es la línea corregida:
import com.opero.api.entity.Incident; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
}