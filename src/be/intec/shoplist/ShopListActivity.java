package be.intec.shoplist;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ShopListActivity extends Activity {


			private final int INFO_MENU= Menu.FIRST;
			private final int SAVE_MENU= Menu.FIRST+1;
			private final int LISTS_MENU= Menu.FIRST+2;
			private final int GAME_MENU= Menu.FIRST+3;
		
			Typeface crimeFighter;
			AlertDialog mAlertDialog;
			ArrayAdapter<String> adpter;
		
			TextView newTV;
			ScrollView itemListScrollView;
			LinearLayout itemLinearLayout;
			
			ImageButton mbtnAddItem;
			ImageButton mbtnDeleteItem;
			ImageButton mbtnNewList;
			
			ArrayList<String> shoppingList;
			ArrayList<String> scratchList;
			HashMap<String, Boolean> checkerList;
			ArrayList<String> myLists;
			String theList,myList;
			String[] games= new String[]{"Anagram","Galgje"};
			Intent gameIntent;
			int height, width;
			
			SharedPreferences.Editor editor;
			SharedPreferences prefs;
			final static String DATA="welkLijst",LIST="myList", INDEX="ListIndex",CHECK="CHECK",GAME="GAME";
			int count=0, id,whichGame;
			boolean infoAtStartUp;
			
			
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
			super.onCreate(savedInstanceState);
			overridePendingTransition(0, 0);
			setContentView(R.layout.shopping_list);
	
			crimeFighter=Typeface.createFromAsset(getAssets(),"fonts/CRIMFBRG.TTF");
			
			mbtnAddItem =(ImageButton)findViewById(R.id.btnAddItem);
			mbtnDeleteItem = (ImageButton)findViewById(R.id.btnDeleteItem);
			mbtnNewList = (ImageButton)findViewById(R.id.btnNEWList);
			mbtnAddItem.setOnClickListener(listener);
			mbtnDeleteItem.setOnClickListener(listener);
			mbtnNewList.setOnClickListener(listener);
			
			//	get screen dimensions
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			height = metrics.heightPixels;
			width = metrics.widthPixels;
			
			// 	create ScrollView for shoppingList
			itemListScrollView = (ScrollView) findViewById(R.id.svItemList);
			itemLinearLayout = (LinearLayout) findViewById(R.id.llItemList);
	
			prefs=getSharedPreferences(DATA, MODE_PRIVATE);
			editor=prefs.edit();
			infoAtStartUp=prefs.getBoolean(CHECK, true);
			
			if(infoAtStartUp){
				
				showInfo();
				
			}//end if
				
				
	}//end oncreate
		
		
	
	@Override
	protected void onResume() {
			
				super.onResume();
				
				myList=prefs.getString(LIST, "currentList");
				shoppingList = MyMethods.getList(myList);
				scratchList = MyMethods.getList("scratchFile");
				myLists = MyMethods.getMyLists();
				
				makeList();
				
	}//end onResume
	
	
	
	//setup OnClickListener
	public OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent;
				
				switch (v.getId()) {	
				
				case R.id.btnAddItem:
					
					intent = new Intent(ShopListActivity.this, AddItemActivity.class);
					startActivity(intent);
					
					break;
				
				case R.id.btnDeleteItem:
					
					if(itemLinearLayout.getChildCount()>0){
						
						deleteItems();
						
					}else{
	
						showToast("Er valt niks te wissen.\nDruk op + om een lijst te maken");
					
					}//end if-else
					
					break;
					
				case R.id.btnNEWList:
					
					if(itemLinearLayout.getChildCount()>0){
						
						reset();
						
					}else{
	
						showToast("Dit is een nieuwe lijst.\nDruk op + om een lijst te maken");
					
					}//end if-else
					
					break;
					
				}//end switch-case
				
			}//end onClick
			
	};//end listener
	
	
	
	// method to scratch/unscratch text on slide
	public OnTouchListener scratcher = new OnTouchListener() {
		
			float firstX;
			float lastX;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				String item = ((TextView)v).getText().toString();
				
				switch (event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
					
					firstX = event.getX();
					
					break;
	
				case MotionEvent.ACTION_UP:
					
					lastX = event.getX();
					
					if (firstX < lastX) {// left to right to strike thru text
						
						((TextView) v).setPaintFlags(((TextView) v).getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
						 scratchList.add(item);
	
					}//end if
					
					if (firstX > lastX) {// right to left to undo
						
						((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
						 scratchList.remove(item);
						 
					}//end if
					
					break;
					
				}//end switch-case
				
				return true;
				
			}//end onTouch
			
	};//end scratcher
	
	
	
	// put elements of ShoppingList into seperate textview and add to scrollview
	public void makeList() {
			
			itemLinearLayout.removeAllViewsInLayout();
	    	int textsize;
	    	
	    	if(width<400){
	    		
	    		textsize = height/20;
	    		
	    	}else{
	    		
	    		textsize=(width/20)-3;
	
	    	}//end if-else
			
			for (int i = 0; i < shoppingList.size(); i++) {
				
			   LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
	           View convertView = inflater.inflate(R.layout.shop_row, null);
	
	           newTV = (TextView) convertView.findViewById(R.id.tvShop);
	           newTV.setText(shoppingList.get(i));
	           newTV.setTypeface(crimeFighter);
	           newTV.setTextSize(textsize);
	           
	           if(scratchList.contains(shoppingList.get(i))){
	        	   
					 newTV.setPaintFlags(newTV.getPaintFlags() |
					 Paint.STRIKE_THRU_TEXT_FLAG);
					 
	           }//end if
	           
	           newTV.setOnTouchListener(scratcher);
	           itemLinearLayout.addView(newTV);
	           
			}//end for
			
	}//end makeList
	
	
	
	// deleteItems from List
	public void deleteItems(){
		
		checkerList = new HashMap<String,Boolean>();
		String[] itemsOnList= new String[shoppingList.size()];
		boolean[] itemsToDelete= new boolean[shoppingList.size()];
		shoppingList.toArray(itemsOnList);
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShopListActivity.this);
		dialogBuilder.setTitle("Kies de items die u wilt verwijderen");
		dialogBuilder.setIcon(R.drawable.icon);
		
		dialogBuilder.setMultiChoiceItems(itemsOnList, itemsToDelete, new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					checkerList.put( shoppingList.get(which),isChecked);
			}//end onClick
			
		});//end setMultiChoiceItems
		
		dialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				for(String t : checkerList.keySet()){
					
					if(checkerList.get(t)){
						
						shoppingList.remove(t);
						
						if(scratchList.contains(t)){
							
							scratchList.remove(t);
							
						}//end nested if
						
					}//end if
					
				}//end for
				
				dialog.dismiss();
				startActivity(getIntent());
				finish();
				
			}//end onClick
			
		});//end setPositiveButton
		
		dialogBuilder.setNegativeButton("Sluit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
				
			}//end onClick
			
		});//end setNegativeButton
		
		AlertDialog alert=dialogBuilder.show();
		
		((TextView)alert.findViewById(android.R.id.button1)).setTypeface(crimeFighter);
		((TextView)alert.findViewById(android.R.id.button2)).setTypeface(crimeFighter);
			
	}//end deleteItems()
		
		
		
	//reset to start a new list
	public void reset(){
			
			MyMethods.prepNewList();
			shoppingList.clear();
			scratchList.clear();
			myList="currentList";
			editor.putString(LIST,"currentList");
			editor.commit();
			startActivity(getIntent());
			finish();
			
	}//end reset
	
	
	
	//setup menuButtons
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(Menu.NONE, INFO_MENU, Menu.NONE,R.string.info);
		menu.add(Menu.NONE, SAVE_MENU, Menu.NONE, "Opslaan");
		menu.add(Menu.NONE, LISTS_MENU, Menu.NONE, "Mijn Lijsten");
		menu.add(Menu.NONE, GAME_MENU, Menu.NONE, "Spelletjes");
		
		return super.onCreateOptionsMenu(menu);
		
	}//end onCreateOptionsMenu
	
	
	
	//setup menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
			switch (item.getItemId()) {
			
			case INFO_MENU:
				
				showInfo();
				
				break;
		
			case SAVE_MENU:
				
				if(itemLinearLayout.getChildCount()>0){
					
					saveList(myList, shoppingList);
				}else{
	
					showToast("Er valt niks op te slagen");
				
				}//end if-else
				
				break;
		
			case LISTS_MENU:
				
				if(myLists.size()>0) 	{
					
	//				if(myLists.get(0).length()>1) {
						myLists();
	//				}
					
				}else{
					
					showToast("Er zijn geen opgeslagen lijsten.");
					
				}//end if-else
				
				break;
				
			case GAME_MENU:
				
				pickAgame();
				
				break;
				
			}//end switch-case
		
		return super.onOptionsItemSelected(item);
		
	}//end onOptionsItemSelected
	
	
	
	//Custom dialog for info
	public void showInfo(){
		//	create dialog
		final Dialog dialog= new Dialog(ShopListActivity.this);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.info_shopping_list);
		dialog.setCancelable(true);
		
		//	prep info drawables
		final LinearLayout mllinfo=(LinearLayout) dialog.findViewById(R.id.llInfo);
		final LinearLayout mllCheckBox=(LinearLayout) dialog.findViewById(R.id.llCheckBox);
		final TextView mtvInfo = (TextView)dialog.findViewById(R.id.tvInfoTonen);
		final CheckBox checker= (CheckBox)dialog.findViewById(R.id.cbInfo);
		mllinfo.setBackgroundResource(R.drawable.info_1);
		mllCheckBox.setVisibility(View.VISIBLE);
		checker.setChecked(infoAtStartUp);
		
		
		//	setup views
		Button btnSluit = (Button) dialog.findViewById(R.id.bOK);
		final Button btnVorige = (Button) dialog.findViewById(R.id.bVorige);
		final Button btnVolgende = (Button) dialog.findViewById(R.id.bVerder);
		mtvInfo.setTypeface(crimeFighter);
		mtvInfo.setTextColor(getResources().getColor(R.color.Dark_Blue));
		btnSluit.setTypeface(crimeFighter);
		btnVorige.setTypeface(crimeFighter);
		btnVolgende.setTypeface(crimeFighter);
		btnVorige.setText("<--");
		btnVolgende.setText("-->");
		btnVorige.setTextColor(Color.GRAY);
	
	    //set up button
	    btnSluit.setOnClickListener(new OnClickListener() {
	    	
	    	@Override
	        public void onClick(View v) {
	    		
	    		dialog.cancel();
	    	
	        }//end onClick
	    	
	    });//end btnSluit.setOnClickListener
	    
	    btnVorige.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(count>0){
					
					mllinfo.setBackgroundResource(--count + R.drawable.info_1);
					
					if (count<3){
						
						btnVolgende.setTextColor(Color.BLACK);
						
						if(count==0){
							
							mllCheckBox.setVisibility(View.VISIBLE);
							((Button)v).setTextColor(Color.GRAY);
							
						}//end nested if
						
					}//end nested if
					
					Log.d("btnVorige",count+"");
					
				}
				
			}//end onClick
			
		});//end btnVorige.setOnClickListener
	    
	    btnVolgende.setOnClickListener(new OnClickListener() {
	    	
	    	@Override
	    	public void onClick(View v) {
	    		
	    		if(count<3){
					
					mllinfo.setBackgroundResource(++count + R.drawable.info_1);
					Log.d("btnVolgende",count+"");
					
					if(count>0){
						
						mllCheckBox.setVisibility(View.INVISIBLE);
						btnVorige.setTextColor(Color.BLACK);
						
						if(count==3){
							
							((Button)v).setTextColor(Color.GRAY);
							
						}//end nested if
						
					}//end nested if
					
				}
	    		
	    	}//end onClick
	    	
	    });//end btnVolgende.setOnClickListener
	    
	    checker.setOnCheckedChangeListener(checklistener());
	    
		dialog.show();
		
	}//end showInfo
	
	
	
	private OnCheckedChangeListener checklistener(){
		OnCheckedChangeListener check= new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
				editor.putBoolean(CHECK, isChecked);
				editor.commit();
				
			}
		};
		return check;
	}
	
	
	
	//Open myLists
	public void myLists(){
		
			AlertDialog.Builder myListsBuilder= new AlertDialog.Builder(ShopListActivity.this);
			myListsBuilder.setTitle("Mijn Lijsten");
			myListsBuilder.setIcon(R.drawable.icon);
			
			myListsBuilder.setSingleChoiceItems(myLists.toArray(new String[myLists.size()]), 0, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					if(!(which<0)){
						
						theList=myLists.get(which);
						editor.putString(LIST, theList);
						editor.putInt(INDEX, which);
						editor.commit();
						
					}//end if
					
				}//end onClick
				
			});//end setSingleChoiceItems
			
			myListsBuilder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(getIntent());
					
				}//end onClick
				
			});//end setPositiveButton
			
			myListsBuilder.setNeutralButton("Wis", new DialogInterface.OnClickListener() {
				
				int index = prefs.getInt(INDEX, 0);
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
						myLists = MyMethods.getMyLists();
						
						dialog.dismiss();
						
						theList=myLists.get(index);
						MyMethods.deleteList(theList);
						
						if(theList.contentEquals(myList)){
							
							reset();	
							
						}//end nested if
						
						myLists.remove(theList);
					
				}//end onClick
				
			});//end setNeutralButton
			
			myListsBuilder.setNegativeButton("Sluit", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
					
				}//end onClick
				
			});//end setNegativeButton
			
			AlertDialog alert=myListsBuilder.show();
			
			((TextView)alert.findViewById(android.R.id.button1)).setTypeface(crimeFighter);
			((TextView)alert.findViewById(android.R.id.button2)).setTypeface(crimeFighter);
			((TextView)alert.findViewById(android.R.id.button3)).setTypeface(crimeFighter);
			
	}//end myLists
	
	
	
