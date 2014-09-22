package com.feihuwang.controller;

import org.glassfish.jersey.server.ResourceConfig;

public class ControllerRegister extends ResourceConfig{
   public ControllerRegister () {
       this.packages("com.feihuwang.controller");
   }
}
