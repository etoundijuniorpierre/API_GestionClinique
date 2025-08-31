package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByRoleType(RoleType roleType);

    @Query("SELECT r FROM Role r WHERE r.roleType = :roleType")
    Optional<Role> findFirstByRoleType(@Param("roleType") RoleType roleType);

}
