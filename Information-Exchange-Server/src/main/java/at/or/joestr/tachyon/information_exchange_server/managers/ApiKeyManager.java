/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.or.joestr.tachyon.information_exchange_server.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles interactions for API keys.
 * 
 * @author Joel
 */
public class ApiKeyManager {
    private static ApiKeyManager INSTANCE;
    
    private final PooledDatabaseConnection pDC;
    
    private ApiKeyManager(PooledDatabaseConnection pDC) {
        this.pDC = pDC;
    }
    
    public static ApiKeyManager getInstance() {
        if(INSTANCE == null)
            throw new IllegalStateException(
                "APIKeyManager has not been initialized yet!"
            );
        
        return INSTANCE;
    }
    
    public static ApiKeyManager getInstance(PooledDatabaseConnection pDC) throws SQLException {
        if(INSTANCE != null) {
            throw new IllegalStateException(
                "APIKeyManager has already been initialized!"
            );
        }
        
        Statement s = pDC.getConnection().createStatement();
        ResultSet r = s.executeQuery("SHOW TABLES LIKE 'tachyon_apikeys';");
        int row = r.getRow();
        
        if(row == 0) {
            throw new IllegalStateException(
                "The table 'tachyon_apikeys' has not been found!"
            );
        }
        
        INSTANCE = new ApiKeyManager(pDC);
        
        return INSTANCE;
    }
    
    public boolean isPermitted(String apiKey, String category) throws SQLException {
        
        PreparedStatement pS = pDC.getConnection().prepareStatement(
            "SELECT * FROM `tachyon_apikeys` WHERE `apikey` = ?;"
        );
        
        pS.setString(0, apiKey);
        ResultSet rs = pS.executeQuery();
        
        List<String> allowedCategories = new ArrayList<>();
        while(rs.next()) {
            allowedCategories.add(
                rs.getString(2)
            );
        }
        
        return allowedCategories.contains("*") || allowedCategories.contains(category);
    }
}