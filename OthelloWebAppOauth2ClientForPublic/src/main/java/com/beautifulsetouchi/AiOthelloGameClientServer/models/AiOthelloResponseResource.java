package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import lombok.Getter; 
import lombok.Setter;

/**
 * AIオセロサーバーからの最適手のレスポンスを格納するクラス
 * 未ログイン時に利用する。
 * @author shunyu
 *
 */
@Getter
@Setter
public class AiOthelloResponseResource {

	private String bestmove;
 
}
