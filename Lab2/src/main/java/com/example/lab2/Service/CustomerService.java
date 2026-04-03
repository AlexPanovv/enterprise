package com.example.lab2.Service;
//бизнес-логика + кэширование

import com.example.lab2.Model.Customer;
import com.example.lab2.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;


  //ГЛАВНЫЙ МЕТОД: Фильтрация + Пагинация + Сортировка + Кэш
    @Cacheable(
            value = "allCustomers",
            key = "'filters_fn-' + #firstName + '_ln-' + #lastName + '_em-' + #email + '_p-' + #pageable.pageNumber + '_s-' + #pageable.pageSize + '_sort-' + #pageable.sort.toString()"
    )
    public Page<Customer> findCustomers(String firstName, String lastName, String email, Pageable pageable) {
        System.out.println(">>> HIT DB: Filtering...");

        // ВАРИАНТ 1: Самый надежный - начинаем с предиката "true" (cb.conjunction())
        Specification<Customer> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(firstName)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%")
            );
        }
        if (StringUtils.hasText(lastName)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%")
            );
        }
        if (StringUtils.hasText(email)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%")
            );
        }

        return customerRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "customers", key = "#id")
    public Optional<Customer> getCustomerById(Long id) {
        System.out.println("ЗАПРОС В БАЗУ ДАННЫХ (КЭШ НЕ СРАБОТАЛ)");
        return customerRepository.findById(id);
    }

    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public Optional<Customer> updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    // Обновляем поля
                    if (customerDetails.getFirstName() != null && !customerDetails.getFirstName().isBlank()) {
                        existingCustomer.setFirstName(customerDetails.getFirstName());
                    }
                    if (customerDetails.getLastName() != null && !customerDetails.getLastName().isBlank()) {
                        existingCustomer.setLastName(customerDetails.getLastName());
                    }
                    if (customerDetails.getEmail() != null && !customerDetails.getEmail().isBlank()) {
                        existingCustomer.setEmail(customerDetails.getEmail());
                    }
                    return customerRepository.save(existingCustomer);
                });

    }

    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public boolean deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            // Выбрасываем исключение вместо возврата false
            throw new RuntimeException("Customer with ID " + id + " not found");
        }
        customerRepository.deleteById(id);
        return true;
    }

    @Cacheable(value = "customers", key = "#id")
    public Customer getCustomerByIdOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found"));
    }

    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public Customer updateCustomerOrThrow(Long id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    if (customerDetails.getFirstName() != null && !customerDetails.getFirstName().isBlank()) {
                        existingCustomer.setFirstName(customerDetails.getFirstName());
                    }
                    if (customerDetails.getLastName() != null && !customerDetails.getLastName().isBlank()) {
                        existingCustomer.setLastName(customerDetails.getLastName());
                    }
                    if (customerDetails.getEmail() != null && !customerDetails.getEmail().isBlank()) {
                        existingCustomer.setEmail(customerDetails.getEmail());
                    }


                    return customerRepository.save(existingCustomer);
                })
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found"));
    }

}