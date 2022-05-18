package com.dalibormucak.im.springrestapi.repositories;

import com.dalibormucak.im.springrestapi.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
