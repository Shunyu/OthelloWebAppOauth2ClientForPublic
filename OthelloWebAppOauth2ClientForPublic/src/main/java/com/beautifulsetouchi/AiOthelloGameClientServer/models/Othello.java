package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * オセロの盤面の情報、およびオセロのルールなどを保持するクラス
 * @author shunyu
 *
 */
@Service
@Scope(value="session")
public class Othello {
	
    private static final int BLACK = 1;
    private static final int WHITE = 2;
    private static final int DRAW = 3;

    private int[][] boardArray;

    private int player;
    private int winner_;

    private int blackStoneNum;
    private int whiteStoneNum;

    private boolean gameOverFlag;

    public Player winner;
    public String restartFlag;

    private static Othello sOthelloInstance;

    public static Othello getInstance(){
        if(sOthelloInstance == null){
            synchronized (Othello.class){
                if (sOthelloInstance == null){
                    sOthelloInstance = new Othello();
                }
            }
        }
        // intialization
        sOthelloInstance.winner = null;
        sOthelloInstance.restartFlag = null;

        return sOthelloInstance;
    }


    private Othello() {
        initializeOthello();
    }

    public void initializeOthello(){

        boardArray = new int[10][10];

        initializeBoard();

        this.player = BLACK;
        this.winner_ = DRAW;
        this.blackStoneNum = 2;
        this.whiteStoneNum = 2;
        this.gameOverFlag = false;
    }

    public void initializeBoard(){

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                this.boardArray[i][j] = -1;
            }
        }

        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                this.boardArray[i][j] = 0;
            }
        }

        this.boardArray[4][4] = 1;
        this.boardArray[4][5] = 2;
        this.boardArray[5][4] = 2;
        this.boardArray[5][5] = 1;
    }

    public int getBlackStoneNum(){
        return this.blackStoneNum;
    }

    public int getWhiteStoneNum(){
        return this.whiteStoneNum;
    }

    public int getPlayer(){
        return this.player;
    }

    public void setPlayer(int player){
        this.player = player;
        return;
    }

    public int[][] getBoardArray(){
        return this.boardArray;
    }

    public int getOpponentPlayer(){
        int opponentPlayer = 3 - this.player;
        return opponentPlayer;
    }

    public int countTurnOverStones(int p, int q, int d, int e){
        int i;
        int opponentPlayer = getOpponentPlayer();

        for(i = 1; this.boardArray[p+i*d][q+i*e] == opponentPlayer; i++){}

        if (this.boardArray[p+i*d][q+i*e] == this.player) {
            return i - 1;
        } else {
            return 0;
        }
    }

    
    public boolean isLegalMove(int p, int q) {
        if (p < 1 || p > 8 || q < 1 || q > 8){
            return false;
        }
        if (this.boardArray[p][q] != 0){
            return false;
        }

        for (int d = -1; d <= 1; d++){
            for (int e = -1; e <= 1; e++){
                if (d == 0 && e == 0){
                    continue;
                }
                if (countTurnOverStones(p, q, d, e) > 0){
                    return true;
                }
            }
        }

        return false;    	
    }
    
    public boolean isLegalMove(BoardIndex boardIndex) {
    	
        int p = boardIndex.getVerticalIndex();
        int q = boardIndex.getHorizontalIndex();

        return isLegalMove(p, q); 
    }

    public boolean existLegelMove(){

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                if (isLegalMove(i, j)){
                    return true;
                }
            }
        }
        return false;
    }

    public void turnOverStones(BoardIndex boardIndex){

        int p = boardIndex.getVerticalIndex();
        int q = boardIndex.getHorizontalIndex();

        for (int d = -1; d <= 1; d++){
            for (int e = -1; e <= 1; e++){
                if (d == 0 && e == 0){
                    continue;
                }
                int count = countTurnOverStones(p, q, d, e);

                for (int i = 1; i <= count; i++){
                    int tempVerticalIndex = p + i * d;
                    int tempHorizontalIndex = q + i * e;
                    this.boardArray[tempVerticalIndex][tempHorizontalIndex] = this.player;
                }
            }
        }

        this.boardArray[p][q] = this.player;

    }

    public void countStone(){
        this.blackStoneNum = 0;
        this.whiteStoneNum = 0;
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                switch(this.boardArray[i][j]){
                    case BLACK:
                        this.blackStoneNum++;
                        break;
                    case WHITE:
                        this.whiteStoneNum++;
                        break;
                }

            }
        }
    }

    public void updateGame(BoardIndex boardIndex){
    	
    	if (isLegalMove(boardIndex)) {
	        turnOverStones(boardIndex);
	        countStone();
	        setPlayer(getOpponentPlayer());
    	}

        if(existLegelMove()) {

        } else {
            setPlayer(getOpponentPlayer());
            if (existLegelMove()){

            } else {
                gameOver();
            }
        }
        return;
    }

    public boolean isGameUpdated(BoardIndex boardIndex){
    	
    	
    	boolean gameUpdated = false;
    	
    	if (isLegalMove(boardIndex)) {
	        turnOverStones(boardIndex);
	        countStone();
	        setPlayer(getOpponentPlayer());
	        gameUpdated = true;
    	}

        if(existLegelMove()) {
        } else {
            setPlayer(getOpponentPlayer());
            if (existLegelMove()){
            } else {
                gameOver();
            }
        }

        return gameUpdated;
    }
    
    public BoardIndex getMove(){
        while(true){
            int q = 3;
            int p = 1;
            if(isLegalMove(p, q)){
                BoardIndex boardIndex = new BoardIndex(p, q);
                return boardIndex;
            }
        }
    }

    public void gameOver(){

        countStone();

        if ((this.blackStoneNum - this.whiteStoneNum) > 0) {
            this.winner_ = BLACK;
            Player blackPlayer = new Player("Black", "1");
            winner = blackPlayer;
        }
        else if ((this.blackStoneNum - this.whiteStoneNum) < 0) {
            this.winner_ = WHITE;
            Player whitePlayer = new Player("White", "2");
            winner = whitePlayer;
        }
        else {
            this.winner_ = DRAW;
            Player drawPlayer = new Player("Draw", "3");
            winner = drawPlayer;
        }

        this.gameOverFlag = true;

    }

    public boolean isGameOverFlag() {
        return gameOverFlag;
    }

    public int[][] getHintArray(){

        int[][] hintArray = new int[10][10];

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                hintArray[i][j] = -1;
            }
        }

        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                hintArray[i][j] = 0;
            }
        }

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                if (isLegalMove(i, j)){
                    if (player == BLACK) {
                        hintArray[i][j] = 1;
                    } else {
                        hintArray[i][j] = 2;
                    }
                }
            }
        }

        return hintArray;
    }

    public void restartGame(){

        initializeOthello();

        restartFlag = "Restart";

        return;
    }

}
