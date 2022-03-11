INSERT INTO duty(description, name, needs_specialty, price)
    VALUES ('Description1', 'Duty1', 'Surgery', 10),
           ('Description1', 'Duty2', 'Surgery', 20),
           ('Description1', 'Duty3', 'Surgery', 20);

INSERT INTO registration(date, is_active, client_id, doctor_id, duty_id)
    VALUES (CURRENT_DATE(), true, 1, 1, 1),
           (CURRENT_DATE(), true, 2, 1, 1),
           (CURRENT_DATE(), false, 3, 2, 2),
           (CURRENT_DATE(), false, 1, 2, 2),
           (CURRENT_DATE(), true, 2, 3, 3);
