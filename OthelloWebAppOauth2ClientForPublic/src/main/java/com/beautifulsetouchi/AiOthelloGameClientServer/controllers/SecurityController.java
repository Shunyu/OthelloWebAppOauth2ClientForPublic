package com.beautifulsetouchi.AiOthelloGameClientServer.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;

import com.beautifulsetouchi.AiOthelloGameClientServer.models.AiOthelloRequestResource;
import com.beautifulsetouchi.AiOthelloGameClientServer.models.AiOthelloResponseResource;
import com.beautifulsetouchi.AiOthelloGameClientServer.models.GameResultRequestResource;
import com.beautifulsetouchi.AiOthelloGameClientServer.models.GameSituation;
import com.beautifulsetouchi.AiOthelloGameClientServer.models.LoginUserAiOthelloResponseResource;
import com.beautifulsetouchi.AiOthelloGameClientServer.services.AiOthelloService;

import reactor.core.publisher.Mono;

/**
 * ログイン後盤面画面（AIモード）では対戦成績も表示されるようになっている。
 * 
 * そのようなログイン後盤面画面などのレスポンスや
 * 対戦成績の取得・最良手の取得時に発生する
 * リソースサーバーへのリクエストの仲介を実施するコントローラークラス
 * @author shunyu
 *
 */
@Controller
public class SecurityController {

	@Autowired
	private final WebClient webClient;
	@Autowired
	private final AiOthelloService aiOthelloService;
	
	private final static String playresultUpdateUrl = "xxxx";
	private final static String playresultGetUrl = "xxxx"; 
	private final static String bestmoveGetUrl = "xxxx";

	public SecurityController(
			WebClient webClient,
			AiOthelloService aiOthelloService
			) {
		this.webClient = webClient;
		this.aiOthelloService = aiOthelloService;
	}
	
	/**
	 * ウェブページ"about"の返却を行う。
	 * @param type
	 * @return
	 */
	@GetMapping("/othello/about")
	public String getHomepageInfo(@RequestParam(name = "type") String type) {
		
		Object authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean registeredUserFlag = false;
		if (authentication instanceof AnonymousAuthenticationToken){
			registeredUserFlag = false;
		} else {
			registeredUserFlag = true;
		}

		System.out.println("registeredUserFlag on about:"+registeredUserFlag);
				
		
		
		if (type.equals("anonymous") && !registeredUserFlag) {
			System.out.println("case1");
			return "about";
			
		} else if (type.equals("registered") && registeredUserFlag) {
			System.out.println("case2");
			return "about";
						
		} else {
			System.out.println("case3");
			return "invalid";
			
		}
		
	}

