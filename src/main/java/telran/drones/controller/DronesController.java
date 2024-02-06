package telran.drones.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.UrlConstants;
import telran.drones.dto.*;
import telran.drones.service.DronesService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DronesController {
	final DronesService dronesService;

	@PostMapping(UrlConstants.DRONES)
	DroneDto registerDrone(@RequestBody @Valid DroneDto droneDto) {
		log.debug("received: {}", droneDto);
		return dronesService.registerDrone(droneDto);
	}

	@PostMapping(UrlConstants.LOAD_DRONE)
	DroneMedication loadDrone(@RequestBody @Valid DroneMedication droneMedication) {
		log.debug("received: {}", droneMedication);
		return dronesService.loadDrone(droneMedication);
	}

	@GetMapping(UrlConstants.DRONE_MEDICATION_ITEMS + "{" + UrlConstants.DRONE_NUMBER + "}")
	List<String> checkMedicationItems(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkMedicationItems controller for drone {}", droneNumber);
		return dronesService.checkMedicationItems(droneNumber);
	}

	@GetMapping(UrlConstants.AVAILABLE_DRONES)
	List<String> checkAvailableDrones() {
		log.debug("checkAvailableDrones controller");
		return dronesService.checkAvailableDrones();
	}

	@GetMapping(UrlConstants.DRONE_BATTERY_CAPACITY + "{" + UrlConstants.DRONE_NUMBER + "}")
	int checkBatteryCapacity(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkBatteryCapacity controller for drone {}", droneNumber);
		return dronesService.checkBatteryCapacity(droneNumber);
	}

	@GetMapping(UrlConstants.DRONES_AMOUNT_ITEMS)
	List<DroneItemsAmount> checkDronesMedItems() {
		log.debug("checkDronesMedItems controller");
		return dronesService.checkDroneLoadedItemAmounts();
	}

	@GetMapping(UrlConstants.DRONE_HISTORY_LOGS + "{" + UrlConstants.DRONE_NUMBER + "}")
	List<EventLogDto> checkHistoryLogs(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkHistoryLogs controller for drone {}", droneNumber);
		return dronesService.checkHistoryLogs(droneNumber);
	}

}