//methods to save list for later use
	public void saveList(String fileName, ArrayList<String> list){
		
			ArrayList<String> temp = MyMethods.getMyLists();
			
			if(temp.contains(fileName)){
				
				confirmOverwrite(fileName);
				
			}else{
				
				saveAs();
				
			}//end if-else
			
	}//end saveList
	
	
	
	//Method to save list list unde a different name
	public void saveAs(){
		
			AlertDialog.Builder newItemDialog = new AlertDialog.Builder(this);
			
			// Customize Title
	    	int titlePad = (width/480)*25;
	    	final String title="~ opslaan als ~";
	    	TextView customTitleView = new TextView(this);
	    	customTitleView.setText(title);
	    	customTitleView.setTextSize(width/20);
	    	customTitleView.setTypeface(crimeFighter);
	    	LinearLayout.LayoutParams titleParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	    	customTitleView.setLayoutParams(titleParams);
	    	customTitleView.setGravity(Gravity.CENTER);
	    	customTitleView.setPadding(0,titlePad , 0, titlePad);//int left, int top, int right, int bottom
	    	newItemDialog.setCustomTitle(customTitleView);
			newItemDialog.setMessage("geef u lijst een naam en druk ok");
			
			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			input.setTypeface(crimeFighter);
			String listName = setSaveName();
			input.setText(listName);
			newItemDialog.setView(input);
	
			newItemDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					String fileName = input.getText().toString();
					ArrayList<String> tempi = MyMethods.getMyLists();
					
					if(tempi.contains(fileName)){
						
						confirmOverwrite(fileName);
						
					}else{
						
						MyMethods.saveNewList(fileName, shoppingList);
						startActivity(getIntent());
						finish();
					
					}//end if-else
						
				}//end onClick
			
			});//end setPositiveButton
			
			newItemDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			
				public void onClick(DialogInterface dialog, int whichButton) {
						
						dialog.cancel();
	
				}//end onClick
					
			});//end setNegativeButton
			
			AlertDialog alert=newItemDialog.show();
			
			// Set font for alertdialog widgets
			((TextView)alert.findViewById(android.R.id.message)).setTypeface(crimeFighter);
			((TextView)alert.findViewById(android.R.id.button1)).setTypeface(crimeFighter);
			((TextView)alert.findViewById(android.R.id.button2)).setTypeface(crimeFighter);
				
			alert.show();
				
	}//end saveAs()
	
	
	
	//
	public void confirmOverwrite(String fileName){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
				final String temp = fileName;
				int titlePad = (width/480)*25;
				TextView customTitleView = new TextView(this);
				customTitleView.setText("~ "+"Lijst bestaat al!"+" ~");
				customTitleView.setTextSize(width/20);
				customTitleView.setTypeface(crimeFighter);
				LinearLayout.LayoutParams titleParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				customTitleView.setLayoutParams(titleParams);
				customTitleView.setGravity(Gravity.CENTER);
				customTitleView.setPadding(0,titlePad , 0, titlePad);//int left, int top, int right, int bottom
				builder.setCustomTitle(customTitleView);
				builder.setMessage("Overschrijven?");
				
			builder.setPositiveButton("JA", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					MyMethods.writeListToFile(shoppingList, temp);
	
				}//end onClick
				
			});//end setPositiveButton
			
			
			builder.setNeutralButton("Sluit", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					
				}//end onClick
				
			});//end setNeutralButton
			
			
			builder.setNegativeButton("NEEN",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					
					saveAs();
					
				}//end onClick
						
			});//end setNegativeButton
	
				AlertDialog alert=builder.show();
				((TextView)alert.findViewById(android.R.id.message)).setTypeface(crimeFighter);
				((TextView)alert.findViewById(android.R.id.button1)).setTypeface(crimeFighter);
				((TextView)alert.findViewById(android.R.id.button2)).setTypeface(crimeFighter);
				((TextView)alert.findViewById(android.R.id.button3)).setTypeface(crimeFighter);
				
	}// end overwrite
	
	
	
	public void prompForSave(){
		
		int titlePad = (width/480)*25;
	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		TextView customTitleView = new TextView(this);
		customTitleView.setText("~ "+"Er is een lijst actief!"+" ~");
		customTitleView.setTextSize(width/20);
		customTitleView.setTypeface(crimeFighter);
		LinearLayout.LayoutParams titleParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		customTitleView.setLayoutParams(titleParams);
		customTitleView.setGravity(Gravity.CENTER);
		customTitleView.setPadding(0,titlePad , 0, titlePad);//int left, int top, int right, int bottom
		builder.setCustomTitle(customTitleView);
		builder.setMessage("opslaan?");
		
		builder.setPositiveButton("JA", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				
				saveList(myList, shoppingList);
				MyMethods.prepNewList();
				myList="currentList";
				shoppingList.clear();
				scratchList.clear();
				startActivity(getIntent());
				finish();
				
			}//end onClick
			
		});//end setPositiveButton
		
		builder.setNegativeButton("NEEN",new DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int whichButton) {
						
						MyMethods.prepNewList();
						myList="currentList";
						shoppingList.clear();
						scratchList.clear();
						startActivity(getIntent());
						finish();
						
					}//end onClick
					
				});//end setNegativeButton
	
		AlertDialog alert=builder.show();
		((TextView)alert.findViewById(android.R.id.message)).setTypeface(crimeFighter);
		((TextView)alert.findViewById(android.R.id.button1)).setTypeface(crimeFighter);
		((TextView)alert.findViewById(android.R.id.button2)).setTypeface(crimeFighter);
		((TextView)alert.findViewById(android.R.id.button3)).setTypeface(crimeFighter);
	
	}//end prompforsave
	
	
	
	public String setSaveName(){
		
		myLists = MyMethods.getMyLists();
		int lijsten = myLists.size();
		do {
			lijsten++;
		} while (myLists.contains("Lijst-"+lijsten));
		
		return "Lijst-"+lijsten;
		
	}//end setSaveName
