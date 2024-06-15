package com.example.uberprojectbookingservice.controllers;

import com.example.uberprojectbookingservice.apis.LocationServiceApi;
import com.example.uberprojectbookingservice.apis.UberSocketApi;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Autowired
    private EurekaClient eurekaClient;

    private String getServiceUrl(String serviceName){
        return eurekaClient.getNextServerFromEureka(serviceName,false).getHomePageUrl();
    }

    @Bean
    public LocationServiceApi locationServiceApi(){
        return new Retrofit.Builder()
                .baseUrl(getServiceUrl("UBERPROJECT-LOCATIONSERVICE"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationServiceApi.class);
    }

    @Bean
    public UberSocketApi uberSocketApi(){
        return new Retrofit.Builder()
                .baseUrl(getServiceUrl("UBERPROJECTSPRINGSOCKET"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UberSocketApi.class);
    }

}

