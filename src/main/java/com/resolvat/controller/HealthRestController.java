package com.resolvat.controller;

import com.resolvat.model.HealthResponse;
import com.resolvat.model.StatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by korteke on 11/03/17.
 */

@RestController
public class HealthRestController {

    @Autowired
    StatusRepo statusRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public HealthResponse healthCheck(HttpServletRequest request, HttpServletResponse response) {


        if (statusRepo.isStatus()) {
            response.setStatus(HttpServletResponse.SC_OK);
            return new HealthResponse(statusRepo.isStatus());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new HealthResponse(statusRepo.isStatus());

        }

    }

}
