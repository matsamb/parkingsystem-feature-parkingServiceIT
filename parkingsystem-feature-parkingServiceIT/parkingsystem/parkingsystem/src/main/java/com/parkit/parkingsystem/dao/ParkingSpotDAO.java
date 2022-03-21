package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig(); 

	
	
	public ParkingSpotDAO () {};
	
	public ParkingSpotDAO (DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}
	
	/**
	 * Starts connection, executes preset parameterized (parameter turned into parking type) query and gets 
	 * the result from the parking table of the prod database. finally, connection is closed.
	 * 
	 * @param parkingType
	 * @return int slot Id if room available
	 */ 

	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();

			PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

	/**
	 * Starts connection, executes preset parameterized ( into parking spot status and Id ) query to update 
	 * parking table available attribute to false for the given Id. Finally connection is closed.
	 * 
	 * @param parkingSpot
	 * @return boolean isAvailable set to false.
	 */
	
	public boolean updateParking(ParkingSpot parkingSpot) {
		// update the availability for that parking slot
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			dataBaseConfig.closePreparedStatement(ps);  
			return (updateRowCount == 1);  
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);  
			return false;
		} finally {
			dataBaseConfig.closeConnection(con);
		}
	}	
}
