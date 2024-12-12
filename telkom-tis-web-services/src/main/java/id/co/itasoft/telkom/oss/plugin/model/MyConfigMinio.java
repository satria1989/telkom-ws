/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin.model;

/**
 * @author asani
 */
public class MyConfigMinio {

  private String urlMinio;
  private String bucket;
  private String userName;
  private String password;

  @Override
  public String toString() {
    return "MyConfigMinio{" + "urlMinio=" + urlMinio + ", bucket=" + bucket + ", userName=" + userName + ", password=" + password + '}';
  }

  
  public MyConfigMinio(String urlMinio, String userName, String password, String bucket) {
        this.urlMinio = urlMinio;
        this.userName = userName;
        this.password = password;
        this.bucket = bucket;
    }

  
  public String getUrlMinio() {
    return urlMinio;
  }

  public void setUrlMinio(String urlMinio) {
    this.urlMinio = urlMinio;
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  
}
