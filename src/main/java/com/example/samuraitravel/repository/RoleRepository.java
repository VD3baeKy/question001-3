package com.example.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Role;

//public class RoleRepository {
public interface RoleRepository extends JpaRepository<Role, Integer> {
	public Role findByName(String name);
}
