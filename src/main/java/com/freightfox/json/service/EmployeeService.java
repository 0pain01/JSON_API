package com.freightfox.json.service;

import com.freightfox.json.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final JdbcTemplate jdbcTemplate;

    public void insertRecord(String datasetName, Employee employee) {
        String table = sanitize(datasetName);

        // throwing error if name is empty
        if(employee.getName()==null || employee.getName().isBlank() || employee.getName().isEmpty() ){
            throw new IllegalArgumentException("Name is required and cannot be blank");
        }

        //throwing error if age is null
        if (employee.getAge() == null) {
            throw new IllegalArgumentException("Age is required and cannot be null");
        }

        //throwing error if department is empty
        if(employee.getDepartment()==null || employee.getDepartment().isBlank() || employee.getDepartment().isEmpty() ){
            throw new IllegalArgumentException("Department is required and cannot be blank");
        }

        //will create the dataset(table) if not present
        createTableIfNotExists(table);

        //using sql command to insert data to specific dataset
        String sql = "INSERT INTO " + table + " (id, name, age, department) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, employee.getId(), employee.getName(), employee.getAge(), employee.getDepartment());
        } catch (DuplicateKeyException e) {

            //throwing error if the id provided is already in the mentioned dataset
            throw new IllegalArgumentException(
                    "Record with ID " + employee.getId() + " already exists in dataset '" + datasetName + "'"
            );
        }}


    //service to group the result
    public Map<String, List<Employee>> groupBy(String datasetName, String groupByField) {
        String table = sanitize(datasetName);

        //throwing error if field for grouping is wrong/misspelled
        if (!List.of("department", "age").contains(groupByField.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported groupBy field");
        }

        String sql = "SELECT id, name, age, department FROM " + table;
        List<Employee> employees = jdbcTemplate.query(sql, employeeRowMapper());

        //grouping results in the basis of "department" or "age" else throw the error for wrong field-name
        return switch (groupByField.toLowerCase()) {
            case "department" -> employees.stream().collect(Collectors.groupingBy(Employee::getDepartment));
            case "age" -> employees.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getAge())));
            default -> throw new IllegalArgumentException("Unsupported groupBy field");
        };
    }
    private RowMapper<Employee> employeeRowMapper() {
        return (RowMapper<Employee>) (rs, rowNum) -> {
            Employee e = new Employee();
            e.setId(rs.getLong("id"));
            e.setName(rs.getString("name"));
            e.setAge(rs.getInt("age"));
            e.setDepartment(rs.getString("department"));
            return e;
        };
    }

    //service to sort the result
    public List<Employee> sortBy(String datasetName, String field, String order) {
        String table = sanitize(datasetName);

        //throwing error if sortBy field is wrongly provided
        if (!List.of("age", "name", "department").contains(field.toLowerCase()))
            throw new IllegalArgumentException("Unsupported sortBy field or field provided does not exist");

        //throwing error if order type is wrongly entered
        if (!List.of("asc", "desc").contains(order.toLowerCase()))
            throw new IllegalArgumentException("Unsupported order type (asc/desc)");

        String sql = String.format("SELECT id, name, age, department FROM %s ORDER BY %s %s", table, field, order);
        return jdbcTemplate.query(sql, employeeRowMapper());
    }

    //create table template as per instructions
    private void createTableIfNotExists(String table) {
        String createSql = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                "id BIGINT PRIMARY KEY," +
                "name VARCHAR(100)," +
                "age INT," +
                "department VARCHAR(50))";
        jdbcTemplate.execute(createSql);
    }

    private String sanitize(String input) {
        if (!input.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid dataset name");
        }
        return input;
    }
}
