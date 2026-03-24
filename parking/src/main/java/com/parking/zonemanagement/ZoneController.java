package com.parking.zonemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @PostMapping
    public ResponseEntity<ParkingZone> createZone(@RequestBody CreateZoneRequest request) {
        return ResponseEntity.ok(zoneService.createZone(
                request.name(), request.address(), request.city(),
                request.latitude(), request.longitude(), request.pricingPolicy()));
    }

    @PostMapping("/{id}/spaces")
    public ResponseEntity<ParkingSpace> addSpace(@PathVariable Long id, @RequestBody AddSpaceRequest request) {
        return ResponseEntity.ok(zoneService.addSpaceToZone(id, request.hasChargingPoint(), request.level(), request.spaceNumber()));
    }

    @GetMapping("/{id}/spaces/available")
    public ResponseEntity<List<ParkingSpace>> getAvailableSpaces(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getAvailableSpaces(id));
    }

    @GetMapping("/{id}/pricing")
    public ResponseEntity<PricingPolicy> getPricing(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getPricingPolicy(id));
    }

    public record CreateZoneRequest(String name, String address, String city, double latitude, double longitude, PricingPolicy pricingPolicy) {}
    public record AddSpaceRequest(HasChargingPoint hasChargingPoint, String level, String spaceNumber) {}
}
