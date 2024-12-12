/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * @author asani
 */
public class OkHttpSingleton {

  private static OkHttpSingleton singletonInstance;

  // No need to be static; OkHttpSingleton is unique so is this.
  private OkHttpClient client;

  // Private so that this cannot be instantiated.
  private OkHttpSingleton() {
    ConnectionPool connectionPool = new ConnectionPool(20, 30, TimeUnit.SECONDS);

    client =
        new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .connectionPool(connectionPool)
            .build();
  }

  public static OkHttpSingleton getInstance() {
    if (singletonInstance == null) {
      singletonInstance = new OkHttpSingleton();
    }
    return singletonInstance;
  }

  // In case you just need the unique OkHttpClient instance.
  public OkHttpClient getClient() {
    return client;
  }

  public void closeConnections() {
    client.dispatcher().cancelAll();
  }
}
