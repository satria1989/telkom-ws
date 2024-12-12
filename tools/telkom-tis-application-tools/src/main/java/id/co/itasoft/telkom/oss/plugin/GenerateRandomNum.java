/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

/**
 *
 * @author itasoft
 */
public class GenerateRandomNum {
    
    public static String getNumericRandom(int n) {
        
        // choose a character random
        String numericString = "0123456789";
        
        // create string builder size
        StringBuilder sb = new StringBuilder();
        
        for(int i=0; i<n; i++) {
            int index = (int) (numericString.length() * Math.random());
            sb.append(numericString.charAt(index));        
        }
        
        return sb.toString();
    }
    
    
}
