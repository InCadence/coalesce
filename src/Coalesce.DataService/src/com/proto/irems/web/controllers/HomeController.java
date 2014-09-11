package com.proto.irems.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.proto.irems.services.monitor.GeneralServices;

@Controller
public class HomeController {

	// @Autowired
	// private ICoalesceDataServiceMonitorDAO cdsDAO;
	GeneralServices genServ;

	@RequestMapping("/")
	public String showHome(Model model) {
		// CallResult.log(CallResults.SUCCESS, "HOME CONTROLLER INIT",
		// "showHome");
		model.addAttribute("ServiceStatus", genServ.getServiceStatus());
		return "home";
	}

	@RequestMapping(value = "/statusChange", method = RequestMethod.POST)
	public void statusChange() {
		// wsStatus = new GeneralServices();
		// wsStatus.setMonitorState(false);
	}

	public HomeController() {
//		ApplicationContext appContext = new ClassPathXmlApplicationContext(
//				"classpath*:/WEB-INF/dataserviceServlet-servlet.xml");
//		genServ = appContext.getBean("generalServices", GeneralServices.class);
		
		genServ = new GeneralServices("java:/comp/env/jdbc/irems");
		/******************** Lets see what the current start state is *************************************************/
		genServ.setServiceStatus(genServ.getServiceState());

		// wsStatus.setMonitorState(wsStatus.setMonitorState(true));
	}
}
