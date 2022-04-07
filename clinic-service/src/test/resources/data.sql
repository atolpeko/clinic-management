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
    VALUES ('DOCTOR', 'alex@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery'),
           ('DOCTOR', 'robert@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Robert', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery'),
           ('DOCTOR', 'mark@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Mark', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery'),
           ('DOCTOR', 'bob@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Bob', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery'),
           ('DOCTOR', 'thomas@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Thomas', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery');

INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex)
    VALUES ('TEAM_MANAGER', 'oliver@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Oliver', '12345678', 1000, 'MALE'),
           ('TEAM_MANAGER', 'lucas@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Lucas', '12345678', 1000, 'MALE'),
           ('TEAM_MANAGER', 'emma@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Emma', '12345678', 1000, 'FEMALE');


INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex)
    VALUES ('TOP_MANAGER', 'evelyn@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Evelyn', '12345678', 1000, 'FEMALE'),
           ('TOP_MANAGER', 'william@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'William', '12345678', 1000, 'MALE'),
           ('TOP_MANAGER', 'james@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'James', '12345678', 1000, 'MALE');

INSERT INTO department_employee(department_id, employee_id)
    VALUES (1, 1), (1, 2), (2, 3), (1 ,4), (2, 5), (2, 6), (2, 7), (1, 8);
