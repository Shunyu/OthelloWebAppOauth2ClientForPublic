package com.beautifulsetouchi.AiOthelloGameClientServer.models;

/**
 * オセロのプレーヤーの情報を格納するクラス
 * @author shunyu
 *
 */
public class Player {
    public String name;
    public String value;

    public Player(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
