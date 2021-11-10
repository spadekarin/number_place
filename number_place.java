import java.util.Scanner;
import java.io.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.collections.*;
import java.util.*;

public class number_place extends Application{
	private static int[][] board=new int[9][9];//ファイルから読み込んだ9×9個の1桁の非負整数値を格納
	private TextField[][] tf=new TextField[9][9];//テキストフィールド
	private Button[][] bt=new Button[2][2];//ボタン
	private Label lb;//メッセージ用
	private ArrayList<ComboBox<String>> cb_list=new ArrayList<ComboBox<String>>();//コンボボックス用
	
	
	public static void main(String[] args){
		
		String fname="input.txt";//デフォルトのファイルとしてinput.txtを読み込む
		if(args.length>0)fname=args[0];//コマンドライン引数でファイルの指定がある場合そちらにする
		
		
		try{
			/*
				9×9個の整数値が並んだ数独の問題ファイルを読み込み、
				読み込んだ数値が1から9の場合は、そのままboardに格納。→あらかじめ数字が埋められたマス
				その他の場合は0を格納。→空欄という意味
			*/
			Scanner sc=new Scanner(new File(fname));
			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					board[i][j]=sc.nextInt();
					if(board[i][j]<0||9<board[i][j])board[i][j]=0;
				}
			}
			
