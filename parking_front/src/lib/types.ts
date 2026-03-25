// Domain types matching the backend

export type SpaceStatus = 'FREE' | 'RESERVED' | 'OCCUPIED';
export type HasChargingPoint = 'TRUE' | 'FALSE';
export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'EXPIRED';

export interface ParkingZone {
  id: number;
  name: string;
  address: string;
  city: string;
  latitude: number;
  longitude: number;
  pricingPolicy: PricingPolicy;
}

export interface ParkingSpace {
  id: number;
  zoneId: number;
  status: SpaceStatus;
  hasChargingPoint: HasChargingPoint;
  level: string;
  spaceNumber: string;
}

export interface PricingPolicy {
  hourlyRateCents: number;
  chargingRatePerKwhCents: number;
  currency: string;
}

export interface Reservation {
  id: number;
  userId: number;
  spaceId: number;
  zoneId: number;
  status: ReservationStatus;
  reservedFrom: string; // ISO duration string
  reservedUntil: string; // ISO duration string
  createdAt: string;
}

export interface Invoice {
  id: number;
  userId: number;
  reservationId: number;
  items: BillingItem[];
  status: InvoiceStatus;
  totalAmountCents: number;
  currency: string;
  createdAt: string;
  paidAt?: string;
}

export interface BillingItem {
  id: number;
  type: BillingItemType;
  description: string;
  amountCents: number;
  durationMinutes?: number;
}

export type InvoiceStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
export type BillingItemType = 'PARKING' | 'EV_CHARGING';

export interface MapData {
  zone: ParkingZone;
  spaces: ParkingSpace[];
}

export interface User {
  userId: string;
  email: string;
  role: 'CITIZEN' | 'ADMIN';
  createdAt: string;
}
