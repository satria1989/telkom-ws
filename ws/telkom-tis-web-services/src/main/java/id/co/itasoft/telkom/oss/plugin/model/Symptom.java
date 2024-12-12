/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author tarkiman
 */
public class Symptom {

    private String parent;
    private String classificationCode;
    private String description;
    private String classificationType;
    private String autoBackend;
    private String solution;
    private String finalCheck;
    private boolean hasChildren;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(String classificationType) {
        this.classificationType = classificationType;
    }

    public String getAutoBackend() {
        return autoBackend;
    }

    public void setAutoBackend(String autoBackend) {
        this.autoBackend = autoBackend;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getFinalCheck() {
        return finalCheck;
    }

    public void setFinalCheck(String finalCheck) {
        this.finalCheck = finalCheck;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
