package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfigIT;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;


public class ParkingSpotDaoTest {
	
	@AfterAll
	private static void tearDown() {

	}
	
	@Test
	public void givenAnEmptyParkingLot_whenACarCallsForASlot_thenTheSlotIdShouldBeOne() {
		
		DataBasePrepareService clearParkingAndTicket = new DataBasePrepareService();
		clearParkingAndTicket.clearDataBaseEntries();
		DataBaseTestConfigIT data = new DataBaseTestConfigIT();
		ParkingSpotDAO emptyParking = new ParkingSpotDAO(data);
		
						
		int result = emptyParking.getNextAvailableSlot(ParkingType.CAR);
		
		assertThat(result).isEqualTo(1);
	}
	
	@Test
	public void givenAnEmptyParkingLot_whenABikeCallsForASlot_thenTheSlotIdShouldBeFour() {
		
		DataBasePrepareService clearParkingAndTicket = new DataBasePrepareService();
		clearParkingAndTicket.clearDataBaseEntries();
		DataBaseTestConfigIT data = new DataBaseTestConfigIT();
		ParkingSpotDAO emptyParking = new ParkingSpotDAO(data);
		//emptyParking.updateToEmptyParking();
						
		int result = emptyParking.getNextAvailableSlot(ParkingType.BIKE);
		
		assertThat(result).isEqualTo(4);
	}
	
	@Test
	public void givenAnEmptyParkingLot_whenACarCallsForSlotThree_thenUpdateParkingShouldReturnTrue() {
		
		
		DataBasePrepareService clearParkingAndTicket = new DataBasePrepareService();
		clearParkingAndTicket.clearDataBaseEntries();
		DataBaseTestConfigIT data = new DataBaseTestConfigIT();
		ParkingSpotDAO emptyParking = new ParkingSpotDAO(data);
		//emptyParking.updateToEmptyParking();
		
		ParkingSpot parkingSpot = new ParkingSpot(3, ParkingType.CAR,false);
						
		boolean result = emptyParking.updateParking(parkingSpot);
		
		assertThat(result).isTrue();
	}
	@Test
	public void givenAnEmptyParkingLot_whenNoCallsForASlot_thenUpdateParkingShouldReturnFalse() {
		
		
		DataBasePrepareService clearParkingAndTicket = new DataBasePrepareService();
		clearParkingAndTicket.clearDataBaseEntries();
		DataBaseTestConfigIT data = new DataBaseTestConfigIT();
		ParkingSpotDAO emptyParking = new ParkingSpotDAO(data);
		
		//emptyParking.updateToEmptyParking();
		
		ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR,false);
						
		boolean result = emptyParking.updateParking(parkingSpot);
		
		assertThat(result).isFalse();
	}
	
	
}
