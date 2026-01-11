-- 1. Insertar Aerolíneas
INSERT INTO airlines (code, name) VALUES ('IB', 'Iberia');
INSERT INTO airlines (code, name) VALUES ('VY', 'Vueling');
INSERT INTO airlines (code, name) VALUES ('UX', 'Air Europa');

-- 2. Insertar Aeropuertos
INSERT INTO airports (code, name, city, country) VALUES ('MAD', 'Adolfo Suárez Madrid-Barajas', 'Madrid', 'España');
INSERT INTO airports (code, name, city, country) VALUES ('BCN', 'Josep Tarradellas Barcelona-El Prat', 'Barcelona', 'España');
INSERT INTO airports (code, name, city, country) VALUES ('ALC', 'Alicante-Elche Miguel Hernández', 'Alicante', 'España');
INSERT INTO airports (code, name, city, country) VALUES ('JFK', 'John F. Kennedy International', 'Nueva York', 'USA');

-- 3. Insertar Pasajeros iniciales
INSERT INTO passengers (document_number, gender) VALUES ('12345678Z', 'M');
INSERT INTO passengers (document_number, gender) VALUES ('98765432X', 'F');

-- 4. Insertar un Vuelo de ejemplo (Opcional, para probar el GET directamente)
-- Nota: airline_id=1 es Iberia, departure=1 es MAD, arrival=2 es BCN
INSERT INTO flights (flight_number, departure_date, arrival_date, departure_time, arrival_time, duration_minutes, base_price, status, airline_id, departure_airport_id, arrival_airport_id)
VALUES ('IB3250', '2026-05-20', '2026-05-20', '10:00:00', '11:30:00', 90, 45.00, 'SCHEDULED', 1, 1, 2);

-- 5. Insertar Asientos para ese vuelo (id_vuelo = 1)
INSERT INTO seats (seat_number, cabin_class, flight_id) VALUES ('12A', 'ECONOMY', 1);
INSERT INTO seats (seat_number, cabin_class, flight_id) VALUES ('12B', 'ECONOMY', 1);
INSERT INTO seats (seat_number, cabin_class, flight_id) VALUES ('1A', 'BUSINESS', 1);