			launch(args);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void start(Stage stage)throws Exception{
		
		String[] candidate={"1","2","3","4","5","6","7","8","9"};//リストの候補
		lb=new Label();
		lb.setText("各コンボボックスで数字を選択してください");//初期メッセージ
		
		
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				
				ComboBox<String> cb=new ComboBox<String>();
				ObservableList<String> ol=FXCollections.observableArrayList();
				
				/*
					boardが1から9の場合(埋まっている際)は編集のできないテキストフィールド、
					0(空欄)の場合はコンボボックスとして9×9の盤面を表示
				*/
				
				if(board[i][j]==0){//コンボボックス作成
					/*
						各コンボボックスで、解になり得ない数字を除外したリストとする。
						そのために、各コンボボックスから見て横1列、縦1列、3×3ボックス内で埋まっている数字を記録。
						(→1～9の番号のついた空の箱a[]に入れる)
						最後に箱に入っていない数字がリストの候補。
							for(int k=1;k<10;k++){
								if(a[k]==0){
									ol.add(candidate[k-1]);
								}
							}
						
					*/
					int[] a=new int[10];//空の箱10個を用意
					for(int k=0;k<10;k++){
						a[k]=0;
					}
					
					for(int k=0;k<9;k++){//横
						if(board[i][k]!=0){
							a[board[i][k]]=1;
						}
					}
					for(int k=0;k<9;k++){//縦
						if(board[k][j]!=0){
							a[board[k][j]]=1;
						}
					}
					
					
					//3×3ボックス内
					/*
						3×3ボックスイメージ
							xj→→
						iy	0	1	2
						↓	1
						↓	2
					*/
					
					int y=i/3;//どの3×3ボックスに居るか？
					int x=j/3;
					
					//3×3ボックス内の9マス確認
					if(board[0+3*y][0+3*x]!=0){
						a[board[0+3*y][0+3*x]]=1;
					}
					if(board[0+3*y][1+3*x]!=0){
						a[board[0+3*y][1+3*x]]=1;
					}
					if(board[0+3*y][2+3*x]!=0){
						a[board[0+3*y][2+3*x]]=1;
					}
					if(board[1+3*y][0+3*x]!=0){
						a[board[1+3*y][0+3*x]]=1;
					}
					if(board[1+3*y][1+3*x]!=0){
						a[board[1+3*y][1+3*x]]=1;
					}
					if(board[1+3*y][2+3*x]!=0){
						a[board[1+3*y][2+3*x]]=1;
					}
					if(board[2+3*y][0+3*x]!=0){
						a[board[2+3*y][0+3*x]]=1;
					}
					if(board[2+3*y][1+3*x]!=0){
						a[board[2+3*y][1+3*x]]=1;
					}
					if(board[2+3*y][2+3*x]!=0){
						a[board[2+3*y][2+3*x]]=1;
					}
					
					//リスト決定
					for(int k=1;k<10;k++){
						if(a[k]==0){
							ol.add(candidate[k-1]);
						}
					}
					
					
					//コンボボックスにリスト、イベントハンドラを結び付ける
					cb.setItems(ol);
					cb.setOnAction(new EventHandler2());
					cb_list.add(cb);
					
				}else{//テキストフィールド作成
					cb.setItems(null);//コンボボックスはnull。ArrayListで管理/cb_listの要素数を81にしたいため。
					cb_list.add(cb);
					
					tf[i][j]=new TextField(String.valueOf(board[i][j]));//埋まっている数字を書く
					tf[i][j].setEditable(false);//編集不可に設定
					tf[i][j].setBackground(new Background(new BackgroundFill(Color.GRAY,null,null)));//背景灰色
					tf[i][j].setMaxWidth(55);//幅
					tf[i][j].setFont(Font.font("MonoSpace",15));//フォント
				}
				
			}
		}
		
		
		
		//グリッドペインの作成
		GridPane[] sub_gp1=new GridPane[9];//sub_gp1[]…3×3ボックス
		
		for(int i=0;i<9;i++){
			sub_gp1[i]=new GridPane();
			sub_gp1[i].setHgap(5);//グリッドの水平方向の間隔
			sub_gp1[i].setVgap(5);//グリッドの垂直方向の間隔
			
		}
		
		
		//ペインへの追加
		/*
			配置イメージ
				j→→
			 i	sub_gp1[0]	sub_gp1[1]	sub_gp1[2]
			↓	sub_gp1[3]	sub_gp1[4]	sub_gp1[5]
			↓	sub_gp1[6]	sub_gp1[7]	sub_gp1[8]
		*/
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(board[i][j]==0){//コンボボックスの配置
					if(i<3){
						if(j<3){
							sub_gp1[0].add(cb_list.get(i*9+j),j%3,i%3);//i*9+jはi行j列に対応
						}else if(j<6){
							sub_gp1[1].add(cb_list.get(i*9+j),j%3,i%3);
						}else{
							sub_gp1[2].add(cb_list.get(i*9+j),j%3,i%3);
						}
					}else if(i<6){
						if(j<3){
							sub_gp1[3].add(cb_list.get(i*9+j),j%3,i%3);
						}else if(j<6){
							sub_gp1[4].add(cb_list.get(i*9+j),j%3,i%3);
						}else{
							sub_gp1[5].add(cb_list.get(i*9+j),j%3,i%3);
						}
					}else{
						if(j<3){
							sub_gp1[6].add(cb_list.get(i*9+j),j%3,i%3);
						}else if(j<6){
							sub_gp1[7].add(cb_list.get(i*9+j),j%3,i%3);
						}else{
							sub_gp1[8].add(cb_list.get(i*9+j),j%3,i%3);
						}
					}
				}else{//テキストフィールドの配置
					if(i<3){
						if(j<3){
							sub_gp1[0].add(tf[i][j],j%3,i%3);
						}else if(j<6){
							sub_gp1[1].add(tf[i][j],j%3,i%3);
						}else{
							sub_gp1[2].add(tf[i][j],j%3,i%3);
						}
					}else if(i<6){
						if(j<3){
							sub_gp1[3].add(tf[i][j],j%3,i%3);
						}else if(j<6){
							sub_gp1[4].add(tf[i][j],j%3,i%3);
						}else{
							sub_gp1[5].add(tf[i][j],j%3,i%3);
						}
					}else{
						if(j<3){
							sub_gp1[6].add(tf[i][j],j%3,i%3);
						}else if(j<6){
							sub_gp1[7].add(tf[i][j],j%3,i%3);
						}else{
							sub_gp1[8].add(tf[i][j],j%3,i%3);
						}
					}
				}
			}
		}
		
		for(int i=0;i<9;i++){
			sub_gp1[i].setAlignment(Pos.CENTER);
		}
		
		
		
		//リセットボタンの作成
		for(int i=0;i<1;i++){
			for(int j=0;j<1;j++){
				bt[i][j]=new Button(String.valueOf("リセット"));
			}
		}
		//リセットボタンの配置
		GridPane sub_gp2=new GridPane();
		sub_gp2.setHgap(15);//グリッドの水平方向の間隔
		sub_gp2.setVgap(15);//グリッドの垂直方向の間隔
		for(int i=0;i<1;i++){
			for(int j=0;j<1;j++){
				sub_gp2.add(bt[i][j],j,i);
			}
		}
		sub_gp2.setAlignment(Pos.CENTER);
		
		//リセットボタンのイベントハンドラの登録
		bt[0][0].setOnAction(new EventHandler1());
		
		
		
		
		//3×3ボックスのグリッドペインを入れるグリッドペイン
		GridPane sub_gp3=new GridPane();//sub_gp3…盤面全体(3×3ボックスを9つ配置)
		sub_gp3.setHgap(15);//グリッドの水平方向の間隔→3×3ボックスの中身より広めに取り、数独として見やすくする
		sub_gp3.setVgap(15);//グリッドの垂直方向の間隔→同上
		sub_gp3.add(sub_gp1[0],0,0);//配置
		sub_gp3.add(sub_gp1[1],1,0);
		sub_gp3.add(sub_gp1[2],2,0);
		sub_gp3.add(sub_gp1[3],0,1);
		sub_gp3.add(sub_gp1[4],1,1);
		sub_gp3.add(sub_gp1[5],2,1);
		sub_gp3.add(sub_gp1[6],0,2);
		sub_gp3.add(sub_gp1[7],1,2);
		sub_gp3.add(sub_gp1[8],2,2);
		sub_gp3.setAlignment(Pos.CENTER);
		
		
		
		
		//ボーダーペインの作成
		BorderPane bp=new BorderPane();
		//ボーダーペインへの追加
		//盤面、メッセージ、リセットボタンを上から順に配置
		bp.setTop(sub_gp3);
		bp.setCenter(lb);
		bp.setBottom(sub_gp2);
		
		
		
		Scene sc=new Scene(bp,200,250);//シーンの作成
		stage.setScene(sc);//ステージへの追加
		stage.setTitle("数独");//タイトル
		stage.show();//ステージの表示
	}
	
	
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//イベントハンドラクラス
	class EventHandler1 implements EventHandler<ActionEvent>{//リセットボタンが押された
		public void handle(ActionEvent e){
			//全てのコンボボックスを初期状態(ブランク)にする
			for(int i=0;i<cb_list.size();i++){
				cb_list.get(i).setValue(null);
			}
			
			//初期メッセージを表示
			lb.setText("各コンボボックスで数字を選択してください");
		}
	}
	
	
	class EventHandler2 implements EventHandler<ActionEvent>{//コンボボックスから数字が選択された
		public void handle(ActionEvent e){
			/*
				ゲームクリア判定を行う。
			
				大まかな流れとしては
				横の1列に1～9が1個づつあればflagを1増やす
				縦の1列に1～9が1個づつあればflagを1増やす
				3×3ボックス内に1～9が1個づつあればflagを1増やす
				そうでないとわかった瞬間、break
				最終的にflagが27となった際ゲームクリア
			
			*/
			int flag=0;
			int breakflag=0;//クリアしないことがわかった際に1を代入
			int[] a=new int[10];//空の箱10個を用意→1～9の数字があるかここで記憶
			for(int i=0;i<10;i++){
				a[i]=0;
			}
			int n;//コンボボックスまたはテキストフィールドに入った数字を整数で覚える
			
			
			
			
			
			//横
			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					//各コンボボックスの値を取得し，nullと比較→分岐
					
					if(cb_list.get(i*9+j).getValue()!=null){//i*9+jはi行j列に対応
						//コンボボックスに値がある
						//コンボボックスの値を整数で取得
						n=Integer.parseInt(cb_list.get(i*9+j).getValue().toString());
						
					}else if(board[i][j]!=0){
						//コンボボックスに値が無く、テキストフィールドだ
						//テキストフィールドの値を整数で取得
						n=Integer.parseInt(tf[i][j].getText());
					}else{
						//空欄のコンボボックスだ
						n=0;//nは1～9でない
					}
				
					if(n<=9&&n>=1){
						a[n]++;//nに合わせて1～9の番号のついた箱に入れる
					}else{
						breakflag=1;//空欄を見かけると即座にbreak
						break;
					}
				}
				
				for(int k=2;k<=9;k++){
					//1～9の登場回数は等しいか？
					if(a[1]!=a[k])breakflag=1;
				}
				if(breakflag==1)break;
				flag++;
			}
			
