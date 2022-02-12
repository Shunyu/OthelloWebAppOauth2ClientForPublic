package com.beautifulsetouchi.AiOthelloGameClientServer.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.beautifulsetouchi.AiOthelloGameClientServer.models.BoardIndex;
import com.beautifulsetouchi.AiOthelloGameClientServer.models.Othello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * オセロゲームを利用する際にリクエストを送るURIを設定したコントローラークラス
 * JavaScriptからこれらのURIにリクエストが送られながら、オセロゲームが進む。
 * @author shunyu
 *
 */
@RestController
@Scope(value="session")
public class OthelloRestController {
	
	@Autowired
	Othello sOthelloInstance;

	@GetMapping("/othello/v1/game-status")
	public Map<String, Object> getOthelloBoard(){
		
		int[][] boardArray = sOthelloInstance.getBoardArray();		
		int player = sOthelloInstance.getPlayer();
		int blackStoneNum = sOthelloInstance.getBlackStoneNum();
		int whiteStoneNum = sOthelloInstance.getWhiteStoneNum();
		boolean gameOverFlag = sOthelloInstance.isGameOverFlag();
		
		Map<String, Object> boardStatus = new HashMap<>();
		boardStatus.put("player", player);
		boardStatus.put("blackStoneNum", blackStoneNum);
		boardStatus.put("whiteStoneNum", whiteStoneNum);
		boardStatus.put("boardArray", boardArray);
		boardStatus.put("gameOverFlag", gameOverFlag);
		
		return boardStatus;
	}	
	
	@GetMapping("/othello/v1/game-status/update/{position:.+}")
	public Map<String, Object> updateOthelloBoard(@PathVariable("position") String position){
		
		int verticalIndex = Integer.parseInt(position.substring(0, 1));
		int horizontalIndex = Integer.parseInt(position.substring(1, 2));
		BoardIndex boardIndex = new BoardIndex(verticalIndex, horizontalIndex);
		boolean gameUpdated = sOthelloInstance.isGameUpdated(boardIndex);
		int[][] boardArray = sOthelloInstance.getBoardArray();
		
		int player = sOthelloInstance.getPlayer();
		int blackStoneNum = sOthelloInstance.getBlackStoneNum();
		int whiteStoneNum = sOthelloInstance.getWhiteStoneNum();
		boolean gameOverFlag = sOthelloInstance.isGameOverFlag();
		
		Map<String, Object> boardStatus = new HashMap<>();
		boardStatus.put("player", player);
		boardStatus.put("blackStoneNum", blackStoneNum);
		boardStatus.put("whiteStoneNum", whiteStoneNum);
		boardStatus.put("boardArray", boardArray);
		boardStatus.put("gameOverFlag", gameOverFlag);
		boardStatus.put("gameUpdated", gameUpdated);
		
		return boardStatus;
	}	
	
	@GetMapping("/othello/v1/game-status/hint")
	public int[][] getHintBoard(){
		
		int[][] hintArray = sOthelloInstance.getHintArray();
		
		return hintArray;
	}
	
	
	@GetMapping("/othello/v1/game-status/summary")
	public int[] getOthelloStatus() {
		int player = sOthelloInstance.getPlayer();
		int blackStoneNum = sOthelloInstance.getBlackStoneNum();
		int whiteStoneNum = sOthelloInstance.getWhiteStoneNum();
		
		int[] statusArray = new int[3];
		statusArray[0] = player;
		statusArray[1] = blackStoneNum;
		statusArray[2] = whiteStoneNum;
		
		return statusArray;
	}

	@GetMapping("/othello/v1/game-status/gameover-flag")
	public boolean getGameOverFlag() {
		
		boolean gameOverFlag = sOthelloInstance.isGameOverFlag();
		
		return gameOverFlag;
	}	

	@GetMapping("/othello/v1/new-game")
	public Map<String, Object> getNewOthelloGame() {
		
		sOthelloInstance.initializeOthello();
		int[][] boardArray = sOthelloInstance.getBoardArray();

		int player = sOthelloInstance.getPlayer();
		int blackStoneNum = sOthelloInstance.getBlackStoneNum();
		int whiteStoneNum = sOthelloInstance.getWhiteStoneNum();
		boolean gameOverFlag = sOthelloInstance.isGameOverFlag();
		
		Map<String, Object> boardStatus = new HashMap<>();
		boardStatus.put("player", player);
		boardStatus.put("blackStoneNum", blackStoneNum);
		boardStatus.put("whiteStoneNum", whiteStoneNum);
		boardStatus.put("boardArray", boardArray);
		boardStatus.put("gameOverFlag", gameOverFlag);
		
		return boardStatus;
	}
	
}
