# UrbanFlow Dashboard - Sveltekit Implementation

A modern, responsive parking management dashboard built with Sveltekit and Tailwind CSS.

## Features

✅ **Interactive Map View** - Visual parking zone overlay with real-time availability
✅ **Responsive Design** - Works seamlessly on desktop, tablet, and mobile devices
✅ **EV Charging Integration** - Browse and reserve EV charging stations
✅ **Booking History** - Track past and current parking sessions with invoices
✅ **User Profile** - Manage account settings, payment methods, and preferences
✅ **Real-time Data** - Backend API integration for live parking availability

## Project Structure

```
parking_front/src/
├── lib/
│   ├── api/
│   │   └── client.ts          # API client with request handling
│   ├── components/
│   │   ├── TopNavBar.svelte    # Header with search and notifications
│   │   ├── Sidebar.svelte      # Parking spaces list
│   │   ├── MapSection.svelte   # Interactive map with pins
│   │   └── BottomNavBar.svelte # Mobile navigation
│   └── types.ts                # TypeScript interfaces for type safety
├── routes/
│   ├── +layout.svelte          # Root layout
│   ├── +page.svelte            # Main dashboard (map view)
│   ├── charging/+page.svelte   # EV charging stations
│   ├── history/+page.svelte    # Booking history & invoices
│   ├── parking/+page.svelte    # My active reservations
│   └── profile/+page.svelte    # User account management
├── app.css                     # Global styles & Tailwind config
└── app.html                    # HTML template

tailwind.config.ts              # Tailwind color scheme
svelte.config.js               # Sveltekit configuration
```

## Colors & Design System

The dashboard uses a Material Design 3 inspired color palette:

- **Primary**: `#041627` (Dark Blue)
- **Secondary**: `#006d37` (Green)
- **Tertiary**: `#001a08` (Dark Green)
- **Error**: `#ba1a1a` (Red)
- **Surface**: `#f8f9fd` (Off-White)

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

```bash
cd parking_front
npm install
```

### Development

```bash
npm run dev
```

The dashboard will be available at `http://localhost:5173`

### Building for Production

```bash
npm run build
npm run preview
```

## API Integration

The dashboard connects to the backend parking server at `http://localhost:8080/api`.

### Available Endpoints

- `GET /api/zones/map` - Get all parking zones with spaces
- `GET /api/zones/{id}/map` - Get specific zone details
- `POST /api/reservations` - Create new reservation
- `GET /api/billing/invoices` - Get user invoices
- `GET /api/users/profile` - Get user profile

See `src/lib/api/client.ts` for implementation details.

## Pages

### Dashboard (`/`)
Main map view with sidebar listing nearby parking spaces. Includes:
- Real-time parking availability
- Pricing information
- EV charging availability
- Quick zone filters

### Charging (`/charging`)
Browse and reserve EV charging stations with pricing and availability.

### History (`/history`)
View past and current parking sessions with associated invoices and receipts.

### My Parking (`/parking`)
Manage active parking reservations with time remaining, directions, and cancellation options.

### Profile (`/profile`)
User account management including:
- Personal information
- Email preferences
- Two-factor authentication
- Payment methods
- Account deletion

## Components

### TopNavBar
Header with logo, navigation links, search, notifications, and user profile.

### Sidebar
Scrollable list of nearby parking spaces with filters.

### MapSection
Interactive map with parking zone pins and map controls.

### BottomNavBar
Mobile navigation bar with 4 main routes.

## Styling

The project uses Tailwind CSS v4 with the custom color palette. Custom utilities are defined in `app.css`.

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
