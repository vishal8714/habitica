package com.xarrier.databaseapp.Controllers;


//import org.springframework.stereotype.Controller;
import com.xarrier.databaseapp.Entities.Employee;
import com.xarrier.databaseapp.Repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// added comment

@RestController
public class HomeController {

    @Autowired
    public EmployeeRepository empRepo;


    @GetMapping("/")
    public String getHome(){



        return "Hello world Home page";
    }
// man nhi hai

    @PostMapping("/addemp")
    public ResponseEntity<String> addEMP(@RequestBody Employee emp){
        empRepo.save(emp);
        return ResponseEntity.ok().body("saved");

    }
}
