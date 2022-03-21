package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.DBConstants;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfigIT;
import java.sql.Connection;

public class DataBasePrepareService {  

    DataBaseTestConfigIT dataBaseTestConfig = new DataBaseTestConfigIT();

    public void clearDataBaseEntries(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //connection.prepareStatement("use test").execute();
            
            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
    
    public void clearDataBaseEntriesAndInsertSame_AA_RegistrationTicket(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();
            
            //connection.prepareStatement("use test").execute();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();
            
            connection.prepareStatement(DBConstants.INSERT_TWO_TICKET_SAME_AA_REGISTRATION_NUMBER_TEST).execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }


}
