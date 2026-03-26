-- Seed data for Parking Management System
-- This file is loaded automatically by Spring on startup
-- Passwords are hashed using BCrypt (min-cost 10)
-- All passwords: TestPassword123

-- ========================
-- USER MANAGEMENT
-- ========================

-- Test users: All with password TestPassword123
-- Hash: $2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM
INSERT INTO um_users (user_id, email, hashed_password, role, numberplate, created_at)
VALUES
  (1, 'admin@parking.de', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'ADMIN', 'ADMIN001', NOW()),
  (2, 'alice@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'CITIZEN', 'DT-AL-001', NOW()),
  (3, 'bob@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'CITIZEN', 'DT-BO-002', NOW()),
  (4, 'charlie@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'CITIZEN', 'DT-CH-003', NOW()),
  (5, 'diana@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'CITIZEN', 'DT-DI-004', NOW()),
  (6, 'eve@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'CITIZEN', 'DT-EV-005', NOW());

-- ========================
-- ZONE MANAGEMENT
-- ========================

-- Insert comprehensive parking zones
INSERT INTO zm_zones (zone_id, name, address, city, latitude, longitude, created_at)
VALUES
  ('550e8400-e29b-41d4-a716-446655440001', 'Central Plaza Hub', 'Main Street 15', 'Dortmund', 51.5140, 7.4652, NOW()),
  ('550e8400-e29b-41d4-a716-446655440002', 'Green Street Garage', 'Green Street 42', 'Dortmund', 51.5150, 7.4650, NOW()),
  ('550e8400-e29b-41d4-a716-446655440003', 'Riverfront Premium Parking', 'Riverside Avenue 8', 'Dortmund', 51.5130, 7.4660, NOW()),
  ('550e8400-e29b-41d4-a716-446655440004', 'Airport Express Lot', 'Airport Road 30', 'Dortmund', 51.5240, 7.4560, NOW());

-- Insert parking spaces for Zone 1: Central Plaza Hub (12 spaces)
-- 4 regular + 4 slow chargers + 4 fast chargers
INSERT INTO zm_spaces (space_id, zone_id, status, level, space_number, charging_point)
VALUES
  ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '1', 'P101', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '1', 'P102', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '1', 'P103', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '1', 'P104', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '2', 'P201', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '2', 'P202', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '2', 'P203', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '2', 'P204', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '3', 'P301', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '3', 'P302', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '3', 'P303', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '3', 'P304', 'FAST_CHARGER');

-- Insert parking spaces for Zone 2: Green Street Garage (8 spaces)
-- 2 regular + 2 slow chargers + 2 fast chargers + 2 regular
INSERT INTO zm_spaces (space_id, zone_id, status, level, space_number, charging_point)
VALUES
  ('660e8400-e29b-41d4-a716-446655440020', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '1', 'G101', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '1', 'G102', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '1', 'G103', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '1', 'G104', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440024', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '2', 'G201', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440025', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '2', 'G202', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440026', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '2', 'G203', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440027', '550e8400-e29b-41d4-a716-446655440002', 'FREE', '2', 'G204', 'FAST_CHARGER');

-- Insert parking spaces for Zone 3: Riverfront Premium Parking (6 spaces)
-- All regular parking, no charging
INSERT INTO zm_spaces (space_id, zone_id, status, level, space_number, charging_point)
VALUES
  ('660e8400-e29b-41d4-a716-446655440030', '550e8400-e29b-41d4-a716-446655440003', 'FREE', '1', 'R101', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440003', 'FREE', '1', 'R102', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440003', 'FREE', '1', 'R103', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440033', '550e8400-e29b-41d4-a716-446655440003', 'FREE', '2', 'R201', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440034', '550e8400-e29b-41d4-a716-446655440003', 'FREE', '2', 'R202', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440035', '550e8400-e29b-41d4-a716-446655440003', 'FREE', '2', 'R203', 'FALSE');

-- Insert parking spaces for Zone 4: Airport Express Lot (10 spaces)
-- 5 slow chargers + 5 fast chargers (premium charging zone)
INSERT INTO zm_spaces (space_id, zone_id, status, level, space_number, charging_point)
VALUES
  ('660e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '1', 'A101', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '1', 'A102', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '1', 'A103', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440043', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '1', 'A104', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440044', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '1', 'A105', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440045', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '2', 'A201', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440046', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '2', 'A202', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440047', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '2', 'A203', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440048', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '2', 'A204', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440049', '550e8400-e29b-41d4-a716-446655440004', 'FREE', '2', 'A205', 'FAST_CHARGER');

-- Update zone pricing policies (embedded in zone table as JSON)
-- Note: PricingPolicy is stored as part of the zone entity
-- Update via application after zones are created for proper serialization

-- ========================
-- RESERVATIONS
-- ========================

-- Sample reservations showing bookable chargers
-- These demonstrate that the system works with both regular and charging spaces
INSERT INTO res_reservations (reservation_id, user_id, user_email, space_id, status, reserved_from, reserved_until, created_at)
VALUES
  (1, 2, 'alice@example.com', '660e8400-e29b-41d4-a716-446655440003', 'CONFIRMED',
   CURRENT_TIMESTAMP + INTERVAL '2 hours', CURRENT_TIMESTAMP + INTERVAL '5 hours', CURRENT_TIMESTAMP),
  (2, 3, 'bob@example.com', '660e8400-e29b-41d4-a716-446655440045', 'CONFIRMED',
   CURRENT_TIMESTAMP + INTERVAL '1 hour', CURRENT_TIMESTAMP + INTERVAL '4 hours', CURRENT_TIMESTAMP);

-- ========================
-- BILLING
-- ========================

-- Invoices are created automatically on space vacation
-- No seed data needed here initially

-- ========================
-- NOTIFICATIONS
-- ========================

-- Notification records are created automatically on events
-- No seed data needed here initially
