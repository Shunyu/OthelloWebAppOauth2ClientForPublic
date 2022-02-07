/**
 * 
 */

var gameSituationList = [];
// boardArray:int[][]
// player:int
// action(selectedItem):String

document.addEventListener('DOMContentLoaded', function(){

	var othellogridDom = document.querySelector('.othellogrid');
	for (var i=0; i<64; i++){
	
		var divCellDom = document.createElement("div");
		divCellDom.className = "cell";
		
		var divOthellodiscDom = document.createElement("div");
		divOthellodiscDom.className = "othellodisc";
		
		divCellDom.appendChild(document.createTextNode("\n"));
		divCellDom.appendChild(divOthellodiscDom);
		divCellDom.appendChild(document.createTextNode("\n"));
		
		othellogridDom.appendChild(document.createTextNode("\n"));
		othellogridDom.appendChild(divCellDom);
		othellogridDom.appendChild(document.createTextNode("\n"));
	}
		
	getNewOthelloBoard()
	.then(
		response => {
			console.log('初期画面のロードにてオセロ盤の初期化が完了しました。');
			console.log("response: "+response);
			var player = response[0];
			var gameOverFlag = response[1];
			
			//gameSituationListの更新ここから
			gameSituationList = [];
			var boardArray = response[5];
			if (!gameOverFlag){
				var gameSituation = {
						beforeboard: JSON.parse(JSON.stringify(boardArray)),
						player: JSON.parse(JSON.stringify(player))
				};
				gameSituationList = [gameSituation];
				console.log("gameSituation.beforeboard:"+gameSituation.beforeboard);
				console.log("gameSituation.player:"+gameSituation.player);
			}
			//gameSituationListの更新ここまで

			//初期化直後なので、不要なはず
			if (gameOverFlag){
				restartGame();
			}
			
			if (othelloMode=="ai" && type=="registered"){
				//ログインユーザーのユーザー名をもとに、対戦成績を取得して、対戦成績を表示する。
				//console.log('初期画面のロードにてupdatePlayResultViewを実行します。');
				//updatePlayResultView();
			}
			
			if (othelloMode=="ai" && player==1){
				updateGameByAi();			
			}
					
		},
		error => {
			//console.log('エラー：${error}');
		}
	);	

	var cells = document.querySelectorAll('.cell');
	for (var i = 0; i < cells.length; i++) {
		
		var targetRow = Math.floor(i / 8) + 1;
		var targetCol = (i % 8) + 1;
		
		cells[i].addEventListener('click', {
			row: targetRow,
			col: targetCol,
			handleEvent: selectCell
		});
	}

   var hintButtonDom = document.getElementById("hintButton");
   hintButtonDom.addEventListener('click', {
       handleEvent: getHint
   });
   
   var restartGameButtonDom = document.getElementById("restartGameButton");
   restartGameButtonDom.addEventListener('click', {
       handleEvent: restartGame
   });
   
}, false);
  

