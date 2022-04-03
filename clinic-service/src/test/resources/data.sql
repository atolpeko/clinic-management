INSERT INTO facility (name)
    VALUES ('Beds'), ('Bladder Scanners'), ('Body Composition Analyzers'), ('Centrifuges');

INSERT INTO department (city, country, house_number, state, street)
    VALUES ('New York City', 'USA', 12, 'New York', '24'),
           ('Los Angeles', 'USA', 14, 'California', '30');

INSERT INTO department_facility (department_id, facility_id)
    VALUES (1, 1), (1, 2), (1, 4), (2, 1), (2, 3);

INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex,
                     practice_beginning_date, specialty)
    VALUES ('DOCTOR', 'email@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery'),
           ('DOCTOR', 'email2@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander2', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery'),
           ('DOCTOR', 'email3@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander3', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery');

INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex)
    VALUES ('TEAM_MANAGER', 'email4@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander4', '12345678', 1000, 'MALE'),
           ('TEAM_MANAGER', 'email5@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander5', '12345678', 1000, 'MALE');


INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex)
    VALUES ('TOP_MANAGER', 'email6@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander4', '12345678', 1000, 'MALE'),
           ('TOP_MANAGER', 'email7@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander5', '12345678', 1000, 'MALE');

INSERT INTO department_employee(department_id, employee_id)
    VALUES (1, 1), (1, 2), (2, 3), (1 ,4), (1, 5);
