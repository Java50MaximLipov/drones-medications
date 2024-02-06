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
import telran.drones.projections.DroneNumber;
import telran.drones.projections.MedicationCode;
import telran.drones.repo.*;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@Transactional(readOnly = true)
public class DronesServiceImpl implements DronesService {
	final DronesRepo droneRepo;
	final MedicationRepo medicationRepo;
	final EventLogRepo logRepo;
	final DronesModelRepo droneModelRepo;
	final Map<State, State> statesMachine;

	@Value("${" + PropertiesNames.CAPACITY_THRESHOLD + ":25}")
	int capacityThreshold;

	@Value("${" + PropertiesNames.CAPACITY_DELTA_TIME_UNIT + ":2}")
	private int capacityDeltaPerTimeUnit;

	@Override
	@Transactional(readOnly = false)
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
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(),
				drone.getBatteryCapacity(), medicationCode);
		logRepo.save(eventLog);
		log.debug("saved log: {}", eventLog);
		return droneMedication;
	}

	@Override
	public List<String> checkMedicationItems(String droneNumber) {
		if (!droneRepo.existsById(droneNumber)) {
			throw new DroneNotFoundException();
		}
		List<MedicationCode> codes = logRepo.findByDroneNumberAndState(droneNumber, State.LOADING);
		List<String> res = codes.stream().map(MedicationCode::getMedicationCode).toList();
		log.debug("Loaded medication items on drone {} are {} ", droneNumber, res);
		return res;
	}

	@Override
	public List<String> checkAvailableDrones() {
		List<DroneNumber> numbers = droneRepo.findByStateAndBatteryCapacityGreaterThanEqual(State.IDLE,
				capacityThreshold);
		List<String> res = numbers.stream().map(DroneNumber::getNumber).toList();
		log.debug("Available drones are {}", res);
		return res;
	}

	@Override
	public int checkBatteryCapacity(String droneNumber) {
		Integer batteryCapacity = droneRepo.findBatteryCapacity(droneNumber);
		if (batteryCapacity == null) {
			throw new DroneNotFoundException();
		}
		log.debug("battery capacity of drone {} is {}", droneNumber, batteryCapacity);
		return batteryCapacity;
	}

	@Override
	public List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
		List<DroneItemsAmount> res = logRepo.getItemAmounts();
		res.forEach(dia -> log.trace("drone {}, items amount {}", dia.getNumber(), dia.getAmount()));
		return res;
	}

	@Scheduled(fixedDelayString = "${" + PropertiesNames.PERIODIC_UNIT_MILLIS + ":5000}")
	@Transactional
	void periodicTask() {
		List<Drone> allDrones = droneRepo.findAll();
		log.trace("there are {} drones", allDrones.size());
		allDrones.forEach(this::droneProcessing);
	}

	private void droneProcessing(Drone drone) {
		String droneNumber = drone.getNumber();
		int batteryCapacity = drone.getBatteryCapacity();
		int capacityDelta = capacityDeltaPerTimeUnit;
		log.trace("before processing - drone: {}, battery capacity: {}, state: {}", droneNumber, batteryCapacity,
				drone.getState());
		if (drone.getState() != State.IDLE) {
			State nextState = statesMachine.get(drone.getState());
			drone.setState(nextState);
			capacityDelta = -capacityDelta;
		}
		if ((drone.getState() == State.IDLE && batteryCapacity < 100) || drone.getState() != State.IDLE) {
			drone.setBatteryCapacity(batteryCapacity + capacityDelta);
			createNewLog(drone);
			batteryCapacity = drone.getBatteryCapacity();
			log.trace("after processing - drone: {}, battery capacity: {}, state: {}", droneNumber, batteryCapacity,
					drone.getState());
		}
	}

	private void createNewLog(Drone drone) {
		String medicationCode = null;
		String droneNumber = drone.getNumber();
		State state = drone.getState();
		int batteryCapacity = drone.getBatteryCapacity();
		if (drone.getState() != State.IDLE) {
			EventLog lastEventLog = logRepo.findFirst1ByDroneNumberOrderByTimestampDesc(droneNumber);
			if (lastEventLog == null) {
				log.error(
						"No event logs are found for drone {}," + " but it means that there is error in states machine",
						droneNumber);
			} else {
				medicationCode = lastEventLog.getMedicationCode();
			}
		}
		EventLog logForSaving = new EventLog(LocalDateTime.now(), droneNumber, state, batteryCapacity, medicationCode);
		logRepo.save(logForSaving);
		log.debug("log {} has been saved", logForSaving.build());
	}

	@Override
	public List<EventLogDto> checkHistoryLogs(String droneNumber) {
		if (!droneRepo.existsById(droneNumber)) {
			throw new DroneNotFoundException();
		}
		List<EventLog> logs = logRepo.findByDroneNumber(droneNumber);
		log.debug("drone {} has {} logs", droneNumber, logs.size());
		return logs.stream().map(EventLog::build).toList();
	}

}