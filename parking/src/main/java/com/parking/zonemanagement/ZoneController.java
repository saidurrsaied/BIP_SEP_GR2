package com.parking.zonemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping
    public ResponseEntity<List<ParkingZone>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @PostMapping
    public ResponseEntity<ParkingZone> createZone(@RequestBody CreateZoneRequest request) {
        return ResponseEntity.ok(zoneService.createZone(
                request.name(), request.address(), request.city(),
                request.latitude(), request.longitude(), request.pricingPolicy()));
    }

    @PostMapping("/{id}/spaces")
    public ResponseEntity<ParkingSpace> addSpace(@PathVariable UUID id, @RequestBody AddSpaceRequest request) {
        return ResponseEntity.ok(zoneService.addSpaceToZone(id, request.chargingPoint(), request.level(), request.spaceNumber()));
    }

    @GetMapping("/{id}/spaces/available")
    public ResponseEntity<List<ParkingSpace>> getAvailableSpaces(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.getAvailableSpaces(id));
    }

    @GetMapping("/{id}/pricing")
    public ResponseEntity<PricingPolicy> getPricing(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.getPricingPolicy(id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ParkingZone> getZoneById(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingZone> updateZone(@PathVariable UUID id, @RequestBody UpdateZoneRequest request) {
        return ResponseEntity.ok(zoneService.updateZone(
                id, request.name(), request.address(), request.city(),
                request.latitude(), request.longitude(), request.pricingPolicy()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable UUID id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateZoneRequest(String name, String address, String city, double latitude, double longitude, PricingPolicy pricingPolicy) {}
    public record UpdateZoneRequest(String name, String address, String city, double latitude, double longitude, PricingPolicy pricingPolicy) {}
    public record AddSpaceRequest(ChargingPoint chargingPoint, String level, String spaceNumber) {}
}
