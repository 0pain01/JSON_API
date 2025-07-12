package com.freightfox.json.controller;

import com.freightfox.json.dto.GroupedResponseDTO;
import com.freightfox.json.dto.InsertResponseDTO;
import com.freightfox.json.dto.SortedResponseDTO;
import com.freightfox.json.model.Employee;
import com.freightfox.json.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dataset/{datasetName}")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //retrieving dataset name to enter data to that dataset(table)
    @PostMapping("/record")
    public ResponseEntity<InsertResponseDTO> addRecord(@PathVariable String datasetName, @RequestBody Employee employee) {
        employeeService.insertRecord(datasetName, employee);
        return ResponseEntity.ok(new InsertResponseDTO("Record added successfully", datasetName, employee.getId()));
    }

    //query groupBy or sortBy on the basis of parameter provided
    @GetMapping("/query")
    public ResponseEntity<?> query(@PathVariable String datasetName,
                                   @RequestParam Optional<String> groupBy,
                                   @RequestParam Optional<String> sortBy,
                                   @RequestParam(defaultValue = "asc") String order) {
        if (groupBy.isPresent()) {
            Map<String, List<Employee>> grouped = employeeService.groupBy(datasetName, groupBy.get());
            return ResponseEntity.ok(new GroupedResponseDTO(grouped));
        } else if (sortBy.isPresent()) {
            List<Employee> sorted = employeeService.sortBy(datasetName, sortBy.get(), order);
            return ResponseEntity.ok(new SortedResponseDTO(sorted));
        }
        return ResponseEntity.badRequest().body("Missing groupBy or sortBy parameter");
    }


}
