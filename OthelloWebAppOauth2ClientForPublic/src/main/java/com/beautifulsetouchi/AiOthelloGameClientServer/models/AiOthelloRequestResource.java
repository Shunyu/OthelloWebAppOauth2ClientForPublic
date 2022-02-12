package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import lombok.Getter; 
import lombok.Setter;

/**
 * 別サーバーであるAIオセロサーバー宛に
 * 最適手のリクエストを行う際の、リクエストボディのクラス
 * 
 * OAuthクライアントは最適手に関するリクエストを仲介し、
 * AIオセロサーバー（未ログイン時）やその前段のリソースサーバー（ログイン時）に、改めて最適手のリクエストを送る。
 * @author shunyu
 *
 */
@Getter
@Setter
public class AiOthelloRequestResource {

	private int nextplayer;
	private String boardarray;

}
