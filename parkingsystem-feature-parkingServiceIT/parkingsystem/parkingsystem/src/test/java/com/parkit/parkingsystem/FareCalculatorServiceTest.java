package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	Calendar inTime = Calendar.getInstance();
	Calendar outTime = Calendar.getInstance();



	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}
	
	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void calculateFareCar() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
		//Calendar outTime = Calendar.getInstance();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	public void calculateFareBike() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	public void calculateFareUnkownType() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithFutureInTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() + (60 * 60 * 1000));
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));

	}

	@Test
	public void calculateFareCarForOutTimeBeforIntime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() + (60 * 60 * 1000));// leave 1 hour before enter parking time
																				// should give 24 *
																				// parking fare per hour
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));

	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();

		inTime.setTimeInMillis(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give
																				// 3/4th
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateRecuringUserFareBikeWithLessThanOneHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();

		inTime.setTimeInMillis(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give
																				// 3/4th
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setRecurrentUser(true);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals((0.75 * 0.95 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give
																				// 3/4th
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateRecurringUserFareCarWithLessThanOneHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give
																				// 3/4th
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setRecurrentUser(true);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals((0.75 * 0.95 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanHalfHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();

		inTime.setTimeInMillis(System.currentTimeMillis() - (29 * 60 * 1000));// 29 minutes parking time should give 0
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals(0, ticket.getPrice());
	}

	@Test
	public void calculateRecurringUserFareBikeWithLessThanHalfHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();

		inTime.setTimeInMillis(System.currentTimeMillis() - (29 * 60 * 1000));// 29 minutes parking time should give 0
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setRecurrentUser(true);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals(0, ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanHalfHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (29 * 60 * 1000));// 29 minutes parking time should give 0
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals(0, ticket.getPrice());
	}

	@Test
	public void calculateRecurringUserFareCarWithLessThanHalfHourParkingTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (29 * 60 * 1000));// 29 minutes parking time should give 0
		// parking fare
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setRecurrentUser(true);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals(0, ticket.getPrice());
	}

	// @Disabled
	@Test
	public void calculateFareBikeWithMoreThanADayParkingTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time should give
																					// 24 *
		// parking fare per hour
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals((24 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	// @Disabled
	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {
		//Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time should give
																					// 24 *
		// parking fare per hour
		//Calendar outTime = Calendar.getInstance();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

}
