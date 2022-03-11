INSERT INTO facility (name)
    VALUES ('Beds'), ('Bladder Scanners'), ('Body Composition Analyzers'), ('Centrifuges');

INSERT INTO department (city, country, house_number, state, street)
    VALUES ('New York City', 'USA', 12, 'New York', '24'),
           ('Los Angeles', 'USA', 14, 'California', '30');

INSERT INTO department_facility (department_id, facility_id)
    VALUES (1, 1), (1, 2), (1, 4), (2, 1), (2, 3);

INSERT INTO doctor (name, specialty, practice_beginning_date, department_id)
    VALUES ('Alexander', 'Surgery', CURRENT_DATE(), 1),
           ('Dmitry', 'Surgery', CURRENT_DATE(), 2),
           ('Robert', 'Surgery', CURRENT_DATE(), 2);
