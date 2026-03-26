-- Seed data for Parking Management System
-- This file is loaded automatically by Spring on startup
-- Passwords are hashed using BCrypt (min-cost 10)

-- ========================
-- USER MANAGEMENT
-- ========================

-- Citizen user: email=citizen@example.com, password=TestPassword123
INSERT INTO um_users (user_id, email, hashed_password, role, numberplate, created_at)
VALUES
  (1, 'citizen@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'CITIZEN', 'AB123CD', NOW()),
  (2, 'admin@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QGLM', 'ADMIN', 'XY789ZW', NOW());

-- ========================
-- ZONE MANAGEMENT
-- ========================

-- Insert test parking zones
INSERT INTO zm_zones (zone_id, name, address, city, latitude, longitude, created_at)
VALUES
  ('550e8400-e29b-41d4-a716-446655440000', 'Downtown Dortmund', 'Main Street 1', 'Dortmund', 51.5136, 7.4653, NOW()),
  ('550e8400-e29b-41d4-a716-446655440001', 'Central Station', 'Bahnhofstr. 10', 'Dortmund', 51.5170, 7.4640, NOW());

-- Insert parking spaces for Zone 1
INSERT INTO zm_spaces (space_id, zone_id, status, level, space_number, charging_point)
VALUES
  ('660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'FREE', '1', '101', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'FREE', '1', '102', 'FALSE'),
  ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'FREE', '2', '201', 'FAST_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 'FREE', '2', '202', 'FALSE');

-- Insert parking spaces for Zone 2
INSERT INTO zm_spaces (space_id, zone_id, status, level, space_number, charging_point)
VALUES
  ('660e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '1', '001', 'SLOW_CHARGER'),
  ('660e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', 'FREE', '1', '002', 'FALSE');

-- Update zone pricing policies (embedded in zone table as JSON)
-- Note: PricingPolicy is stored as part of the zone entity
-- Update via application after zones are created for proper serialization

-- ========================
-- RESERVATIONS
-- ========================

-- Sample reservation (optional - usually created via API)
-- INSERT INTO res_reservations (reservation_id, user_id, user_email, space_id, status, reserved_from, reserved_until, created_at)
-- VALUES
--   (1, 1, 'citizen@example.com', '660e8400-e29b-41d4-a716-446655440000', 'CONFIRMED',
--    NOW() + INTERVAL 1 HOUR, NOW() + INTERVAL 3 HOUR, NOW());

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
