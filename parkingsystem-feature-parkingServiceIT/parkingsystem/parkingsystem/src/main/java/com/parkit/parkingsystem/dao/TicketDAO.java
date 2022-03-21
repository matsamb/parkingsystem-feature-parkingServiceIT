package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 
 * @author matlu
 * 
 */
 
public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	
	public TicketDAO () {};
	
	public TicketDAO (DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}
	
	/**
	 * 
	 * @param ticket
	 * @return
	 */
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);

			// Timestamp tsp = new Timestamp(2);
			// tsp.sett
			// Date.
			// ps.setTimestamp(0, null);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, IN_SLOT)
			// ps.setInt(1,ticket.getId());
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());

			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTimeInMillis()));
			ps.setTimestamp(5,
					(ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTimeInMillis())));
			ps.setBoolean(6, true);

			return ps.execute();
			
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * 
	 * @param vehicleRegNumber
	 * @return
	 */
	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Ticket ticket = null; 
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			// 1

			rs = ps.executeQuery(); 
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));

				Calendar inTime = Calendar.getInstance();
				inTime.setTimeInMillis(rs.getTimestamp(4).getTime());
				ticket.setInTime(inTime);

				Calendar outTimeGt = Calendar.getInstance();
				outTimeGt.setTimeInMillis(System.currentTimeMillis());// rs.getTimestamp(5).getTime()
				ticket.setOutTime(outTimeGt);
				return ticket;
			}
			
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);  
		}
		return ticket;
	}

	/**
	 * 
	 * @param ticket
	 * @return
	 */
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTimeInMillis()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	public boolean recurringUserTicket(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.COUNT_HOW_MANY_TIMES_REG_NUMBER_PARKED);
			ps.setString(1, vehicleRegNumber);

			rs = ps.executeQuery();
 
			rs.next();
			int occurrence = rs.getInt(1);
			if (occurrence > 1) {
				logger.info("Welcome back! As a recurring user of our parking lot, you'll benefit from a"
						+" * 5% discount.");
				result = true;
				System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a\r\n"
								+ "	 * 5% discount.");
			}
			
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

}