package com.example.lab1.Service;

import com.example.lab1.Model.Customer;
import com.example.lab1.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

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

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}