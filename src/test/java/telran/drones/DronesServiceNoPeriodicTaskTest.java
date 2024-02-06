package telran.drones;

import telran.drones.api.PropertiesNames;
import telran.drones.dto.*;
import telran.drones.exceptions.*;

import telran.drones.repo.*;
import telran.drones.service.DronesService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(properties = { PropertiesNames.PERIODIC_UNIT_MILLIS + "=100000000" })
@Sql(scripts = "classpath:test_data.sql")

class DronesServiceNoPeriodicTaskTest {
	private static final String DRONE1 = "Drone-1";
	private static final String DRONE2 = "Drone-2";
	private static final String MED1 = "MED_1";
	private static final String DRONE3 = "Drone-3";
	private static final String SERVICE_TEST = "Service: ";
	private static final String DRONE4 = "Drone-4";
	private static final String MED2 = "MED_2";

	@Autowired
	DronesService dronesService;

	@Autowired
	DronesRepo droneRepo;

	@Autowired
	EventLogRepo logRepo;

	DroneDto droneDto = new DroneDto(DRONE4, ModelType.Cruiserweight);
	DroneDto drone1 = new DroneDto(DRONE1, ModelType.Middleweight);
	DroneMedication droneMedication1 = new DroneMedication(DRONE1, MED1);
	DroneMedication droneMedication2 = new DroneMedication(DRONE2, MED2);

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_STATE)
	void loadDroneWrongState() {
		assertThrowsExactly(IllegalDroneStateException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE3, MED1)));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_MEDICATION_NOT_FOUND)
	void loadDroneMedicationNotFound() {
		assertThrowsExactly(MedicationNotFoundException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE1, "KUKU")));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NOT_FOUND)
	void loadDroneNotFound() {
		assertThrowsExactly(DroneNotFoundException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE4, MED1)));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MED_ITEMS_NORMAL)
	void checkDroneMedItemsNormal() {
		dronesService.loadDrone(droneMedication1);
		List<String> medItemsExpected = List.of(MED1);
		List<String> medItemsActual = dronesService.checkMedicationItems(DRONE1);
		assertIterableEquals(medItemsExpected, medItemsActual);
		assertTrue(dronesService.checkMedicationItems(DRONE2).isEmpty());
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MED_ITEMS_DRONE_NOT_FOUND)
	void checkDroneMedItemsNotFound() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.checkMedicationItems(DRONE4));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.AVAILABLE_DRONES)
	void checkAvailableDrones() {
		List<String> availableExpected = List.of(DRONE1);
		List<String> availableActual = dronesService.checkAvailableDrones();
		assertIterableEquals(availableExpected, availableActual);
		dronesService.loadDrone(droneMedication1);
		assertTrue(dronesService.checkAvailableDrones().isEmpty());
	}

}