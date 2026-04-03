package com.example.lab2.Controller;
//API v1 с пагинацией/фильтрацией
import com.example.lab2.Model.Customer;
import com.example.lab2.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
//@CrossOrigin(origins = "*") // для тестов из браузера/Postman
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // GET /api/v1/customers?page=0&size=10&sort=id,asc&firstName=Ivan&email=gmail
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email
    ) {
        try {
            // 1. Парсинг сортировки
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (sortParams.length > 1) {
                String dir = sortParams[1].trim().toLowerCase();
                if ("desc".equals(dir)) {
                    direction = Sort.Direction.DESC;
                }
            }

            // Проверка на существование поля (защита от SQL injection через сортировку)
            // В реальном проекте лучше сверять с whitelist полей
            Sort sorting = Sort.by(direction, sortBy);

            // 2. Создание Pageable
            var pageable = PageRequest.of(page, size, sorting);

            // 3. Вызов сервиса с фильтрами
            Page<Customer> result = customerService.findCustomers(firstName, lastName, email, pageable);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            // Ошибка сортировки (несуществующее поле)
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /api/customers/1
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerByIdOrThrow(id));
    }

    // POST /api/customers
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Customer>  createCustomer(@RequestBody Customer customer) {
        Customer saved = customerService.saveCustomer(customer);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/customers/1
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return ResponseEntity.ok(customerService.updateCustomerOrThrow(id, customerDetails));
    }

    // DELETE /api/customers/1
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

}