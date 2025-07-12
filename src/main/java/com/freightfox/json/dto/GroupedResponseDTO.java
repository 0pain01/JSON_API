package com.freightfox.json.dto;


import com.freightfox.json.model.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class GroupedResponseDTO {
    private Map<String, List<Employee>> groupedRecords;
}
