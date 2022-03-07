INSERT INTO facility(name) VALUES('Beds');
INSERT INTO facility(name) VALUES('Bladder Scanners');
INSERT INTO facility(name) VALUES('Body Composition Analyzers');
INSERT INTO facility(name) VALUES('Centrifuges');

INSERT INTO department(city, country, house_number, state, street)
    VALUES('New York City', 'USA', 12, 'New York', '24');
INSERT INTO department(city, country, house_number, state, street)
    VALUES('Los Angeles', 'USA', 14, 'California', '30');

INSERT INTO department_facility(department_id, facility_id) VALUES(1, 1);
INSERT INTO department_facility(department_id, facility_id) VALUES(1, 2);
INSERT INTO department_facility(department_id, facility_id) VALUES(1, 4);
INSERT INTO department_facility(department_id, facility_id) VALUES(2, 1);
INSERT INTO department_facility(department_id, facility_id) VALUES(2, 3);

INSERT INTO doctor(name, specialty, practice_beginning_date, department_id)
    VALUES('Alexander', 'Surgery', CURRENT_DATE(), 1);
INSERT INTO doctor(name, specialty, practice_beginning_date, department_id)
    VALUES('Dmitry', 'Surgery', CURRENT_DATE(), 2);
INSERT INTO doctor(name, specialty, practice_beginning_date, department_id)
    VALUES('Robert', 'Surgery', CURRENT_DATE(), 2);
