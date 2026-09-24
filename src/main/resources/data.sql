--Password: 123456
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
(location, price, rating, user_id, specialty_id)
VALUES
    ('Lima', 80.0, 4.5, 1, 1);

INSERT INTO professionals
(location, price, rating, user_id, specialty_id)
VALUES
    ('San Isidro', 60.0, 4.1, 2, 2);

INSERT INTO patients
(address, date_of_birth, user_id)
VALUES
    ('Surco', '2001-05-20', 3);

INSERT INTO appointments
(date, time, status, notes, price, patient_id, professional_id)
VALUES
    (
        '2026-10-05',
        '10:30',
        'PENDIENTE',
        'Consulta general',
        80.0,
        1,
        1
    );