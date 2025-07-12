package com.freightfox.json.dto;

import com.freightfox.json.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SortedResponseDTO {
    private List<Employee> sortedRecords;
}
