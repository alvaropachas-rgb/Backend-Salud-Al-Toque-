INSERT INTO users
(name, email, password, phone, role)
VALUES
    (
        'Administrador',
        'admin@gmail.com',
        '$2a$10$kKDtQY9PGvFsjFwzv.QbxeJRon8s9mdTFuOC1mNfau.RyWqyMToMK',
        '999000000',
        'ADMIN'
    );

INSERT INTO specialties (name)
VALUES ('Cardiologia');

INSERT INTO specialties (name)
VALUES ('Dermatologia');

INSERT INTO specialties (name)
VALUES ('Pediatria');

INSERT INTO specialties (name)
VALUES ('Odontologia');

INSERT INTO users
(name, email, password, phone, role)
VALUES
    ('Carlos Perez', 'carlos@gmail.com', '$2a$10$kKDtQY9PGvFsjFwzv.QbxeJRon8s9mdTFuOC1mNfau.RyWqyMToMK', '999111222', 'PROFESSIONAL');

INSERT INTO users
(name, email, password, phone, role)
VALUES
    ('Maria Lopez', 'maria@gmail.com', '$2a$10$kKDtQY9PGvFsjFwzv.QbxeJRon8s9mdTFuOC1mNfau.RyWqyMToMK', '999222333', 'PROFESSIONAL');

INSERT INTO users
(name, email, password, phone, role)
VALUES
    ('Luis Ramirez', 'luis@gmail.com', '$2a$10$kKDtQY9PGvFsjFwzv.QbxeJRon8s9mdTFuOC1mNfau.RyWqyMToMK', '999444555', 'PATIENT');

INSERT INTO professionals
(location, rating, user_id, specialty_id)
VALUES
    ('Lima',4.5, 2, 1);

INSERT INTO professionals
(location, rating, user_id, specialty_id)
VALUES
    ('San Isidro', 4.1, 3, 2);


INSERT INTO medical_services
(name, description, price, professional_id)
VALUES (
           'Consulta cardiológica',
           'Consulta médica especializada en cardiología',
           80.0,
           1
       );

INSERT INTO medical_services
(name, description, price, professional_id)
VALUES (
           'Control cardiológico',
           'Control y seguimiento del paciente',
           60.0,
           1
       );

INSERT INTO medical_services
(name, description, price, professional_id)
VALUES (
           'Consulta dermatológica',
           'Consulta especializada en dermatología',
           100.0,
           2
       );

INSERT INTO patients
(address, date_of_birth, user_id)
VALUES
    ('Surco', '2001-05-20', 4);

INSERT INTO appointments
(date, time, status, notes, price, patient_id, professional_id, medical_service_id)
VALUES (
           '2026-10-05',
           '10:30',
           'PENDIENTE',
           'Consulta general',
           80.0,
           1,
           1,
           1
       );

INSERT INTO availabilities
(day_of_week, start_time, end_time, professional_id)
VALUES
    ('LUNES', '09:00', '13:00', 1);

INSERT INTO availabilities
(day_of_week, start_time, end_time, professional_id)
VALUES
    ('LUNES', '15:00', '18:00', 1);

INSERT INTO availabilities
(day_of_week, start_time, end_time, professional_id)
VALUES
    ('MARTES', '09:00', '13:00', 1);

INSERT INTO availabilities
(day_of_week, start_time, end_time, professional_id)
VALUES
    ('MIERCOLES', '10:00', '14:00', 2);