function selectCell(e) {

	var row = this.row;
	var col = this.col;

	var selectedItem = row.toString() + col.toString();
  	console.log("selectCell:"+selectedItem + "がクリックされました。");

	updateOthelloBoard(selectedItem)
	.then(
		response => {
			//console.log(response);
			var player = response[0];
			var gameOverFlag = response[1];
			
			//gameSituationListの更新ここから
			var gameUpdated = response[6];
			if (gameUpdated){
				console.log("selectCell:盤面がアップデートされました。");
				var boardArray = response[5];
				var gameSituationListLength = gameSituationList.length;
				var gameSituation = gameSituationList[gameSituationListLength-1];
				gameSituation.action = (' ' + selectedItem).slice(1);
				gameSituation.afterboard = JSON.parse(JSON.stringify(boardArray));
				console.log("gameSituation.beforeboard:"+gameSituation.beforeboard);
				console.log("gameSituation.player:"+gameSituation.player);
				console.log("gameSituation.action:"+gameSituation.action);
				console.log("gameSituation.afterboard:"+gameSituation.afterboard);
				if (!gameOverFlag){
					console.log("selectCell:gameOverではありません。");
					var nextGameSituation = {
							beforeboard: JSON.parse(JSON.stringify(boardArray)),
							player: JSON.parse(JSON.stringify(player))
					};
					gameSituationList.push(nextGameSituation);
					console.log("nextGameSituation.beforeboard:"+nextGameSituation.beforeboard);
					console.log("nextGameSituation.player:"+nextGameSituation.player);
				}
			}
			//gameSituationListの更新ここまで
			
			if (gameOverFlag){
				if (othelloMode=="ai" && type=="registered"){
					//ログインユーザーのユーザー名をもとに、勝ち負け分けを踏まえて、対戦成績を更新する。
					//やはり、対戦成績の更新は必要。直接リソースサーバーではなく、クライアントのウェブアプリ経由で実行する。
					var lead = response[2];
					var winner = lead;
					var blackNum = response[3];
					var whiteNum =response[4];
					
					//対戦成績の更新
					console.log('selectCellにてupdatePlayResultBeforeLocationReplaceを実行します。');
					console.log('selectCellにてgameSituationList:'+gameSituationList);
					updatePlayResultBeforeLocationReplace(winner, blackNum, whiteNum);
//					console.log('selectCellにてupdatePlayResultを実行します。');
//					updatePlayResult(winner);
//					console.log('selectCellにてupdatePlayResultBeforeRestartGameを実行します。');
//					updatePlayResultBeforeRestartGame(winner);
					
//					var blackNum = response[3];
//					var whiteNum =response[4];
//					var url = '/othello/result/'+othelloMode+'?type='+type+'&blackNum='+blackNum+'&whiteNum='+whiteNum;
//					location.replace(url);
				} else {
					//console.log('selectCellにてrestartGameを実行します。');
					//restartGame();
					
					var blackNum = response[3];
					var whiteNum =response[4];
					var url = '/othello/result/'+othelloMode+'?type='+type+'&blackNum='+blackNum+'&whiteNum='+whiteNum;
					location.replace(url);					
				}
			} else {
				if (othelloMode=="ai" && player==1){
					//console.log('selectCellにてupdateGameByAiを実行します。');
					updateGameByAi();
				}
			}
		},
		error => {
			//console.log('エラー：${error}');
		}
	);
}


function updateGameByAi(){

	getOthelloPlayerAndBoardArray()
	.then(
		response => {
			//console.log("updateGameByAiにてfinish getOthelloPlayerAndBoardArray");
			//console.log("updateGameByAiにてstart getBestMove");				
			//console.log(response);
			var playerAndBoardArray = response;
			return getBestMove(playerAndBoardArray);
		}		
	)				
	.then(
		response => {
			console.log("updateGameByAiにてfinish getBestMove");
			console.log("updateGameByAiにてstart updateOthelloBoard");				
			//console.log(response);
//			var selectedItemByAi = response;
			var selectedItemByAi = response['bestmove'];
			
			//gameSituationListの更新ここから
			var bestmoveid = response['bestmoveid'];
			var gameSituationListLength = gameSituationList.length;
			var gameSituation = gameSituationList[gameSituationListLength-1];
			gameSituation.action = (' ' + bestmoveid).slice(1);
			console.log("gameSituation.beforeboard:"+gameSituation.beforeboard);
			console.log("gameSituation.player:"+gameSituation.player);
			console.log("gameSituation.action:"+gameSituation.action);
			//gameSituationListの更新ここまで
			
			return updateOthelloBoard(selectedItemByAi);
		}		
	)
	.then(
		response => {
			//console.log(response);
			console.log("updateGameByAiにてfinish updateOthelloBoard");
			
			var player = response[0];
			var gameOverFlag = response[1];
			
			//gameSituationListの更新ここから
			var boardArray = response[5];
			var gameSituationListLength = gameSituationList.length;
			var gameSituation = gameSituationList[gameSituationListLength-1];
			gameSituation.afterboard = JSON.parse(JSON.stringify(boardArray));
			console.log("gameSituation.beforeboard:"+gameSituation.beforeboard);
			console.log("gameSituation.player:"+gameSituation.player);
			console.log("gameSituation.action:"+gameSituation.action);
			console.log("gameSituation.afterboard:"+gameSituation.afterboard);
			if (!gameOverFlag){
				console.log("updateGameByAiにてgameOverではありません。");
				var nextGameSituation = {
						beforeboard: JSON.parse(JSON.stringify(boardArray)),
						player: JSON.parse(JSON.stringify(player))
				};
				gameSituationList.push(nextGameSituation);
				console.log("nextGameSituation.beforeboard:"+nextGameSituation.beforeboard);
				console.log("nextGameSituation.player:"+nextGameSituation.player);
			}
			//gameSituationListの更新ここまで
			
			if (gameOverFlag){
				if (othelloMode=="ai" && type=="registered"){
					//ログインユーザーのユーザー名をもとに、勝ち負け分けを踏まえて、対戦成績を更新する。
					//やはり、対戦成績の更新は必要。直接リソースサーバーではなく、クライアントのウェブアプリ経由で実行する。
					var lead = response[2];
					var winner = lead;
					var blackNum = response[3];
					var whiteNum =response[4];
					
					//対戦成績の更新
					console.log('updateGameByAiにてupdatePlayResultBeforeLocationReplaceを実行します。');
					console.log('updateGameByAiにてgameSituationList:'+gameSituationList);
					updatePlayResultBeforeLocationReplace(winner, blackNum, whiteNum);
					//console.log('updateGameByAiにてupdatePlayResultを実行します。');
					//updatePlayResult(winner);
					//console.log('updateGameByAiにてupdatePlayResultBeforeRestartGameを実行します。');
					//updatePlayResultBeforeRestartGame(winner);
					
					//var blackNum = response[3];
					//var whiteNum =response[4];
					//var url = '/othello/result/'+othelloMode+'?type='+type+'&blackNum='+blackNum+'&whiteNum='+whiteNum;
					//location.replace(url)
												
				} else {
					//console.log('updateGameByAiにてrestartGameを実行します。');
					//restartGame();
					
					var blackNum = response[3];
					var whiteNum =response[4];
					var url = '/othello/result/'+othelloMode+'?type='+type+'&blackNum='+blackNum+'&whiteNum='+whiteNum;
					location.replace(url);
				}
			} else {
				if (player==1){
					//console.log("updateGameByAiにて、再度updateGameByAiを実行する。");
					updateGameByAi();
				}
			}
		},
		error => {
			//console.log('エラー：${error}');
		}
	);
}

