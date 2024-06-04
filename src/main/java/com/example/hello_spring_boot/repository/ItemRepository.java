package com.example.hello_spring_boot.repository;

import com.example.hello_spring_boot.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}