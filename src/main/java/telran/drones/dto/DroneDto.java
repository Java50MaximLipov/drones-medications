package telran.drones.dto;

import jakarta.validation.constraints.*;
import static telran.drones.api.DronesValidationErrorMessages.*;

public record DroneDto(
		@Size(max = MAX_DRONE_NUMBER_LENGTH, message = DRONE_NUMBER_WRONG_LENGTH) @NotEmpty(message = MISSING_DRONE_NUMBER) String number,
		@NotNull(message = MISSING_MODEL) ModelType modelType) {

}