function getOthelloPlayerAndBoardArray(){

	return new Promise((resolve, reject) => { 
  
		var xmlHttpRequest = new XMLHttpRequest();
    
    	xmlHttpRequest.open('GET', '/othello/v1/game-status', true);
    	xmlHttpRequest.send();
    
    	xmlHttpRequest.onreadystatechange = function(){
      		if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
        		var boardStatus = JSON.parse(xmlHttpRequest.responseText);
        		var player = boardStatus.player;
        		var boardArray = boardStatus.boardArray;
        		var playerAndBoardArray = [player, boardArray]
     		    resolve(playerAndBoardArray);

		    } else if (xmlHttpRequest.status != 200){

				reject("getOthelloBoardArrayエラー");

			} 
		}
	});
}


function getBestMove(playerAndBoardArray){

	return new Promise((resolve, reject) => {
		var xmlHttpRequest = new XMLHttpRequest();
		
		/*POSTのBODYをただしく送る必要*/
		/*xmlHttpRequest.open('POST', 'http://localhost:5000/ai-othello/v1/best-move', true);*/
//		xmlHttpRequest.open('POST', '/ai-othello/v1/best-move', true);
		xmlHttpRequest.open('POST', '/ai-othello/v2/best-move', true);
		xmlHttpRequest.setRequestHeader("Content-Type", "application/json");
		var playerAndBoardArrayDic = {
		    "nextplayer": playerAndBoardArray[0],
		    "boardarray": JSON.stringify(playerAndBoardArray[1])
		}
		var playerAndBoardArrayJson = JSON.stringify(playerAndBoardArrayDic);
		//console.log(playerAndBoardArrayJson);
		xmlHttpRequest.send(playerAndBoardArrayJson);
		
		xmlHttpRequest.onreadystatechange = function(){
			if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
			
				//console.log(xmlHttpRequest.responseText);
				var bestMoveDic = JSON.parse(xmlHttpRequest.responseText);
//				console.log(bestMoveDic);
				
//				var bestMoveId = bestMoveDic['bsetmoveid'];
//				var bestMove = bestMoveDic['bestmove'];
				
				/*結果を正しく次の関数に与える必要*/
//				resolve(bestMove);
				resolve(bestMoveDic);
				
			} else if (xmlHttpRequest.status != 200) {
			
				reject("getBestMoveエラー");
			
			}
		}
	});
}

