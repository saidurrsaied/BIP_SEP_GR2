package com.parking.zonemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class MapOverlayController {

    private final MapOverlayService mapOverlayService;

    @GetMapping("/map")
    public ResponseEntity<List<MapOverlayService.MapData>> getAllZonesMap() {
        return ResponseEntity.ok(mapOverlayService.getAllZonesMapData());
    }

    @GetMapping("/{id}/map")
    public ResponseEntity<MapOverlayService.MapData> getZoneMap(@PathVariable UUID id) {
        return ResponseEntity.ok(mapOverlayService.getEnrichedMapData(id));
    }
}
