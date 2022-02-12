package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 対戦成績の更新の際に、ゲーム結果とオセロ盤面の一連の推移の情報が必要なので、
 * その情報を格納するクラス
 * @author shunyu
 *
 */
@Getter
@Setter
public class GameResultRequestResource {
	
	private String gameresult;
	private List<GameSituation> gameSituationList;

}