function getHint(){
	
	var xmlHttpRequest = new XMLHttpRequest();
	
	xmlHttpRequest.open('GET', '/othello/v1/game-status/hint', true);
	xmlHttpRequest.send();
	
	xmlHttpRequest.onreadystatechange = function(){
		if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
			var hintArray = JSON.parse(xmlHttpRequest.responseText);
			
			setOthelloHintArray(hintArray);
			
		}
	}
}

function restartGame(){

	getNewOthelloBoard()
	.then(
		response => {
			console.log('restartGameにてオセロ盤の初期化が完了しました。');
			//console.log(response);
			var player = response[0];
			var gameOverFlag = response[1];
			
			//gameSituationListの更新ここから
			gameSituationList = [];
			var boardArray = response[5];
			if (!gameOverFlag){
				var gameSituation = {
						beforeboard: JSON.parse(JSON.stringify(boardArray)),
						player: JSON.parse(JSON.stringify(player))
				};
				gameSituationList = [gameSituation];
				console.log("gameSituation.beforeboard:"+gameSituation.beforeboard);
				console.log("gameSituation.player:"+gameSituation.player);
			}
			//gameSituationListの更新ここまで
			
			//実行されないはず
			if (gameOverFlag){
				restartGame();
			}
			
			if (othelloMode=="ai" && type=="registered"){
				//ログインユーザーのユーザー名をもとに、対戦成績を取得して、対戦成績を表示する。
				//console.log('restartGameにてupdatePlayResultViewを実行します。');
				//updatePlayResultView();
			}
						
			if (othelloMode=="ai" && player==1){
				//console.log('restartGameにてupdateGameByAiを実行します。');
				updateGameByAi();			
			}		
		},
		error => {
			//console.log('エラー：${error}');
		}
	);	

}

//ログインユーザーのユーザー名をもとに、勝ち負け分けを踏まえて、対戦成績を更新する。
function updatePlayResultBeforeLocationReplace(winner, blackNum, whiteNum){
	console.log('updatePlayResult');
	
	postPlayResult(winner)
	.then(
		response => {
			console.log(response);
			
			var url = '/othello/result/'+othelloMode+'?type='+type+'&blackNum='+blackNum+'&whiteNum='+whiteNum;
			location.replace(url)
		},
		error => {
			console.log('エラー：'+error);
		}
	);	
}

//ログインユーザーのユーザー名をもとに、勝ち負け分けを踏まえて、対戦成績を更新するためにPOSTを行う。
function postPlayResult(winner){
	return new Promise((resolve, reject) => {
		var xmlHttpRequest = new XMLHttpRequest();
		//xmlHttpRequest.open('POST', '/playresult/update', true);
		xmlHttpRequest.open('POST', '/ai-othello/v2/playresult/update', true);
		xmlHttpRequest.setRequestHeader("Content-Type", "application/json");
//		var gameResultDic = {
//		    "gameresult": winner
//		}
		var gameResultDic = {
			"gameresult": winner,
			"gameSituationList": gameSituationList
		}
		var gameResultJson = JSON.stringify(gameResultDic);
		console.log(gameResultJson);
		xmlHttpRequest.send(gameResultJson);		
		xmlHttpRequest.onreadystatechange = function(){
			if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
				console.log('成功');
				resolve("postPlayResultにて対戦成績を更新しました。");
			} else if (xmlHttpRequest.status != 200) {
				console.log('失敗'+xmlHttpRequest.status);
				reject("対戦成績の更新に失敗しました。");
			}
		}
	});
}


