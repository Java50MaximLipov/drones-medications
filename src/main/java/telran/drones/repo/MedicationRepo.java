package telran.drones.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.model.*;

public interface MedicationRepo extends JpaRepository<Medication, String> {

}