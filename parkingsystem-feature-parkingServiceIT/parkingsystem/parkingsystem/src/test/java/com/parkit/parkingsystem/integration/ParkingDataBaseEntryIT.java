package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfigIT;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseEntryIT {

	private static DataBaseTestConfigIT dataBaseTestConfig = new DataBaseTestConfigIT();
	private static ParkingSpotDAO parkingSpotDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
		ticketDAO = new TicketDAO(dataBaseTestConfig);
		dataBasePrepareService = new DataBasePrepareService();
	}

	@AfterAll
	private static void tearDown() {

	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("reg");

		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Ticket ticket = ticketDAO.getTicket("reg");
		boolean available = ticketDAO.getTicket("reg").getParkingSpot().isAvailable();

		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		assertThat(ticket.getId()).isEqualTo(1);
		assertThat(available).isFalse();

	}

}