function getNewOthelloBoard(){
	
	return new Promise((resolve, reject) => {
		var xmlHttpRequest = new XMLHttpRequest();
		
		xmlHttpRequest.open('GET', '/othello/v1/new-game', true);
		xmlHttpRequest.send();
		
		xmlHttpRequest.onreadystatechange = function(){
			if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
				var boardStatus = JSON.parse(xmlHttpRequest.responseText);
				console.log("boardStatus: "+boardStatus);
				
				var boardArray = boardStatus.boardArray;
				var player = boardStatus.player;
				var blackStoneNum = boardStatus.blackStoneNum;
				var whiteStoneNum = boardStatus.whiteStoneNum;
				var gameOverFlag = boardStatus.gameOverFlag;
				var lead = getLead(blackStoneNum, whiteStoneNum);
								
				setOthelloBoardArray(boardArray);				
				setOthelloStatus(player, blackStoneNum, whiteStoneNum);
				
/*				if (othelloMode=="ai" && type=="registered"){
					//ログインユーザーのユーザー名をもとに、対戦成績を取得して、対戦成績を表示する。
				}*/
				
				//console.log(gameOverFlag.toString());
				//checkGameOverFlag(gameOverFlag);
				
				var gameStatus = [player, gameOverFlag, lead, blackStoneNum, whiteStoneNum, boardArray]
				
     		    resolve(gameStatus); 

		    } else if (xmlHttpRequest.status != 200){

				reject("getNewOthelloBoardエラー");

			} 
		}		
	});
}

function getOthelloBoard(){

	return new Promise((resolve, reject) => { 
  
		var xmlHttpRequest = new XMLHttpRequest();
    
    	xmlHttpRequest.open('GET', '/othello/v1/game-status', true);
    	xmlHttpRequest.send();
    
    	xmlHttpRequest.onreadystatechange = function(){
      		if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
     		    
				var boardStatus = JSON.parse(xmlHttpRequest.responseText);
				//console.log(boardStatus);
				
				var boardArray = boardStatus.boardArray;
				var player = boardStatus.player;
				var blackStoneNum = boardStatus.blackStoneNum;
				var whiteStoneNum = boardStatus.whiteStoneNum;
				var gameOverFlag = boardStatus.gameOverFlag;
				var lead = getLead(blackStoneNum, whiteStoneNum);
				
				setOthelloBoardArray(boardArray);				
				setOthelloStatus(player, blackStoneNum, whiteStoneNum);

				
/*				if (gameOverFlag && othelloMode=="ai" && type=="registered"){
					//ログインユーザーのユーザー名をもとに、勝ち負け分けを踏まえて、対戦成績を更新する。			
				}*/
				
				//console.log(gameOverFlag.toString());
				//checkGameOverFlag(gameOverFlag);
				
				var gameStatus = [player, gameOverFlag, lead, blackStoneNum, whiteStoneNum]
				
     		    resolve(gameStatus);    		    

		    } else if (xmlHttpRequest.status != 200){

				reject("getOthelloBoardエラー");

			} 
		}
	});
}

function updateOthelloBoard(selectedItem){
  
	return new Promise((resolve, reject) => {
		
		var xmlHttpRequest = new XMLHttpRequest();

		xmlHttpRequest.open('GET', '/othello/v1/game-status/update/'+selectedItem, true);
    	xmlHttpRequest.send();
  
		xmlHttpRequest.onreadystatechange = function(){
			if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200){
    
				var boardStatus = JSON.parse(xmlHttpRequest.responseText);
				//console.log(boardStatus);
				
				var boardArray = boardStatus.boardArray;
				var player = boardStatus.player;
				var blackStoneNum = boardStatus.blackStoneNum;
				var whiteStoneNum = boardStatus.whiteStoneNum;
				var gameOverFlag = boardStatus.gameOverFlag;
				var gameUpdated = boardStatus.gameUpdated;
				var lead = getLead(blackStoneNum, whiteStoneNum);
				
				setOthelloBoardArray(boardArray);				
				setOthelloStatus(player, blackStoneNum, whiteStoneNum);

				
/*				if (gameOverFlag && othelloMode=="ai" && type=="registered"){
					//ログインユーザーのユーザー名をもとに、勝負分を踏まえて、対戦成績を更新する。					
				}*/
				
				//console.log(gameOverFlag.toString());
				//checkGameOverFlag(gameOverFlag);
				
				var gameStatus = [player, gameOverFlag, lead, blackStoneNum, whiteStoneNum, boardArray, gameUpdated]
				
     		    resolve(gameStatus);  

      		} else if (xmlHttpRequest.status != 200){

				reject("updateOthelloBoardエラー");

      		} 
    	}
	});
}

