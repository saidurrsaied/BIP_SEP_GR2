/**
 * Domain types matching the backend exactly
 * These correspond to Java entity classes in the parking backend
 */

// ==================== ENUMERATIONS ====================

export type SpaceStatus = 'FREE' | 'RESERVED' | 'OCCUPIED' | 'BLOCKED';
export type ChargingPoint = 'SLOW_CHARGER' | 'FAST_CHARGER' | 'FALSE';
export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'EXPIRED';
export type InvoiceStatus = 'CREATED' | 'PENDING' | 'PAID' | 'FAILED';
export type BillingItemType = 'PARKING' | 'EV_CHARGING';
export type UserRole = 'CITIZEN' | 'ADMIN';

// ==================== DOMAIN OBJECTS ====================

/**
 * Pricing policy for a parking zone (embedded value object)
 */
export interface PricingPolicy {
  hourlyRateCents: number;
  chargingRatePerKwhCents?: number;
  slowMultiplier?: number;
  fastMultiplier?: number;
  currency?: string;
}

/**
 * Parking space within a zone
 */
export interface ParkingSpace {
  spaceId: string; // UUID
  status: SpaceStatus;
  chargingPoint: ChargingPoint;
  level?: string;
  spaceNumber?: string;
  zone?: ParkingZone; // Optional nested zone object
}

/**
 * Parking zone (aggregate root)
 */
export interface ParkingZone {
  zoneId: string; // UUID
  name: string;
  city: string;
  address: string;
  latitude: number;
  longitude: number;
  spaces?: ParkingSpace[];
  pricingPolicy: PricingPolicy;
}

/**
 * Map data for displaying zones and spaces on map
 */
export interface MapData {
  zoneId: string;
  name: string;
  latitude: number;
  longitude: number;
  spaces: SpaceData[];
}

export interface SpaceData {
  spaceId: string;
  status: SpaceStatus;
  chargingPoint: ChargingPoint;
}

/**
 * Reservation for a parking space
 */
export interface Reservation {
  reservationId: number;
  userId: number;
  userEmail?: string; // Denormalized for notifications
  spaceId: string; // UUID
  zoneId?: string; // UUID
  status: ReservationStatus;
  reservedFrom: string; // ISO 8601 datetime
  reservedUntil: string; // ISO 8601 datetime
  createdAt: string; // ISO 8601 datetime
}

/**
 * Billing item (line item in invoice)
 */
export interface BillingItem {
  id?: number;
  itemId?: number;
  type: BillingItemType;
  description: string;
  amountCents: number; // Amount in cents (long)
  amount?: number; // Alternative field name
  durationMinutes?: number;
}

/**
 * Invoice for parking + EV charging costs
 */
export interface Invoice {
  invoiceId: number;
  userId: number;
  reservationId?: number;
  items: BillingItem[];
  status: InvoiceStatus;
  totalAmountCents: number; // Total in cents (long)
  currency: string;
  createdAt: string; // ISO 8601 datetime
  paidAt?: string; // ISO 8601 datetime
}

/**
 * User account
 */
export interface User {
  userId: number;
  email: string;
  role: UserRole;
  numberplate?: string;
  createdAt?: string; // ISO 8601 datetime
}

/**
 * Authentication response from backend
 */
export interface AuthResponse {
  token: string; // JWT token
  user: User;
}

/**
 * Occupation record for tracking parking session
 */
export interface OccupationRecord {
  recordId: string; // UUID
  spaceId: string; // UUID
  userId?: number;
  userEmail?: string; // Denormalized
  startTime: string; // ISO 8601 datetime
  endTime?: string; // ISO 8601 datetime
  chargingPoint: ChargingPoint;
}
