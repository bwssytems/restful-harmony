package com.bwssystems.restfulharmony.api;

import static spark.Spark.get;
import static spark.Spark.put;
import static spark.Spark.options;

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
    private Boolean devMode;
    private DevModeResponse devResponse;
    private int sleepTime;

    public HarmonyRest(HarmonyClient theClient, Boolean noopCallsSetting, int sleepMillis, DevModeResponse devResponseSetting) {
		super();
		noopCalls = noopCallsSetting;
		devMode = Boolean.TRUE;
		devResponse = null;
		if(devResponseSetting == null)
			devMode = Boolean.FALSE;
		else
			devResponse = devResponseSetting;
		sleepTime = sleepMillis;
		harmonyClient = theClient;
	}

    //	This function sets up the sparkjava rest calls for the harmony api
    public void setupServer() {
    	log.info("Harmony rest service started....");
    	// http://ip_address:port/harmony/list/activities
	    get(HARMONY_REST_CONTEXT + "/list/activities", "application/json", (request, response) -> {
	        log.debug("Harmony api activities list requested from: " + request.ip());
	        List<Activity> activities = null;
	        if(devMode)
	        	activities = devResponse.getActivities();
	        else
	        	activities = harmonyClient.getConfig().getActivities();
	        response.status(HttpStatus.SC_OK);
	    	return activities;
	    }, new JsonTransformer());

	    // http://ip_address:port/harmony/list/devices
	    get(HARMONY_REST_CONTEXT + "/list/devices", "application/json", (request, response) -> {
	        log.debug("Harmony api device list requested from: " + request.ip());
	        List<Device> devices = null;
	        if(devMode)
	        	devices = devResponse.getDevices();
	        else
	        	devices = harmonyClient.getConfig().getDevices();
	        response.status(HttpStatus.SC_OK);
	    	return devices;
	    }, new JsonTransformer());

	    // http://ip_address:port/harmony/config
	    get(HARMONY_REST_CONTEXT + "/config", "application/json", (request, response) -> {
	        log.debug("Harmony api config requested from: " + request.ip());
	        String theResponse = null;
	        if(devMode)
	        	theResponse = devResponse.getConfig().toJson();
	        else
	        	theResponse = harmonyClient.getConfig().toJson();
	        response.status(HttpStatus.SC_OK);
	    	return theResponse;
	    });

	    // http://ip_address:port/harmony/show/activity
	    get(HARMONY_REST_CONTEXT + "/show/activity", "application/json", (request, response) -> {
	        log.debug("Harmony api current sctivity requested from: " + request.ip());
	        Activity activity = null;
	        if(devMode)
	        	activity = devResponse.getCurrentActivity();
	        else
	        	activity = harmonyClient.getCurrentActivity();
	        response.status(HttpStatus.SC_OK);
	    	return activity;
	    }, new JsonTransformer());

	    // http://ip_address:port/harmony/start CORS request
	    options(HARMONY_REST_CONTEXT + "/start", "application/json", (request, response) -> {
	        response.status(HttpStatus.SC_OK);
	        response.header("Access-Control-Allow-Origin", request.headers("Origin"));
	        response.header("Access-Control-Allow-Methods", "GET, POST, PUT");
	        response.header("Access-Control-Allow-Headers", request.headers("Access-Control-Request-Headers"));
	        response.header("Content-Type", "text/html; charset=utf-8");
        	if(noopCalls)
        		log.info("Noop: setting options response and sending");
	    	return "";
	    });
	    // http://ip_address:port/harmony/start the body is the id of the activity
	    put(HARMONY_REST_CONTEXT + "/start", "application/json", (request, response) -> {
	    	ActivityId anActivity;
	    	String aResponse = "{\"status\": \"OK\"}";
	    	int status_code = HttpStatus.SC_OK;
	        if(request.body() != null && !request.body().isEmpty()) {
	        	anActivity = new Gson().fromJson(request.body(), ActivityId.class);
	            try {
	            	if(noopCalls || devMode)
	            	{
	            		log.info("Noop: start activity call would be: " + anActivity.getActivityid());
	            		if(devMode)
	            		{
	            			if(Integer.getInteger(anActivity.getActivityid()) == null)
	            				devResponse.setCurrentActivity(devResponse.getConfig().getActivityByName(anActivity.getActivityid()));
	            			else
	            				devResponse.setCurrentActivity(devResponse.getConfig().getActivityById(Integer.getInteger(anActivity.getActivityid()).intValue()));
	            		}
	            	}
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
	        
	        response.header("Access-Control-Allow-Origin", request.headers("Origin"));
	        response.status(status_code);
	        return aResponse;
	    });

	    // http://ip_address:port/harmony/start CORS request
	    options(HARMONY_REST_CONTEXT + "/press", "application/json", (request, response) -> {
	        response.status(HttpStatus.SC_OK);
	        response.header("Access-Control-Allow-Origin", request.headers("Origin"));
	        response.header("Access-Control-Allow-Methods", "GET, POST, PUT");
	        response.header("Access-Control-Allow-Headers", request.headers("Access-Control-Request-Headers"));
	        response.header("Content-Type", "text/html; charset=utf-8");
        	if(noopCalls)
        		log.info("Noop: setting options response and sending");
	    	return "";
	    });
	    // http://ip_address:port/harmony/press the body is the id of the activity
	    put(HARMONY_REST_CONTEXT + "/press", "application/json", (request, response) -> {
	    	DeviceButton aDeviceButtons[];
	    	String theArguments = request.body();
	    	String aResponse = "{\"status\": \"OK\"}";
        	Integer theDelay = sleepTime;
	    	int status_code = HttpStatus.SC_OK;
	        if(request.body() != null && !request.body().isEmpty()) {
	        	if(theArguments.substring(0, 1).equalsIgnoreCase("{")) {
	        		theArguments = "[" + theArguments +"]";
	        	}
	        	aDeviceButtons = new Gson().fromJson(theArguments, DeviceButton[].class);
	        	Integer setCount = 1;
        		for(int i = 0; i < aDeviceButtons.length; i++) {
	        		if(aDeviceButtons[i].getCount() != null && aDeviceButtons[i].getCount() > 0)
	        			setCount = aDeviceButtons[i].getCount();
	        		else
	        			setCount = 1;
	        		for(int x = 0; x < setCount; x++) {
	        			if( x > 0 || i > 0)
	        				Thread.sleep(theDelay);
	        			if(aDeviceButtons[i].getDelay() != null && aDeviceButtons[i].getDelay() > 0)
	        				theDelay = aDeviceButtons[i].getDelay();
	        			else
	        				theDelay = sleepTime;
			            try {
			            	if(noopCalls || devMode)
			            		log.info("Noop: press call would be device: " + aDeviceButtons[i].getDevice() + " for button: " + aDeviceButtons[i].getButton());
			            	else
			            		harmonyClient.pressButton(Integer.parseInt(aDeviceButtons[i].getDevice()), aDeviceButtons[i].getButton());
			            } catch (IllegalArgumentException e) {
			            	try {
			            		harmonyClient.pressButton(aDeviceButtons[i].getDevice(), aDeviceButtons[i].getButton());
			            	} catch (IllegalArgumentException ei) {
			            		aResponse = "{\"status\": \"" + ei.getMessage() + "\"}";
			            		status_code = HttpStatus.SC_NOT_FOUND;
			            	}
			            }
	        		}
        		}
	        }
	        else
	        {
	        	status_code = HttpStatus.SC_NOT_FOUND;
		    	aResponse = "{\"status\": \"null request\"}";
	        }
	        
	        response.header("Access-Control-Allow-Origin", request.headers("Origin"));
 	        response.status(status_code);
	        return aResponse;
	    });
    }
}
