package org.openiot.gsn.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {
   
   public boolean isValid(HttpServletRequest request,HttpServletResponse response) throws IOException;
   
   public void handle(HttpServletRequest request,HttpServletResponse response) throws IOException;
}
