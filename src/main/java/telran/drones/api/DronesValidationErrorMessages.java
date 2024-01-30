package telran.drones.api;

public interface DronesValidationErrorMessages {
	int MAX_DRONE_NUMBER_LENGTH = 100;
	String DRONE_NUMBER_WRONG_LENGTH = "Length of drone number cannot be greater than " + MAX_DRONE_NUMBER_LENGTH;
	String MISSING_DRONE_NUMBER = "Missing drone number";
	String MISSING_MODEL = "Missing drone model";
	String WRONG_MEDICATION_CODE = "Wrong Medication Code";
	String MISSING_MEDICATION_CODE = "Missing Medication code";

}