//////////////////////////////////////////////////////////////////////
			//縦(横の場合から行と列を入れ替えて、同様にする)
			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					if(cb_list.get(i+j*9).getValue()!=null){
						n=Integer.parseInt(cb_list.get(i+j*9).getValue().toString());
					}else if(board[j][i]!=0){
						n=Integer.parseInt(tf[j][i].getText());
					}else{
						n=0;
					}
					
					if(n<=9&&n>=1){
						a[n]++;
					}else{
						breakflag=1;
						break;
					}
				}
				
				for(int k=2;k<=9;k++){
					if(a[1]!=a[k])breakflag=1;
				}
				if(breakflag==1)break;
				flag++;
			}
			
//////////////////////////////////////////////////////////////////////
			//3×3ボックス
			
			/*
				3×3ボックス内イメージ
					j→→
				i	0	1	2
				↓	1
				↓	2
			
				ここに、lとmというパラメータを使用し、3×3ボックスをさらに3×3個並べる。
				どの3×3ボックスに居るかがlとmでわかる。
					m→→
				l	0	1	2
				↓	1
				↓	2
			
				lとmを利用してi=0+l*3、j=0+m*3と書くと
				00	01	02	03	04	05	06	07	08
				10	11	12	13	14	15	16	17	18
				20	21	22	23	24	25	26	27	28
				30	31	32	33	34	35	36	37	38
				40	41	42	43	44	45	46	47	48
				50	51	52	53	54	55	56	57	58
				60	61	62	63	64	65	66	67	68
				70	71	72	73	74	75	76	77	78
				80	81	82	83	84	85	86	87	88
				直感的に計算しやすくなる
			
			
			あとは、3×3ボックス内に1～9が1個づつあればflagを1増やすのみ。
			breakの仕方も、横の場合と同様である。
			
			*/
			
			for(int l=0;l<3;l++){
				for(int m=0;m<3;m++){
					for(int i=0+l*3;i<3+l*3;i++){
						for(int j=0+m*3;j<3+m*3;j++){
							if(cb_list.get(i*9+j).getValue()!=null){
								n=Integer.parseInt(cb_list.get(i*9+j).getValue().toString());
							}else if(board[i][j]!=0){
								n=Integer.parseInt(tf[i][j].getText());
							}else{
								n=0;
							}
							if(n<=9&&n>=1){
								a[n]++;
							}else{
								breakflag=1;
								break;
							}
						}
						
						if(breakflag==1)break;
					}
					for(int k=2;k<=9;k++){
							if(a[1]!=a[k])breakflag=1;
					}
					if(breakflag==0){
						flag++;
					}else{
						break;
					}
				}
				if(breakflag==1)break;
			}
//////////////////////////////////////////////////////////////////////
			if(flag==27){
				lb.setText("ゲームクリア！おめでとうございます！！");
			}
		}
	}
}
