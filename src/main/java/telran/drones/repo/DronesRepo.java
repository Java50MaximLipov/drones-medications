package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.dto.State;
import telran.drones.model.*;
import telran.drones.projections.DroneNumber;

public interface DronesRepo extends JpaRepository<Drone, String> {
	List<DroneNumber> findByStateAndBatteryCapacityGreaterThanEqual(State state, int capacityThreshold);

}