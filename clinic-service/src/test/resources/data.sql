INSERT INTO facility (name)
    VALUES ('Beds'), ('Bladder Scanners'), ('Body Composition Analyzers'), ('Centrifuges');

INSERT INTO department (city, country, house_number, state, street)
    VALUES ('New York City', 'USA', 12, 'New York', '24'),
           ('Los Angeles', 'USA', 14, 'California', '30'),
           ('Los Angeles', 'USA', 22, 'California', '11');

INSERT INTO department_facility (department_id, facility_id)
    VALUES (1, 1), (1, 2), (1, 4), (2, 1), (2, 3);
