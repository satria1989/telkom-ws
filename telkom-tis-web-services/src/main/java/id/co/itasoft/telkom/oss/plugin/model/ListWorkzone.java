/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 * @author mtaup
 */
public class ListWorkzone {
    String stoCode;
    String stoName;
    String witel;
    String region;

    public String getWitel() {
        return witel;
    }

    public void setWitel(String witel) {
        this.witel = witel;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStoCode() {
        return stoCode;
    }

    public void setStoCode(String stoCode) {
        this.stoCode = stoCode;
    }

    public String getStoName() {
        return stoName;
    }

    public void setStoName(String stoName) {
        this.stoName = stoName;
    }

}
