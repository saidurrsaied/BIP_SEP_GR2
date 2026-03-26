package com.parking.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Void> placeReservation(@RequestBody PlaceReservationRequest request) {
        reservationService.checkReservationSpace(request.userId(), request.spaceId(), request.from(), request.until());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id,@RequestBody UpdateReservationRequest request) {
        return ResponseEntity.ok(reservationService.updateReservation(
                id, request.spaceId(), request.from(), request.until()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsForUser(userId));
    }

    public record PlaceReservationRequest(Long userId, UUID spaceId, Instant from, Instant until) {}
    public record UpdateReservationRequest(UUID spaceId, Instant from, Instant until) {}
}