function getLead(blackStoneNum, whiteStoneNum){

	var lead = "draw";
	if (blackStoneNum > whiteStoneNum) {
		lead = "black";
	} else if (blackStoneNum < whiteStoneNum) {
		lead = "white";
	}
	
	return lead;
}

function setOthelloHintArray(hintArray){
			
	for (var row = 1; row < 9; row++){
		for (var col = 1; col < 9; col++){

			var targetCells = document.querySelectorAll('.cell');
			var targetItem = (row - 1)*8 + (col - 1);
			var targetDom = targetCells[targetItem].childNodes.item(1);

			if (othelloMode=="normal" || othelloMode=="ai"){			
				if (hintArray[row][col] == 1){
					targetDom.style.backgroundColor = "#292b2c";
					targetDom.style.opacity = "0.3";
				} else if (hintArray[row][col] == 2){
					targetDom.style.backgroundColor = "#f7f7f7";
					targetDom.style.opacity = "0.3";
				}
			} else if (othelloMode=="mican"){
				
				if (hintArray[row][col] == 1){

					targetDom.style.backgroundImage="url('../../../images/mican_b.gif')";
					targetDom.style.backgroundSize="cover";
					targetDom.style.padding="0";									
					targetDom.style.opacity = "0.3";
								
				} else if (hintArray[row][col] == 2){

					targetDom.style.backgroundImage="url('../../../images/mican_w.gif')";
					targetDom.style.backgroundSize="cover";
					targetDom.style.padding="0";									
					targetDom.style.opacity = "0.3";

				}			
			}
		}
	}
	
}


function setOthelloBoardArray(boardArray){
	
	for (var row = 1; row < 9; row++){
		for (var col = 1; col < 9; col++){

			var targetCells = document.querySelectorAll('.cell');
			var targetItem = (row - 1)*8 + (col - 1);
			var targetDom = targetCells[targetItem].childNodes.item(1);
			
			if (othelloMode=="normal" || othelloMode=="ai"){
	    		if (boardArray[row][col]==0) {
	    			targetDom.style.backgroundColor="#5bc0de";
					targetDom.style.opacity = "1.0";
				} else if (boardArray[row][col]==1) {
					targetDom.style.backgroundColor="#292b2c";
					targetDom.style.opacity = "1.0";
				} else if (boardArray[row][col]==2) {
					targetDom.style.backgroundColor="#f7f7f7";
					targetDom.style.opacity = "1.0";
	    		}
	    	} else if (othelloMode=="mican"){
				
	    		if (boardArray[row][col]==0) {
	    			targetDom.style.backgroundImage="";
	    			targetDom.style.backgroundColor="#5bc0de";
					targetDom.style.opacity = "1.0";					
					
				} else if (boardArray[row][col]==1) {

					targetDom.style.backgroundImage="url('../../../images/mican_b.gif')";
					targetDom.style.backgroundSize="cover";
					targetDom.style.padding="0";									
					targetDom.style.opacity = "1.0";
														
				} else if (boardArray[row][col]==2) {
	
					targetDom.style.backgroundImage="url('../../../images/mican_w.gif')";
					targetDom.style.backgroundSize="cover";
					targetDom.style.padding="0";									
					targetDom.style.opacity = "1.0";
					
	    		}	    	    
			}
		}
	}	
	
}

function setOthelloStatus(player, blackStoneNum, whiteStoneNum){

    var blackStoneNumDom = document.getElementById("blackStoneNum");
    blackStoneNumDom.textContent = blackStoneNum;

    var whiteStoneNumDom = document.getElementById("whiteStoneNum");
    whiteStoneNumDom.textContent = whiteStoneNum;
    		
    var blackStoneLabelDom = document.getElementById("blackStoneLabel");
    var whiteStoneLabelDom = document.getElementById("whiteStoneLabel");
  
    if (player == 1) {
        blackStoneLabelDom.className = "text-white bg-dark border border-warning border-10 font-weight-bold lead text-center rounded-circle";
        whiteStoneLabelDom.className = "text-dark bg-white border border-dark border-10 lead text-center rounded-circle";   		    
    } else if (player == 2) {
        blackStoneLabelDom.className = "text-white bg-dark border border-dark border-10 lead text-center rounded-circle";      		    
        whiteStoneLabelDom.className = "text-dark bg-white border border-warning border-10 font-weight-bold lead text-center rounded-circle";   		    
    }
    
}
