package com.parking.zonemanagement.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OccupationRepository extends JpaRepository<OccupationRecord, UUID> {
    Optional<OccupationRecord> findBySpaceIdAndEndTimeIsNull(UUID spaceId);
}
