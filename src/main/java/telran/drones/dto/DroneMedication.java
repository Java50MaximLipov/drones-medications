package telran.drones.dto;

import static telran.drones.api.DronesValidationErrorMessages.*;
import static telran.drones.api.ConstraintConstants.*;
import jakarta.validation.constraints.*;

//TODO add validation constraints
public record DroneMedication(
		@Size(max = MAX_DRONE_NUMBER_LENGTH, message = DRONE_NUMBER_WRONG_LENGTH) @NotEmpty(message = MISSING_DRONE_NUMBER) String droneNumber,
		@NotEmpty(message = MISSING_MEDICATION_CODE) @Pattern(regexp = MEDICATION_CODE_REGEXP, message = WRONG_MEDICATION_CODE) String medicationCode) {

}