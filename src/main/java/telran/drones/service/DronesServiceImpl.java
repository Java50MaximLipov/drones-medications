package telran.drones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.PropertiesNames;
import telran.drones.dto.*;
import telran.drones.exceptions.*;

import telran.drones.model.*;
import telran.drones.repo.*;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class DronesServiceImpl implements DronesService {
	final DronesRepo droneRepo;
	final MedicationRepo medicationRepo;
	final EventLogRepo logRepo;
	final DronesModelRepo droneModelRepo;

	@Value("${" + PropertiesNames.CAPACITY_THRESHOLD + ":25}")
	int capacityThreshold;

	@Override
	@Transactional
	public DroneDto registerDrone(DroneDto droneDto) {
		log.debug("service got drone DTO: {}", droneDto);
		if (droneRepo.existsById(droneDto.number())) {
			throw new DroneAlreadyExistException();
		}
		Drone drone = Drone.of(droneDto);
		DroneModel droneModel = droneModelRepo.findById(droneDto.modelType())
				.orElseThrow(() -> new ModelNotFoundException());
		drone.setModel(droneModel);
		log.debug("drone object is {}", drone);
		droneRepo.save(drone);
		return droneDto;
	}

	@Override
	@Transactional(readOnly = false)
	public DroneMedication loadDrone(DroneMedication droneMedication) {
		String droneNumber = droneMedication.droneNumber();
		String medicationCode = droneMedication.medicationCode();
		log.debug("received: droneNumber={}, medicationCode={}", droneNumber, droneMedication.medicationCode());
		log.debug("capacity threshold is {}", capacityThreshold);
		Drone drone = droneRepo.findById(droneNumber).orElseThrow(() -> new DroneNotFoundException());
		log.debug("found drone: {}", drone);
		Medication medication = medicationRepo.findById(medicationCode)
				.orElseThrow(() -> new MedicationNotFoundException());
		log.debug("found medication: {}", medication);
		if (drone.getState() != State.IDLE) {
			throw new IllegalDroneStateException();
		}
		if (drone.getBatteryCapacity() < capacityThreshold) {
			throw new LowBatteryCapacityException();
		}
		if (drone.getModel().getWeight() < medication.getWeight()) {
			throw new IllegalMedicationWeightException();
		}
		drone.setState(State.LOADING);
		// EventLog(LocalDateTime timestamp, String droneNumber, State state, int
		// batteryCapacity)
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(),
				drone.getBatteryCapacity(), medicationCode);
		logRepo.save(eventLog);
		log.debug("saved log: {}", eventLog);
		return droneMedication;
	}

	@Override
	public List<String> checkMedicationItems(String droneNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> checkAvailableDrones() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int checkBatteryCapacity(String droneNumber) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DroneItemsAmount checkDroneLoadedItemAmounts() {
		// TODO Auto-generated method stub
		return null;
	}

}