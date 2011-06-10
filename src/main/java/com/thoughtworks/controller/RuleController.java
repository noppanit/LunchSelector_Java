package com.thoughtworks.controller;

import com.thoughtworks.model.Rule;
import com.thoughtworks.repository.RuleRepository;
import org.springframework.stereotype.Controller;
import com.thoughtworks.model.Menu;
import com.thoughtworks.repository.MenuRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/rules")
public class RuleController {

    @RequestMapping(method = GET)
    public String list(Model model) throws Exception {
        RuleRepository ruleRepository = new RuleRepository();
        List<Rule> listOfRules = ruleRepository.getRules();

        model.addAttribute("rules", listOfRules);
        return "rules";
    }

    @RequestMapping(value = "add", method = GET)
    public String add() {

        return "ruleAdd";
    }

}
