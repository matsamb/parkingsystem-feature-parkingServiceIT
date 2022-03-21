package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@Nested
	class processIncomingVehicleNestTest {
		@BeforeEach
		private void setUpPerTest() {
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

				ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
				Ticket ticket = new Ticket();

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));// 1 hour stay

				ticket.setInTime(cal);
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber("ABCDEF");

				when(inputReaderUtil.readSelection()).thenReturn(1);
				when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

				when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

				parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
		}
		@Test
		public void processIncomingVehicleTest() {
			int result = parkingService.processIncomingVehicle();

			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
			assertThat(result).isEqualTo(1);
		}
	}

	@Nested
	class processExitingVehicleNestTest {
		@BeforeEach
		private void setUpPerTest() {
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

				ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
				Ticket ticket = new Ticket();

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));// 1 hour stay

				ticket.setInTime(cal);
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber("ABCDEF");

				when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
				when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

				when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

				parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
		}

		@Test
		public void processExitingVehicleTest() {

			parkingService.processExitingVehicle();
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		}
	}
}
