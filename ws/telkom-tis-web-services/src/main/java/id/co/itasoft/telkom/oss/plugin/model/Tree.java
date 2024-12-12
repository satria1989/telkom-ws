/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author itasoft
 */
public class Tree {
    private String parent;
    private String classificationCode;
    private String description;
    private String classificationType;
    private String autoBackend;
    private String solution;
    private String finalCheck;
    private boolean hasChildren;
    private String classstructureId;

    public String getClassstructureId() {
        return classstructureId;
    }

    public void setClassstructureId(String classstructureId) {
        this.classstructureId = classstructureId;
    }

    
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
