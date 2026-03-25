package com.parking.zonemanagement.internal;

import com.parking.zonemanagement.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<ParkingZone, UUID> {
}
