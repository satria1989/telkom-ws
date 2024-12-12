/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author asani
 */
public enum ListEnum {
     // STATUS
    NEW,
    ANALYSIS,
    BACKEND,
    DRAFT,
    PENDING,
    FINALCHECK,
    RESOLVED,
    MEDIACARE,
    SALAMSIM,
    CLOSED,
    REQUESTPENDING,
    
    // ACTION STATUS
    REDISPATCH_BY_SELECT_SOLUTION,
    RESOLVE,
    TOSALAMSIM,
    REASSIGN_OWNERGROUP,
    REQUEST_PENDING,
    REOPEN,
    SENDTOANALYSIS,
    AFTERPENDING,
    AFTER_REQUEST,
    DEADLINETOSALAMSIM,
    DEADLINETOCLOSED,
    DEADLINETOANALYSIS,
    DEADLINETOMEDIACARE,
    SQMTOCLOSED,
    CLOSEDMASSAL,
    RESOLVEMASAL
}
