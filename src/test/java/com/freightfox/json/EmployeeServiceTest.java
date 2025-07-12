package com.freightfox.json;

import com.freightfox.json.model.Employee;
import com.freightfox.json.service.EmployeeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeServiceTest {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    final String dataset = "test_dataset";

    //cleaning the test table everytime
    @BeforeEach
    void cleanup(){
        jdbcTemplate.execute("DROP TABLE IF EXISTS "+ dataset);
    }

    //testing when name not provided
    @Test
    void testMissingNameThrowsError() {
        Employee e = new Employee(1L, null, 25, "Engineering");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> employeeService.insertRecord(dataset, e));
        assertEquals("Name is required and cannot be blank", ex.getMessage());
    }

    @Test
    void testBlankNameThrowsError() {
        Employee e = new Employee(2L, "  ", 28, "Engineering");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> employeeService.insertRecord(dataset, e));
        assertEquals("Name is required and cannot be blank", ex.getMessage());
    }

    //testing when department not provided
    @Test
    void testMissingDepartmentThrowsError() {
        Employee e = new Employee(3L, "Jane", 30, null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> employeeService.insertRecord(dataset, e));
        assertEquals("Department is required and cannot be blank", ex.getMessage());
    }

    @Test
    void testBlankDepartmentThrowsError() {
        Employee e = new Employee(4L, "Jane", 30, "   ");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> employeeService.insertRecord(dataset, e));
        assertEquals("Department is required and cannot be blank", ex.getMessage());
    }

    //testing when age not provided
    @Test
    void testMissingAgeThrowsError() {
        Employee e = new Employee(5L, "Alice", null, "HR");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> employeeService.insertRecord(dataset, e));
        assertEquals("Age is required and cannot be null", ex.getMessage());
    }

    //testing to successfully entering the data
    @Test
    @Order(1)
    void testInsertRecordSuccess() {
        Employee e = new Employee(1L, "John", 30, "Engineering");
        assertDoesNotThrow(() -> employeeService.insertRecord(dataset, e));
    }

    //testing that duplicate id based data not entered
    @Test
    @Order(2)
    void testInsertDuplicateRecordThrows(){
        Employee e1 = new Employee(1L, "John", 30, "Engineering");
        Employee e2 = new Employee(1L, "Jane", 25, "Marketing");

        employeeService.insertRecord(dataset, e1);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> employeeService.insertRecord(dataset, e2));

        assertTrue(ex.getMessage().contains("already exists"));
    }

    //testing groupBy working successfully
    @Test
    @Order(3)
    void testGroupByDepartment() {
        employeeService.insertRecord(dataset, new Employee(1L, "John", 30, "Engineering"));
        employeeService.insertRecord(dataset, new Employee(2L, "Jane", 25, "Engineering"));
        employeeService.insertRecord(dataset, new Employee(3L, "Alice", 28, "Marketing"));

        Map<String, List<Employee>> grouped = employeeService.groupBy(dataset, "department");

        assertEquals(2, grouped.size());
        assertEquals(2, grouped.get("Engineering").size());
        assertEquals(1, grouped.get("Marketing").size());
    }

    //testing sorting working correctly
    @Test
    @Order(4)
    void testSortByAgeAsc() {
        employeeService.insertRecord(dataset, new Employee(1L, "John", 30, "Engineering"));
        employeeService.insertRecord(dataset, new Employee(2L, "Jane", 25, "Engineering"));
        employeeService.insertRecord(dataset, new Employee(3L, "Alice", 28, "Marketing"));

        List<Employee> sorted = employeeService.sortBy(dataset, "age", "asc");

        assertEquals(25, sorted.get(0).getAge());
        assertEquals(28, sorted.get(1).getAge());
        assertEquals(30, sorted.get(2).getAge());
    }


    //testing sort by age is working successfully
    @Test
    @Order(5)
    void testSortByAgeDesc() {
        employeeService.insertRecord(dataset, new Employee(1L, "John", 30, "Engineering"));
        employeeService.insertRecord(dataset, new Employee(2L, "Jane", 25, "Engineering"));
        employeeService.insertRecord(dataset, new Employee(3L, "Alice", 28, "Marketing"));

        List<Employee> sorted = employeeService.sortBy(dataset, "age", "desc");

        assertEquals(30, sorted.get(0).getAge());
        assertEquals(28, sorted.get(1).getAge());
        assertEquals(25, sorted.get(2).getAge());
    }


    //testing to check error on invalid field entered
    @Test
    void testInvalidGroupByField() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> employeeService.groupBy(dataset, "invalidField"));

        assertEquals("Unsupported groupBy field", ex.getMessage());
    }

    //testing to check invalid field entered during sorting
    @Test
    void testInvalidSortByField() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> employeeService.sortBy(dataset, "foo", "asc"));

        assertTrue(ex.getMessage().contains("Unsupported sortBy field"));
    }


    //testing to check invalid sort order parameters
    @Test
    void testInvalidSortOrder() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> employeeService.sortBy(dataset, "age", "invalid"));

        assertTrue(ex.getMessage().contains("Unsupported order type (asc/desc)"));
    }

}
