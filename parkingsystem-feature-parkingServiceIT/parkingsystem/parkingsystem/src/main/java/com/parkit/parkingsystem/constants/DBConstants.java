package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking "
    		+ "where AVAILABLE = true and TYPE = ?";
    public static final String GET_AVAILABLE_PARKING_SPOT = "select count(distinct PARKING_NUMBER) "
    		+ "from parking where AVAILABLE = true";
    
    public static final String ALL_SLOT_AVAILABLE_TRUE = "update parking set available = true";
    public static final String TICKET_RESTART = "truncate table ticket";

    public static final String REG_NUMBER_OCCURRENCES_FULL_TICKET_TABLE = "SELECT *,COUNT(vehicle_reg_number) "
    		+ "as occurences FROM ticket GROUP BY vehicle_reg_number ORDER BY occurences DESC";
    public static final String REG_NUMBER_OCCURRENCES = "SELECT vehicle_reg_number, COUNT(vehicle_reg_number) "
    		+ "as occurences FROM ticket GROUP BY vehicle_reg_number ORDER BY occurences DESC";
    
    public static final String GET_THE_FIVE_CURRENT_TICKET = "select * from ticket inner join parking "
    		+ "on ticket.parking_number = parking.parking_number order by in_time desc limit 5";

    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";
    public static final String UPDATE_PARKING_SPOT_TEST_ENTRY = "update parking set available = false where PARKING_NUMBER = 1";

    
    public static final String UPDATE_FULL_PARKING_SPOT_TEST = "update parking, ticket set available = false";
    public static final String UPDATE_FULL_PARKING_SPOT_WITH_SAME_REG_TEST= " update parking, ticket set parking.available = false and ticket.vehicle_reg_number =?";
    public static final String UPDATE_EMPTY_PARKING_SPOT_TEST = "update parking set available = true";
    
    public static final String INSERT_TWO_TICKET_SAME_AA_REGISTRATION_NUMBER_TEST = " insert into ticket "
    		+ "(PARKING_NUMBER,VEHICLE_REG_NUMBER,PRICE,IN_TIME,OUT_TIME,IN_SLOT) "
    		+ "values (3,'AA',1.5,'2022-02-13 14:29:32',null,1), "
    		+ "(2,'AA',1.65,'2022-02-13 14:39:32','2022-02-13 15:49:32',0);";
    public static final String COUNT_HOW_MANY_TIMES_REG_NUMBER_PARKED = "SELECT COUNT(ticket.vehicle_reg_number) "
    		+ "AS count FROM ticket WHERE ticket.vehicle_reg_number =?"
    		+ " GROUP BY ticket.vehicle_reg_number ORDER BY count DESC";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, "
    		+ "PRICE, IN_TIME, OUT_TIME, IN_SLOT) values(?,?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=?, IN_SLOT = false where ID=?";
    public static final String GET_TICKET = "select ticket.PARKING_NUMBER, ticket.ID, ticket.PRICE, ticket.IN_TIME, ticket.OUT_TIME, "
    		+ "parking.TYPE from ticket, parking where parking.parking_number = ticket.parking_number and "
    		+ "ticket.VEHICLE_REG_NUMBER=? and ticket.in_slot = true order by ticket.IN_TIME  limit 1 ";
}
