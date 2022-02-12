package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import lombok.Getter;
import lombok.Setter;

/**
 * 対戦成績の更新の際には、一連の盤面の推移の情報が必要となるが、
 * 1時点の盤面推移のデータを格納するクラス
 * 
 * 石を置く前の盤面、その時のplayer、置かれた手（もしくはリソースサーバーで生成した乱数）、石を置いた後の盤面
 * @author shunyu
 *
 */
@Getter
@Setter
public class GameSituation {
	
	private int[][] beforeboard;
	private int player;
	private String action;
	private int[][] afterboard;

}