	/**
	 * JavaScriptから対戦成績のアップデートを行うリクエストを行う際に使われる。
	 * リソースサーバーへのリクエストの仲介を実施している。
	 * 
	 * SecurityRestControllerクラスに移行すべき。
	 * @param gameResultRequestResource
	 */
	@PostMapping("/ai-othello/v2/playresult/update")
	@ResponseStatus(value = HttpStatus.OK)
	public void postGameResult(
			@RequestBody GameResultRequestResource gameResultRequestResource
			) {

		// BODYには、ゲーム結果の妥当性の判定に必要なデータ（盤面推移）も含める。
		// ログインされていない場合には、何もしない。
		// ログインしている場合には、実際の妥当性判定を行うリソースサーバーに、リクエストを仲介する。
		
		System.out.println("/user/v2/playresult/update");
		String gameresult = gameResultRequestResource.getGameresult();
		System.out.println("gameresult: "+gameresult);
		List<GameSituation> gameSituationList = gameResultRequestResource.getGameSituationList();
		System.out.println("gameSituationList: "+gameSituationList);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean registeredUserFlag = false;
		if (authentication instanceof AnonymousAuthenticationToken) {
			registeredUserFlag = false;
		} else {
			registeredUserFlag = true;
		}
		System.out.println("registeredUserFlag on result:"+registeredUserFlag);
		
		System.out.println("othelloMode: ai");
		if (registeredUserFlag) {

			String userName = authentication.getName();
			System.out.println("userId: "+userName);
			
			System.out.println("before post othello result");

			Map<String, Object> playResult = webClient.post()
					.uri(playresultUpdateUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.body(Mono.just(gameResultRequestResource),GameResultRequestResource.class)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
					.block();
			
			System.out.println("after post othello result");
			System.out.println("playResult: "+playResult);
		
		} else {
			System.out.println("unregistered: anonymous user");
		}
		
		return; 
	}
	
	/**
	 * ウェブページ"result"の返却を行う。
	 * @param othelloMode
	 * @param type
	 * @param blackNum
	 * @param whiteNum
	 * @param model
	 * @return
	 */
	@GetMapping("/othello/result/{othelloMode}")
	public String getOthelloResult(
			@PathVariable("othelloMode") String othelloMode, 
			@RequestParam(name = "type") String type, 
			@RequestParam(name = "blackNum") String blackNum, 
			@RequestParam(name = "whiteNum") String whiteNum,
			Model model
			) {
		
		model.addAttribute("othelloMode", othelloMode);
		model.addAttribute("type", type);
		model.addAttribute("blackNum", blackNum);
		model.addAttribute("whiteNum", whiteNum);
		
		System.out.println("/othello/result/othelloMode");
		System.out.println("othelloMode:"+othelloMode);
		System.out.println("type:"+type);
		System.out.println("blackNum:"+blackNum);
		System.out.println("whiteNum:"+whiteNum);
		
		String winner = "draw";
		Integer blackNumInteger = Integer.parseInt(blackNum);
		Integer whiteNumInteger = Integer.parseInt(whiteNum);
		if (blackNumInteger > whiteNumInteger) {
			winner = "black";
		} else if (blackNumInteger < whiteNumInteger) {
			winner = "white";
		} else {
			winner = "draw";
		}
		model.addAttribute("winner", winner);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean registeredUserFlag = false;
		if (authentication instanceof AnonymousAuthenticationToken) {
			registeredUserFlag = false;
		} else {
			registeredUserFlag = true;
		}
		System.out.println("registeredUserFlag on result:"+registeredUserFlag);
		
		if (othelloMode.equals("normal") || othelloMode.equals("mican") || othelloMode.equals("ai")) {
			if (type.equals("anonymous") && !registeredUserFlag) {
				System.out.println("case1:anonymous "+registeredUserFlag);			
				return "result";
				
			} else if (type.equals("registered") && registeredUserFlag) {
				System.out.println("case2:registered "+registeredUserFlag);
				return "result";
			} else {
				System.out.println("case3");
				return "invalid";
			}	
		} else {
			System.out.println("case4");
			return "invalid";
		}
		
	}
	
	/**
	 * ウェブページ"board"の返却を行う。
	 * ログインユーザーの場合には、リソースサーバーに対して対戦成績を取得するリクエストを送り、
	 * 対戦成績の実績の情報を取得して、ウェブページに埋め込む。
	 * @param othelloMode
	 * @param type
	 * @param model
	 * @return
	 */
	@GetMapping("/othello/board/graphic/{othelloMode}")
	public String getOthelloBoardGraphic(@PathVariable("othelloMode") String othelloMode, @RequestParam(name = "type") String type, Model model) {
		
		model.addAttribute("othelloMode", othelloMode);
		model.addAttribute("type", type);
		
		System.out.println("othelloMode:"+othelloMode);
		System.out.println("type:"+type);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean registeredUserFlag = false;
		if (authentication instanceof AnonymousAuthenticationToken){
			registeredUserFlag = false;
		} else {
			registeredUserFlag = true;
		}

		System.out.println("registeredUserFlag on graphic:"+registeredUserFlag);
		
		if (othelloMode.equals("normal") || othelloMode.equals("mican") || othelloMode.equals("ai")) {
			
			if (type.equals("anonymous") && !registeredUserFlag) {
				System.out.println("case1:anonymous "+registeredUserFlag);
				return "board";
				
			} else if (type.equals("registered") && registeredUserFlag) {
				System.out.println("case2:registered "+registeredUserFlag);
				
				if (othelloMode.equals("ai")) {
					System.out.println("othelloMode:ai");
					
					String userName = authentication.getName();
					System.out.println("userId:"+userName);
					
					Map<String, Object> playResult = webClient.get()
							.uri(playresultGetUrl)
							.retrieve()
							.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
							.block();
					
					Integer winNum = (Integer)playResult.get("winNum");
					Integer drawNum = (Integer)playResult.get("drawNum");
					Integer loseNum = (Integer)playResult.get("loseNum");
					
					model.addAttribute("winNum", Integer.toString(winNum));
					model.addAttribute("drawNum", Integer.toString(drawNum));
					model.addAttribute("loseNum", Integer.toString(loseNum));
					
					return "board";					
				} else {
					System.out.println("othelloMode:else");
					return "board";
				}
				
			} else {
				System.out.println("case3");
				return "invalid";
			}
			
		} else {
			System.out.println("case4");
			return "invalid";
		}
		
	}	

	/**
	 * ログインせずに、AIオセロ機能を利用する場合の、AIオセロサーバーへのアクセスの仲介を行う。
	 * このメソッドでは、AIオセロサーバーにリクエストを送っている。
	 * 
	 * OthelloRestControllerクラスに移行すべき。
	 * @param aiOthelloRequestResource
	 * @return
	 */
	@PostMapping("/ai-othello/v1/best-move")
	@ResponseBody
	public AiOthelloResponseResource getBestMove(@RequestBody AiOthelloRequestResource aiOthelloRequestResource) {

		AiOthelloResponseResource aiOthelloResponseResource = aiOthelloService.getAiOthelloResponse(aiOthelloRequestResource); 
		
		return aiOthelloResponseResource; 
	}
	
	/**
	 * ログインして、AIオセロ機能を利用する場合の、AIオセロサーバーへのアクセスの仲介を行う。
	 * この場合、AIオセロサーバーの前段にはリソースサーバーを配置した状態で利用するので、
	 * このメソッドでは、リソースサーバーにリクエストを送っている。
	 * 
	 * @param aiOthelloRequestResource
	 * @return
	 */
	@PostMapping("/ai-othello/v2/best-move")
	@ResponseBody
	public LoginUserAiOthelloResponseResource getBestMoveIdAndBestMove(@RequestBody AiOthelloRequestResource aiOthelloRequestResource) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean registeredUserFlag = false;
		if (authentication instanceof AnonymousAuthenticationToken){
			registeredUserFlag = false;
		} else {
			registeredUserFlag = true;
		}

		System.out.println("registeredUserFlag on getBestMove:"+registeredUserFlag);
		
		LoginUserAiOthelloResponseResource loginUserAiOthelloResponseResource = new LoginUserAiOthelloResponseResource();
		if(registeredUserFlag) {
			loginUserAiOthelloResponseResource = webClient.post()
					.uri(bestmoveGetUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.body(Mono.just(aiOthelloRequestResource),AiOthelloRequestResource.class)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<LoginUserAiOthelloResponseResource>() {})
					.block();
		} else {
			AiOthelloResponseResource aiOthelloResponseResource = aiOthelloService.getAiOthelloResponse(aiOthelloRequestResource);
			loginUserAiOthelloResponseResource.setBestmoveFromAiOthelloResponseResource(aiOthelloResponseResource);
			loginUserAiOthelloResponseResource.setBestmoveid("");
		}

		return loginUserAiOthelloResponseResource; 
	}
}
