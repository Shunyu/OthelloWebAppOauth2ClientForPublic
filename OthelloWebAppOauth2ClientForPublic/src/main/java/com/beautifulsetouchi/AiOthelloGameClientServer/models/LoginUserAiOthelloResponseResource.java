package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import lombok.Getter; 
import lombok.Setter;

/**
 * リソースサーバーからの最適手のレスポンスを格納するクラス
 * ログイン時に利用する。
 * @author shunyu
 *
 */
@Getter
@Setter
public class LoginUserAiOthelloResponseResource {

	private String bestmove;
	private String bestmoveid;
	
	public void setBestmoveFromAiOthelloResponseResource(AiOthelloResponseResource aiOthelloResponseResource) {
		this.bestmove = aiOthelloResponseResource.getBestmove();
	}
 
}
