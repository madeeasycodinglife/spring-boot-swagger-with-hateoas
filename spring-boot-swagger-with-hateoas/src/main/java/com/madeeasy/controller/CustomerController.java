package com.madeeasy.controller;

import com.madeeasy.entity.Customer;
import com.madeeasy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
//@EnableHypermediaSupport(type = HAL)
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @GetMapping("/get-all-hateoas-links/{id}")
    public ResponseEntity<?> getHateoasLinks(@PathVariable("id") Long id){
        return customerService.getHateoasLinks(id);
    }
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerByIdControllerClassMethod(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }
    @GetMapping
    public ResponseEntity<CollectionModel<Customer>> getAllCustomersControllerClassMethod() {
        ResponseEntity<CollectionModel<Customer>> allCustomers = customerService.getAllCustomers();
        return ResponseEntity.ok(allCustomers).getBody();
    }   
    @GetMapping("/all")
    public  ResponseEntity<List<EntityModel<Customer>>> getAllCustomersControllerClassMethodSecondType() {
        ResponseEntity<List<EntityModel<Customer>>> allCustomersSecondType = customerService.getAllCustomersSecondType();
        return ResponseEntity.ok(allCustomersSecondType).getBody();
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer) {
        customer.setId(customerId);
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
