package com.bwssystems.restfulharmony.api;

import static spark.Spark.get;
import static spark.Spark.put;

import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bwssystems.restfulharmony.JsonTransformer;
import com.google.gson.Gson;

import net.whistlingfish.harmony.HarmonyClient;
import net.whistlingfish.harmony.config.Activity;
import net.whistlingfish.harmony.config.Device;

public class HarmonyRest {
    private static final Logger log = LoggerFactory.getLogger(HarmonyRest.class);
    private static final String HARMONY_REST_CONTEXT = "/harmony";
    private HarmonyClient harmonyClient;
    private Boolean noopCalls;

    public HarmonyRest(HarmonyClient theClient, Boolean noopCallsSetting) {
		super();
		noopCalls = noopCallsSetting;
		harmonyClient = theClient;
	}

    //	This function sets up the sparkjava rest calls for the harmony api
    public void setupServer() {
    	log.info("Harmony rest service started....");
    	// http://ip_address:port/harmony/list/activities
	    get(HARMONY_REST_CONTEXT + "/list/activities", "application/json", (request, response) -> {
	        log.debug("Harmony api activities list requested from: " + request.ip());
	        List<Activity> activities = harmonyClient.getConfig().getActivities();
	        response.status(HttpStatus.SC_OK);
	    	return activities;
	    }, new JsonTransformer());

	    // http://ip_address:port/harmony/list/devices
	    get(HARMONY_REST_CONTEXT + "/list/devices", "application/json", (request, response) -> {
	        log.debug("Harmony api device list requested from: " + request.ip());
	        List<Device> devices = harmonyClient.getConfig().getDevices();
	        response.status(HttpStatus.SC_OK);
	    	return devices;
	    }, new JsonTransformer());

	    // http://ip_address:port/harmony/config
	    get(HARMONY_REST_CONTEXT + "/config", "application/json", (request, response) -> {
	        log.debug("Harmony api config requested from: " + request.ip());
	        response.status(HttpStatus.SC_OK);
	    	return harmonyClient.getConfig().toJson();
	    });

	    // http://ip_address:port/harmony/show/activity
	    get(HARMONY_REST_CONTEXT + "/show/activity", "application/json", (request, response) -> {
	        log.debug("Harmony api current sctivity requested from: " + request.ip());
	        Activity activity = harmonyClient.getCurrentActivity();
	        response.status(HttpStatus.SC_OK);
	    	return activity;
	    }, new JsonTransformer());

	    // http://ip_address:port/harmony/start the body is the id of the activity
	    put(HARMONY_REST_CONTEXT + "/start", "application/json", (request, response) -> {
	    	ActivityId anActivity;
	    	String aResponse = "{\"status\": \"OK\"}";
	    	int status_code = HttpStatus.SC_OK;
	        if(request.body() != null && !request.body().isEmpty()) {
	        	anActivity = new Gson().fromJson(request.body(), ActivityId.class);
	            try {
	            	if(noopCalls)
	            		log.info("Noop: start activity call would be: " + anActivity.getActivityid());
	            	else
	            		harmonyClient.startActivity(Integer.parseInt(anActivity.getActivityid()));
	            } catch (IllegalArgumentException e) {
	            	try {
	            		harmonyClient.startActivityByName(anActivity.getActivityid());
	            	} catch (IllegalArgumentException ei) {
	            		aResponse = "{\"status\": \"" + ei.getMessage() + "\"}";
	            		status_code = HttpStatus.SC_NOT_FOUND;
	            	}
	            }
	        }
	        else
	        {
	        	status_code = HttpStatus.SC_NOT_FOUND;
		    	aResponse = "{\"status\": \"null request\"}";
	        }
	        
	        response.status(status_code);
	        return aResponse;
	    });

	    // http://ip_address:port/harmony/press the body is the id of the activity
	    put(HARMONY_REST_CONTEXT + "/press", "application/json", (request, response) -> {
	    	DeviceButton aDeviceButton;
	    	String aResponse = "{\"status\": \"OK\"}";
	    	int status_code = HttpStatus.SC_OK;
	        if(request.body() != null && !request.body().isEmpty()) {
	        	aDeviceButton = new Gson().fromJson(request.body(), DeviceButton.class);
	            try {
	            	if(noopCalls)
	            		log.info("Noop: press call would be device: " + aDeviceButton.getDevice() + " for button: " + aDeviceButton.getButton());
	            	else
	            		harmonyClient.pressButton(Integer.parseInt(aDeviceButton.getDevice()), aDeviceButton.getButton());
	            } catch (IllegalArgumentException e) {
	            	try {
	            		harmonyClient.pressButton(aDeviceButton.getDevice(), aDeviceButton.getButton());
	            	} catch (IllegalArgumentException ei) {
	            		aResponse = "{\"status\": \"" + ei.getMessage() + "\"}";
	            		status_code = HttpStatus.SC_NOT_FOUND;
	            	}
	            }
	        }
	        else
	        {
	        	status_code = HttpStatus.SC_NOT_FOUND;
		    	aResponse = "{\"status\": \"null request\"}";
	        }
	        
	        response.status(status_code);
	        return aResponse;
	    });
    }
}
