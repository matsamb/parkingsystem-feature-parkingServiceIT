package com.parkit.parkingsystem.service;

import java.util.Calendar;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * 
 * Operates parking stays related calculations.
 * 
 * @author matlu
 *
 */
public class FareCalculatorService {

	/**
	 * 
	 * Calculates cars or bikes parking stay's fare. Less than 30 minutes stays are
	 * free. From second stay, recurring users benefit a 5 % discount on fare.
	 * 
	 * @param ticket
	 */
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// times for up to one year of fare calculation
		double inDay = ticket.getInTime().get(Calendar.DAY_OF_YEAR);
		double outDay = ticket.getOutTime().get(Calendar.DAY_OF_YEAR);
		double inHour = ticket.getInTime().get(Calendar.HOUR_OF_DAY);
		double outHour = ticket.getOutTime().get(Calendar.HOUR_OF_DAY);
		double inMinute = ticket.getInTime().get(Calendar.MINUTE);
		double outMinute = ticket.getOutTime().get(Calendar.MINUTE);
 
		// constants to calculate day or hour change crossing stays
		final double DAY_END = 24;
		final double DAY_START = 0;
		final double MINUTE_END = 60;
		final double MINUTE_START = 0;
		final double FREE_TIME = 30;
		final double RECURRENT_USER = 0.95;

		double dayDuration = outDay - inDay;
		double hourDurationDifferentDays = (dayDuration - 1) * DAY_END + (outHour - DAY_START) + (DAY_END - inHour);
		double minuteDurationDifferentHours = (hourDurationDifferentDays - 1) * MINUTE_END + (outMinute - MINUTE_START)
				+ (MINUTE_END - inMinute);

		if (ticket.getRecurrentUser() == true) {
			//System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a\r\n"
			//		+ "	 * 5% discount.");
			switch (ticket.getParkingSpot().getParkingType()) {
				case CAR: {
					ticket.setPrice(MINUTE_START);
					if (minuteDurationDifferentHours > FREE_TIME) {
						ticket.setPrice((minuteDurationDifferentHours / 60) * RECURRENT_USER * Fare.CAR_RATE_PER_HOUR);
					}
					break;
				}

				case BIKE: {
					ticket.setPrice(MINUTE_START);
					if (minuteDurationDifferentHours > FREE_TIME) {
						ticket.setPrice((minuteDurationDifferentHours / 60) * RECURRENT_USER * Fare.BIKE_RATE_PER_HOUR);
					}
					break;
				}
				default:
					throw new IllegalArgumentException("Unknown Parking Type");
			}
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
				case CAR: {
					ticket.setPrice(MINUTE_START);
					if (minuteDurationDifferentHours > FREE_TIME) {
						ticket.setPrice((minuteDurationDifferentHours / 60) * Fare.CAR_RATE_PER_HOUR);
					}
					break;
				}
				case BIKE: {
					ticket.setPrice(MINUTE_START);
					if (minuteDurationDifferentHours > FREE_TIME) {
						ticket.setPrice((minuteDurationDifferentHours / 60) * Fare.BIKE_RATE_PER_HOUR);
					}
					break;
				}
				default:
					throw new IllegalArgumentException("Unknown Parking Type");
			}
		}
	}
}