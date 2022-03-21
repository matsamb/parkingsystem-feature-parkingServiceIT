package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Processes vehicles ( car or bike ) entering or exiting the parking lot which
 * consists of 3 car's and 2 bikes's slots. Starts and Closes connection to prod
 * database's ticket and parking tables.
 * 
 * @author matlu
 *
 */

public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;
	

	/**
	 * Constructor
	 * 
	 * @param inputReaderUtil
	 * @param parkingSpotDAO
	 * @param ticketDAO
	 */

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	/**
	 * Checks for available parking spot in the parking table of the prod DataBase.
	 * If room is found, fetches the lowest Id spot according to the type of
	 * vehicle, updates the availability of the slot in the parking table to false,
	 * and generates a new ticket in the ticket table. Else if the parking lot is
	 * full, warns that the vehicle can not be processed.
	 *
	 * @return int parkingSpot.getId() the parking spot identity number (1,2,3) for
	 *         cars and (4,5) for bikes.
	 */

	public int processIncomingVehicle() {
		int result = 0;

		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehichleRegNumber();
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);// allot
				// this parking space and mark it's availability as false

				Calendar inTime = Calendar.getInstance();
				inTime.setTimeInMillis(System.currentTimeMillis());
				Ticket ticket = new Ticket();
				// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME,(database
				// only) IN_SLOT)
				// ticket.setId(ticketID);
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(0);
				ticket.setInTime(inTime);
				ticket.setOutTime(inTime);
				ticketDAO.saveTicket(ticket);
				
				ticket.setRecurrentUser(ticketDAO.recurringUserTicket(vehicleRegNumber));


				System.out.println("Generated Ticket and saved in DB");
				System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
				System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:"
						+ new Timestamp(inTime.getTime().getTime()));

				result = parkingSpot.getId();
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);

		}
		return result;
	}

	/**
	 * Calls for an InputReader to read registration number as input from the
	 * console and pass on.
	 * 
	 * @return String registration number.
	 * @throws Exception if empty String or non valid registration number is
	 *                   provided.
	 */

	public String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * Calls for parkingSpotDao.getNextAvailableSlot() method to get a parking spot
	 * if available and to pass on.
	 * 
	 * @return a parkingSpot if available
	 */

	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	/**
	 * Displays a menu to choose from on the console and calls for an InputReader to
	 * read input from the console and pass on.
	 * 
	 * @return parkingType
	 */

	private ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
			case 1: {
				return ParkingType.CAR;
			}
			case 2: {
				return ParkingType.BIKE;
			}
			default: {
				System.out.println("Incorrect input provided");
				throw new IllegalArgumentException("Entered input is invalid");
			}
		}
	}

	/**
	 * Reads a registration number from the console and, if it exists in the parking
	 * lot, will set an exit time, calculate parking fare and alter the parking spot
	 * availability to true and log them on the console. Data base values of parking
	 * and ticket tables are updated as well. if the registration does not exist, an
	 * error message is logged on the console.
	 * 
	 */

	public void processExitingVehicle() {
		try {
			Ticket ticket = null; 
			String vehicleRegNumber = getVehichleRegNumber();
			ticket = ticketDAO.getTicket(vehicleRegNumber);
			//ticket.setRecurrentUser(ticketDAO.recurringUserTicket(vehicleRegNumber));

			Calendar outTime = Calendar.getInstance();
			outTime.setTimeInMillis(System.currentTimeMillis());

			ticket.setOutTime(outTime);

			fareCalculatorService.calculateFare(ticket);
			ticketDAO.updateTicket(ticket);
			
			if (ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);
				
				
				DecimalFormat numberFormat = new DecimalFormat("#.00");
				System.out.println("Please pay the parking fare: " + numberFormat.format(ticket.getPrice()));
				System.out.println("Recorded out-time for vehicle number:  " + ticket.getVehicleRegNumber() + "  is:  "
						+ new Timestamp(outTime.getTime().getTime()));

  
			} else {
				System.out.println("Unable to update ticket information. Error occurred");

			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
			// e.printStackTrace();
		}
	}

	
}
