package com.madeeasy.service;

import com.madeeasy.controller.CustomerController;
import com.madeeasy.entity.Customer;
import com.madeeasy.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED,
        readOnly = true)
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            readOnly = false)
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            readOnly = false)
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public ResponseEntity<CollectionModel<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        customers.forEach(customer -> {
            customer.add(linkTo(methodOn(CustomerController.class)
                    .getAllCustomersControllerClassMethod())
                    .withRel("getAllCustomers"));

        });
        Link allLinks = linkTo(methodOn(CustomerController.class).getAllCustomersControllerClassMethod()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(customers, allLinks));
    }

    public ResponseEntity<?> getHateoasLinks(Long id) {
        return customerRepository.findById(id)
                .map(customerMap -> {
                    customerMap.add(linkTo(methodOn(CustomerController.class)
                            .getCustomerByIdControllerClassMethod(id))
                            .withRel("getCustomerById"));
                    customerMap.add(linkTo(methodOn(CustomerController.class)
                            .getAllCustomersControllerClassMethod())
                            .withRel("getAllCustomers"));
                    return ResponseEntity.ok(customerMap);
                })
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));

        //-----------------------------------
        // OR
        //-----------------------------------
       /* Customer customer = customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));;
        EntityModel<Customer> customerModel = EntityModel.of(customer);
        try {

            Link getCustomerByIdLink = linkTo(methodOn(CustomerController.class)
                    .getCustomerByIdControllerClassMethod(id))
                    .withRel("getCustomerById");
            customerModel.add(getCustomerByIdLink);
            Link getAllCustomersLink = linkTo(methodOn(CustomerController.class)
                    .getAllCustomersControllerClassMethod())
                    .withRel("getAllCustomers");

            customerModel.add(getAllCustomersLink);
              //-----------------------------------
            System.out.println(customerModel);
            //-----------------------------------
            return new ResponseEntity<>(customerModel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(customerModel, HttpStatus.NOT_FOUND);
        }*/
    }

    public ResponseEntity<List<EntityModel<Customer>>> getAllCustomersSecondType() {
        List<Customer> customers = customerRepository.findAll();
        List<EntityModel<Customer>> listOfCustomerModel = new ArrayList<>();

        for (Customer customer : customers) {
            EntityModel<Customer> customerModel = EntityModel.of(customer);
            customerModel.add(linkTo(methodOn(CustomerController.class)
                    .getCustomerByIdControllerClassMethod(customer.getId())).withSelfRel());
            listOfCustomerModel.add(customerModel);
        }

        return new ResponseEntity<>(listOfCustomerModel, HttpStatus.OK);
    }
}
