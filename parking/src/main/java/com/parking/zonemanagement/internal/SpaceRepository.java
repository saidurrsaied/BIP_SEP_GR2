package com.parking.zonemanagement.internal;

import com.parking.zonemanagement.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<ParkingSpace, String> {
    List<ParkingSpace> findByZoneId(String zoneId);
}