//end	methods to save list for later use
	
	
	
	//pick a game
	public void pickAgame(){
		
		whichGame = prefs.getInt(GAME, 0);
		AlertDialog.Builder gameBuilder= new AlertDialog.Builder(ShopListActivity.this);
		gameBuilder.setTitle("Kies een spel");
		gameBuilder.setIcon(R.drawable.icon);
		
		
		//	setSingleChoiceItems (CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener)
		gameBuilder.setSingleChoiceItems(games, whichGame, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int game) {
				
				if(game>=0){
					
					switch (game) {
					
					case 0:
						
						editor.putInt(GAME, 0);
						editor.commit();
						break;
						
					case 1:
						
						editor.putInt(GAME, 1);
						editor.commit();
						
						break;
	
					}//end switch/case
					
				}//end if
				
			}//end onClick
			
		});//end setSingleChoiceItems
		
		
		gameBuilder.setPositiveButton("Speel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				int game = prefs.getInt(GAME, 0);
				switch (game) {
				
				case 0:
					
					gameIntent=new Intent(ShopListActivity.this,AnagramActivity.class);
					
					break;
					
				case 1:
					
					gameIntent=new Intent(ShopListActivity.this,GalgActivity.class);
					
					break;

				}//end switch/case
				dialog.dismiss();
				startActivity(gameIntent);
				
			}//end onClick
			
		});//end setPositiveButton
		
		gameBuilder.setNegativeButton("Sluit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				
				dialog.cancel();
				
			}//end onClick
			
		});//end setNegativeButton
		
		AlertDialog alert=gameBuilder.show();
		((TextView)alert.findViewById(android.R.id.button1)).setTypeface(crimeFighter);
		((TextView)alert.findViewById(android.R.id.button2)).setTypeface(crimeFighter);
	
	}//end pickAgame
	
	
	
	//makes showing toasts much easier
	public void showToast(String message){
		
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		
	}//end showToast
	
	
	
	// send list via sms/email:? to be investigated
	public void invokeSMSApp(String Message, URI uri){
		
	    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
	    smsIntent.putExtra("sms_body", "Hello World!");
	    smsIntent.setType("vnd.android-dir/mms-sms");
	    smsIntent.setData(null);
	    startActivity(smsIntent);
	
	}//end invokeSMSApp
	
	
	
	@Override
	protected void onPause() {
		
		MyMethods.writeListToFile(scratchList, "scratchFile");
		MyMethods.writeListToFile(shoppingList, myList);
		super.onPause();
		
	}//end onPause



}//end ShopListActivity
