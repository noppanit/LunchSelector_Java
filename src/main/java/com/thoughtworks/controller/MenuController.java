package com.thoughtworks.controller;

import com.thoughtworks.model.Menu;
import com.thoughtworks.repository.MenuRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class MenuController {

    private MenuRepository menu = new MenuRepository();

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public String menu(Model model)
    {
        List<Menu> allDishes = menu.getDishes();

        model.addAttribute("dishes", allDishes);
        return "menu";
    }
}
