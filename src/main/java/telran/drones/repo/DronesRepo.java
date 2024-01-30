package telran.drones.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.model.*;

public interface DronesRepo extends JpaRepository<Drone, String> {

}