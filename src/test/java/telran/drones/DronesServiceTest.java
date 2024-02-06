package telran.drones;

import telran.drones.api.PropertiesNames;
import telran.drones.dto.*;
import telran.drones.model.*;
import telran.drones.exceptions.*;

import telran.drones.repo.*;
import telran.drones.service.DronesService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(properties = { PropertiesNames.PERIODIC_UNIT_MILLIS + "=10" })
@Sql(scripts = "classpath:test_idle.data.sql")

class DronesServiceTest {
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
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NORMAL)
	void loadDroneNormal() throws InterruptedException {
		dronesService.loadDrone(droneMedication1);
		Thread.sleep(2500);
		List<EventLog> logs = logRepo.findAll();
		// 1-log (method loadDrone) + 11 (Full cycle of delivering and returning) + 11
		// (full cycle of battery charging)
		assertEquals(23, logs.size());
		State[] statesChain = getStatesChail();
		assertStates(statesChain, logs);
		Drone drone = droneRepo.findById(DRONE1).orElseThrow();
		assertEquals(State.IDLE, drone.getState());
		assertEquals(100, drone.getBatteryCapacity());
	}

	private State[] getStatesChail() {
		State[] stateValues = State.values();
		State[] statesChain = Arrays.copyOfRange(stateValues, 1, stateValues.length + 12);
		for (int i = 0; i < statesChain.length; i++) {
			if (statesChain[i] == null) {
				statesChain[i] = State.IDLE;
			}
		}
		return statesChain;
	}

	private void assertStates(State[] statesChain, List<EventLog> logs) {
		final int[] indexValues = { 0 };
		logs.forEach(l -> assertEquals(statesChain[indexValues[0]++], l.getState()));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_NORMAL)
	void registerDroneNormal() {
		assertEquals(droneDto, dronesService.registerDrone(droneDto));
		assertTrue(droneRepo.existsById(DRONE4));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void registerDroneAlreadyExists() {
		assertThrowsExactly(DroneAlreadyExistException.class, () -> dronesService.registerDrone(drone1));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MED_ITEMS_DRONE_NOT_FOUND)
	void checkDroneMedItemsNotFound() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.checkMedicationItems(DRONE4));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_BATTERY_LEVEL_NORMAL)
	void checkBatteryCapacityNormal() {
		assertEquals(100, dronesService.checkBatteryCapacity(DRONE2));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_BATTERY_LEVEL_DRONE_NOT_FOUND)
	void checkBatteryCapacityNotFound() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.checkBatteryCapacity(DRONE4));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_DRONES_ITEMS_AMOUNT)
	void checkDroneLoadedItemAmounts() {
		dronesService.loadDrone(droneMedication1);
		dronesService.loadDrone(droneMedication2);
		Map<String, Long> resultMap = dronesService.checkDroneLoadedItemAmounts().stream()
				.collect(Collectors.toMap(da -> da.getNumber(), da -> da.getAmount()));
		assertEquals(3, resultMap.size());
		assertEquals(1, resultMap.get(DRONE1));
		assertEquals(1, resultMap.get(DRONE2));
		assertEquals(0, resultMap.get(DRONE3));
	}

}