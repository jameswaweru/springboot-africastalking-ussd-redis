package com.jameswaweru.springbootussd.repositories;


import com.jameswaweru.springbootussd.data.UssdSession;
import org.springframework.data.repository.CrudRepository;

public interface UssdSessionRepository extends CrudRepository<UssdSession, String> {
}