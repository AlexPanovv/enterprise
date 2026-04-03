package com.example.customer.service.impl;

import com.example.customer.dto.CustomerRequestDTO;
import com.example.customer.dto.CustomerResponseDTO;
import com.example.customer.entity.Customer;
import com.example.customer.jms.NotificationProducer;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.repository.CustomerSpecification;
import com.example.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepo;
    private final NotificationProducer notificationProducer;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepo, NotificationProducer notificationProducer) {
        this.customerRepo = customerRepo;
        this.notificationProducer = notificationProducer;
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        log.info("Создание нового клиента: {}", dto.getEmail());

        if (customerRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован: " + dto.getEmail());
        }

        Customer newCustomer = new Customer(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail()
        );

        Customer saved = customerRepo.save(newCustomer);
        log.info("Клиент сохранён в базе данных с ID: {}", saved.getId());

        notificationProducer.sendWelcomeEmail(saved.getId(), saved.getEmail(), saved.getFirstName());

        return convertToDTO(saved);
    }

    @Override
    @Cacheable(value = "customers", key = "#id")
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент с id " + id + " не найден в базе"));
        return convertToDTO(customer);
    }

    @Override
    @Cacheable(value = "allCustomers", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #firstName + '_' + #lastName + '_' + #email")
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable, String firstName, String lastName, String email) {
        return customerRepo.findAll(CustomerSpecification.filterBy(firstName, lastName, email), pageable)
                .map(this::convertToDTO);
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer existing = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Не могу обновить - клиент с id " + id + " не существует"));

        if (!existing.getEmail().equals(dto.getEmail()) &&
                customerRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Этот email " + dto.getEmail() + " уже используется другим клиентом");
        }

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());

        Customer updated = customerRepo.save(existing);
        return convertToDTO(updated);
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public void deleteCustomer(Long id) {
        if (!customerRepo.existsById(id)) {
            throw new RuntimeException("Не могу удалить - клиент с id " + id + " не найден");
        }
        customerRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchCustomers(String searchWord) {
        return customerRepo.searchByName(searchWord)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "customers", key = "#email")
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Клиент с email " + email + " отсутствует в системе"));
        return convertToDTO(customer);
    }

    private CustomerResponseDTO convertToDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}
