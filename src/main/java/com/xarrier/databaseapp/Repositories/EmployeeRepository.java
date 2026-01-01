package com.xarrier.databaseapp.Repositories;

import com.xarrier.databaseapp.Entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
