package com.abranlezama.ecommerceservice.repository;

import com.abranlezama.ecommerceservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByCustomer_User_Id(long id);
}
