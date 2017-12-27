package ru.oleg.rsoi.service.mainsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.oleg.rsoi.dto.statistics.StatisticsResponse;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
import ru.oleg.rsoi.service.mainsite.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AdminController {
    @Autowired
    private RemoteGatewayService gatewayService;


    @RequestMapping("/admin")
    public String stats(HttpServletRequest request, HttpServletResponse response, Model model) {
        if (!Util.isAdmin(request, gatewayService)) {
            return "redirect:/forbidden";
        }

        StatisticsResponse stats = gatewayService.statistics();

        model.addAttribute("averagePrice", stats.getAveragePrice());
        model.addAttribute("averageChairs", stats.getAverageChairs());
        model.addAttribute("mostWatchedMovie", stats.getMostWatchedMovie());

        return "stats";
    }

}
