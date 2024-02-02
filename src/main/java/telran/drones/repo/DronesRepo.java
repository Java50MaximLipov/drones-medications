package telran.drones.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.State;
import telran.drones.model.*;
import telran.drones.projections.DroneNumber;

public interface DronesRepo extends JpaRepository<Drone, String> {

	List<DroneNumber> findByStateAndBatteryCapacityGreaterThanEqual(State state, int capacityThreshold);

	@Query("select batteryCapacity from Drone where number=:droneNumber")
	Integer findBatteryCapacity(String droneNumber);

}