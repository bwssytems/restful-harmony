package com.bwssystems.restfulharmony;

import com.google.gson.Gson;
import spark.ResponseTransformer;
/*
 * Implementation of a Json renderer through google GSON utility.
 */
public class JsonTransformer implements ResponseTransformer {

   private Gson gson = new Gson();

   @Override
   public String render(Object model) {
       return gson.toJson(model);
   }

}