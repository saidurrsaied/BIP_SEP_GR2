package com.parking.zonemanagement;

import com.parking.zonemanagement.internal.OccupationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class OccupationController {

    private final OccupationService occupationService;

    @PostMapping("/{id}/occupy")
    public ResponseEntity<Void> occupy(@PathVariable Long id, @RequestBody OccupyRequest request) {
        occupationService.markSpaceOccupied(id, request.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vacate")
    public ResponseEntity<OccupationRecord> vacate(@PathVariable Long id) {
        return ResponseEntity.ok(occupationService.markSpaceVacated(id));
    }

    @GetMapping("/{id}/occupancy")
    public ResponseEntity<OccupationRecord> getOccupancy(@PathVariable Long id) {
        OccupationRecord record = occupationService.getCurrentOccupancy(id);
        return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
    }

    public record OccupyRequest(Long userId) {}
}
