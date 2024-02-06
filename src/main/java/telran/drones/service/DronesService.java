package telran.drones.service;

import java.util.List;

import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.EventLogDto;

public interface DronesService {
	/**
	 * adds new Drone into Database
	 * 
	 * @param droneDto
	 * @return DroneDto for success
	 * @throws DroneIllegalStateException (drone with a given number already exists)
	 */
	DroneDto registerDrone(DroneDto droneDto);

	/************************************************************/
	/**
	 * checks whether a given drone available for loading (state IDLE, battery
	 * capacity >= 25%, weight match) checks whether a given medication exists
	 * checks whether a given drone model exists creates event log with the state
	 * LOADING and current battery capcity
	 * 
	 * @param droneMedication
	 * @return DroneMedication for success
	 * @throws appropriate exception in accordance with the required checks
	 */
	DroneMedication loadDrone(DroneMedication droneMedication);

//		   - check how many medication items have been loaded for each drone, ordered by the amount in the
	/**
	 * checking loaded medication items for a given drone;
	 * 
	 * @param droneNumber
	 * @return list of medication codes that have been loaded on a given drone (for
	 *         all time)
	 */
	List<String> checkMedicationItems(String droneNumber);

	/*************************************************************/
	/**
	 * checking available drones for loading;
	 * 
	 * @return list of drone numbers that are available for loading
	 */
	List<String> checkAvailableDrones();

	/******************************************************/
	/**
	 * checking drone battery level for a given drone
	 * 
	 * @param droneNumber
	 * @return the battery capacity of a given drone
	 */
	int checkBatteryCapacity(String droneNumber);

	/****************************************************************/
	/**
	 * check how many medication items have been loaded for each drone, ordered by
	 * the amount in the descending order
	 * 
	 * @return distribution projection
	 */
	List<DroneItemsAmount> checkDroneLoadedItemAmounts();

	/******************************************************************/
	/**
	 * 
	 * @param droneNumber
	 * @return data about all logs of the drone with a given number
	 */
	List<EventLogDto> checkHistoryLogs(String droneNumber);

}