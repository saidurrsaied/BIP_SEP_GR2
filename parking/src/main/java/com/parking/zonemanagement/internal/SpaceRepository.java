package com.parking.zonemanagement.internal;

import com.parking.zonemanagement.ParkingSpace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByZoneId(Long zoneId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSpace s WHERE s.spaceId = :spaceId")
    Optional<ParkingSpace> findByIdForUpdate(Long spaceId);
}
