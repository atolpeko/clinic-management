INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex,
                     practice_beginning_date, specialty, department_id)
    VALUES ('DOCTOR', 'alex@gmail.com', true, 'dmfd33kjfm4', 'NYC', 'USA', '11',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Alexander', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery', 1),
           ('DOCTOR', 'robert@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Robert', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery', 1),
           ('DOCTOR', 'mark@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Mark', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery', 2),
           ('DOCTOR', 'bob@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Bob', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery', 2),
           ('DOCTOR', 'thomas@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Thomas', '12345678', 1000, 'MALE',
            CURRENT_DATE(), 'Surgery', 1);

INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex, department_id)
    VALUES ('TEAM_MANAGER', 'oliver@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Oliver', '12345678', 1000, 'MALE', 1),
           ('TEAM_MANAGER', 'lucas@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Lucas', '12345678', 1000, 'MALE', 2),
           ('TEAM_MANAGER', 'emma@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Emma', '12345678', 1000, 'FEMALE', 2),
           ('TEAM_MANAGER', 'olivia@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Olivia', '12345678', 1000, 'FEMALE', 1);


INSERT INTO employee(role, email, is_enabled, password, city, country, house_number,
                     state, street, date_of_birth, hire_date, name, phone, salary, sex, department_id)
    VALUES ('TOP_MANAGER', 'evelyn@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'Evelyn', '12345678', 1000, 'FEMALE', null),
           ('TOP_MANAGER', 'william@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'William', '12345678', 1000, 'MALE', null),
           ('TOP_MANAGER', 'james@gmail.com', true, '12345678', 'NYC', 'USA', '12345678',
            'NY', '23', CURRENT_DATE(), CURRENT_DATE(), 'James', '12345678', 1000, 'MALE', null);
