package com.xarrier.databaseapp.Repositories;

import com.xarrier.databaseapp.Entities.Concession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcessionRepository extends JpaRepository<Concession, Long> {
}
