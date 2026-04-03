package com.example.customer.service;

import com.example.customer.dto.CustomerRequestDTO;
import com.example.customer.dto.CustomerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);

    CustomerResponseDTO getCustomerById(Long id);

    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable, String firstName, String lastName, String email);

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO);

    void deleteCustomer(Long id);

    List<CustomerResponseDTO> searchCustomers(String searchWord);

    CustomerResponseDTO getCustomerByEmail(String email);
}