package com.parking.zonemanagement.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OccupationRepository extends JpaRepository<OccupationRecord, Long> {
    Optional<OccupationRecord> findBySpaceIdAndEndTimeIsNull(Long spaceId);
}
