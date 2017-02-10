package org.springframework.samples.mvc.views;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/views/*")
public class ViewsController {
	
	@RequestMapping(value="folding", method=RequestMethod.GET)
	public String folding(Model model) {
		// graph demo
		return "folding";
	}
	
	@RequestMapping(value="editor", method=RequestMethod.GET)
	public String editor(Model model) {
		// graph demo
		return "editor";
	}

}
