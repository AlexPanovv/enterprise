package com.example.customer.controller;

import com.example.customer.dto.CustomerRequestDTO;
import com.example.customer.dto.CustomerResponseDTO;
import com.example.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Контроллер работает";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> addNewCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO result = customerService.createCustomer(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CustomerResponseDTO>> showAllCustomers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {

        Page<CustomerResponseDTO> page = customerService.getAllCustomers(pageable, firstName, lastName, email);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponseDTO> findCustomerById(@PathVariable Long customerId) {
        CustomerResponseDTO found = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(found);
    }

    @GetMapping("/by-email/{mail}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponseDTO> findCustomerByEmail(@PathVariable String mail) {
        CustomerResponseDTO found = customerService.getCustomerByEmail(mail);
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> changeCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO updated = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}