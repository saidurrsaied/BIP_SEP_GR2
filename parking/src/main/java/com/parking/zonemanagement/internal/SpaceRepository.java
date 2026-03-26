package com.parking.zonemanagement.internal;

import com.parking.zonemanagement.ParkingSpace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<ParkingSpace, UUID> {
    List<ParkingSpace> findByZoneZoneId(UUID zoneId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ParkingSpace> findBySpaceId(UUID spaceId);
}
