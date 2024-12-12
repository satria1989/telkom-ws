/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

/**
 *
 * @author 12
 */
public class StringManipulation {

    public String getNonNullTrimmedUpper(String value) {
        return (value == null) ? "" : value.toUpperCase().trim();
    }

    public String getNonNullTrimmedUpper(String value, String defaultValue) {
        return (value == null) ? defaultValue : value.toUpperCase().trim();
    }

    public String getNonNullTrimmed(String value) {
        return (value == null) ? "" : value.trim();
    }

    public String getNonNullTrimmed(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value.trim();
    }

}
