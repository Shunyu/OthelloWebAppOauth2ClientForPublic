package com.beautifulsetouchi.AiOthelloGameClientServer.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OthelloController {
	
	@GetMapping("/hello")
	public String getHellow(){
		return "hello";
	}
	
	@PostMapping("/hello")
	public String postRequest(@RequestParam("text1") String str, Model model) {
		model.addAttribute("sample", str);
		
		return "helloResponse";
	}
	
	@GetMapping("/othello/homepage")
	public String getOthelloHomepage() {
		
		return "index";
		
	}
	
	@GetMapping("/")
	public String getRoot() {
		
		return "forward:othello/homepage";
	}
	
}
	