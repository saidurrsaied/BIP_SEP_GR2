package com.parking;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityVerificationTest {

	@Test
	void verifyModularity() {
		ApplicationModules.of(ParkingApplication.class).verify();
	}
}
