package com.bwssystems.restfulharmony;

import static java.lang.String.format;
import static spark.Spark.*;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bwssystems.restfulharmony.api.DevModeResponse;
import com.bwssystems.restfulharmony.api.HarmonyRest;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.whistlingfish.harmony.ActivityChangeListener;
import net.whistlingfish.harmony.HarmonyClient;
import net.whistlingfish.harmony.HarmonyClientModule;
import net.whistlingfish.harmony.config.Activity;

public class HarmonyRestServer {
    @Inject
    private HarmonyClient harmonyClient;
    
    private HarmonyRest harmonyApi;
    
    private DevModeResponse devResponse;
    
    private static Boolean devMode;
    private static Boolean noopCalls;
    private static Logger log;

    public static void main(String[] args) throws Exception {
        log = LoggerFactory.getLogger(HarmonyRestServer.class);
        Version theVersion;
        
        theVersion = new Version();
    	devMode = Boolean.parseBoolean(System.getProperty("dev.mode", "false"));
        noopCalls = Boolean.parseBoolean(System.getProperty("noop.calls", "false"));
        String modeString = "";
        if(devMode)
        	modeString = " (development mode)";
        if(noopCalls)
        	modeString = " (no op calls to harmony)";
        log.info("Harmony v" + theVersion.getVersion() + " rest server running" + modeString + "....");
    	Injector injector = null;
    	if(!devMode)
            injector = Guice.createInjector(new HarmonyClientModule());
        HarmonyRestServer mainObject = new HarmonyRestServer();
    	if(!devMode)
    		injector.injectMembers(mainObject);
        System.exit(mainObject.execute(args));
    }

    public int execute(String[] args) throws Exception {
        if(devMode)
        {
        	harmonyClient = null;
        	devResponse = new DevModeResponse();
        }
        else {
        	devResponse = null;
	        harmonyClient.addListener(new ActivityChangeListener() {
	            @Override
	            public void activityStarted(Activity activity) {
	                log.info(format("activity changed: [%d] %s", activity.getId(), activity.getLabel()));
	            }
	        });
	        harmonyClient.connect(args[0]);
        }
        port(Integer.valueOf(System.getProperty("server.port", "8081")));
        int sleepTime = Integer.parseInt(System.getProperty("button.sleep", "100"));
        harmonyApi = new HarmonyRest(harmonyClient, noopCalls, sleepTime, devResponse);
        harmonyApi.setupServer();
        while(true)
        {
        	//no op
        	Thread.sleep(100000);
        }
    }
}
