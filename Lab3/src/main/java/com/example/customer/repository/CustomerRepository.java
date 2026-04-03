package com.example.customer.repository;

import com.example.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Customer> findByLastName(String lastName);

    @Query("SELECT c FROM Customer c WHERE c.firstName LIKE %:searchTerm% OR c.lastName LIKE %:searchTerm%")
    List<Customer> searchByName(@Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Customer c WHERE c.createdAt > :date")
    List<Customer> findCreatedAfter(@Param("date") LocalDateTime date);
}