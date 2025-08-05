package com.pp_web_project.controller;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @GetMapping
    public Object handleError(HttpServletRequest req, Model model) {
        int status = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // 브라우저? → HTML, 그 외 → JSON
        if (acceptsHtml(req)) {
            return "error/" + status;
        }
        Map<String,Object> body = Map.of(
                "status", status,
                "error",  HttpStatus.valueOf(status).getReasonPhrase(),
                "path",   req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        return ResponseEntity.status(status).body(body);
    }


    /** Accept 헤더에 text/html 이 포함돼 있으면 true */
    private boolean acceptsHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept == null || accept.contains("text/html");
    }
}


