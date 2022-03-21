package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfigIT;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDaoTest {
	
	@Test
	public void givenAnInSlotRegNumber_whenAskedFor_thenGetTicketShouldReturnTheTicket() {
		//(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, IN_SLOT)
		//values (3, 'AA', 1.5, '2022-02-13 14:29:32', null, 1)
		
		DataBasePrepareService clearParkingAndTicket = new DataBasePrepareService();
		clearParkingAndTicket.clearDataBaseEntriesAndInsertSame_AA_RegistrationTicket();
		DataBaseTestConfigIT data = new DataBaseTestConfigIT();
		
		ParkingSpot refParkingSpot = new ParkingSpot();
		
		refParkingSpot.setAvailable(false);
		refParkingSpot.setParkingType(ParkingType.CAR);
		refParkingSpot.setId(3);
		
		Ticket refTicket = new Ticket();
		
		refTicket.setParkingSpot(refParkingSpot);
		refTicket.setId(1);
		refTicket.setVehicleRegNumber("AA");
		refTicket.setPrice(1.5);

		Calendar inTime = Calendar.getInstance();
		Timestamp tsIn = Timestamp.valueOf("2022-02-13 14:29:32");
		inTime.setTimeInMillis(tsIn.getTime());
		refTicket.setInTime(inTime);

		Calendar outTime = Calendar.getInstance();
		Timestamp tsOut = Timestamp.valueOf("2022-02-13 15:29:32");
		outTime.setTimeInMillis(tsOut.getTime());// rs.getTimestamp(5).getTime() 
		refTicket.setOutTime(null);
		
		TicketDAO ticketDao = new TicketDAO(data);						
		
		Ticket result = ticketDao.getTicket("AA");
		
		assertThat(result.getPrice()).isEqualTo(refTicket.getPrice()); 
		assertThat(result.getId()).isEqualTo(refTicket.getId()); 

		//assertThat(result.getVehicleRegNumber()).isEqualTo(refTicket.getVehicleRegNumber()); 

	}
	
	@Test
	public void givenARecurringUser_whenHeAsksForHisStatus_thenRecurringUserTicketShouldReturnTrue() {
		
		DataBasePrepareService clearParkingAndTicket = new DataBasePrepareService();
		clearParkingAndTicket.clearDataBaseEntriesAndInsertSame_AA_RegistrationTicket();
		DataBaseTestConfigIT data = new DataBaseTestConfigIT();

		String vehicleRegNumber = "AA";
		TicketDAO ticketDao = new TicketDAO(data);
						
		boolean result = ticketDao.recurringUserTicket(vehicleRegNumber);
		
		assertThat(result).isTrue();
	}
}
