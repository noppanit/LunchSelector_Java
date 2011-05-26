package com.thoughtworks.controller;

import com.thoughtworks.model.Menu;
import com.thoughtworks.repository.MenuRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProcessController {

    @RequestMapping(value = "startprocess", method = RequestMethod.GET)
    public String startProcess(Model model)
    {
        return "startProcess";
    }


}
