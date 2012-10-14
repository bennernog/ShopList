package be.intec.shoplist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AnagramActivity extends Activity implements OnClickListener{
	
	
			Button mbNewWord, mbShuffle, mbHelp, mbbackspace;
			ImageButton mbBack;
			TextView mtvTheme,mtvNewWord,mtvLevel,mtvLevelTitle;
			LinearLayout mllNewWord;
			
			ArrayList<Button> inviButtons;
			ArrayList<View> buttonList,helpButtons;
			String word;
			ArrayList<String> array=new ArrayList<String>();
			int height, width, textSize, marginL, requestCode, helpCount, btnCount;
			int level=1;
		
			Typeface crimeFighter;
	
	
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		overridePendingTransition(0, 0);
		setContentView(R.layout.anagram_2);
		
		//get screen dimensions
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
			
		crimeFighter=Typeface.createFromAsset(getAssets(),"fonts/CRIMFBRG.TTF");
		
			mbNewWord = (Button) findViewById(R.id.bNewGame);
			mbShuffle = (Button) findViewById(R.id.bShuffle);
			mbHelp = (Button) findViewById(R.id.bHelp);
			mbBack = (ImageButton) findViewById(R.id.bBack);
			mbbackspace = (Button) findViewById(R.id.bbackspace);
			mtvTheme = (TextView) findViewById(R.id.tvTheme);
			mtvNewWord = (TextView) findViewById(R.id.tvWord);
			mtvLevel = (TextView) findViewById(R.id.tvLevel);
			mtvLevelTitle = (TextView) findViewById(R.id.tvLevelTitle);
			mllNewWord = (LinearLayout) findViewById(R.id.llNewWord);
			
			mbNewWord.setOnClickListener(this);
			mbShuffle.setOnClickListener(this);
			mbbackspace.setOnClickListener(this);
			mbHelp.setOnClickListener(this);
			mbBack.setOnClickListener(this);
			mtvNewWord.addTextChangedListener(textwatcher);
		
		inviButtons = new ArrayList<Button>();
		
			mtvNewWord.setTextColor(getResources().getColor(R.color.Dark_Blue));
			float size= mtvLevelTitle.getTextSize()+1;
			mtvLevelTitle.setTextSize(size);
			mtvLevel.setTextSize(size);
			mtvTheme.setTextSize(size);
			
			mtvNewWord.setTypeface(crimeFighter);
			mtvTheme.setTypeface(crimeFighter);
			mtvLevel.setTypeface(crimeFighter);
			mtvLevelTitle.setTypeface(crimeFighter);
			mbNewWord.setTypeface(crimeFighter);
			mbShuffle.setTypeface(crimeFighter);
			mbbackspace.setTypeface(crimeFighter);
			mbHelp.setTypeface(crimeFighter);
		
		getGame();
		
}//end onCreate()
	
	
	
//Setup OnClickListener 
@Override
public void onClick(View view) {
	
		switch (view.getId()) {
		
			case R.id.bNewGame:
				
				getGame();
				
				break;
	
			case R.id.bShuffle:
				
				shuffleButtons();
				
				break;
				
			case R.id.bHelp:
				
				helpCount++;
				getHelp();
				shuffleButtons();
				
				break;
				
			case R.id.bbackspace:
				
				String temp = mtvNewWord.getText().toString();
				int l = temp.length()-1;
				
				if(l>=0){
					
					mtvNewWord.setText(temp.substring(0, l));
					(inviButtons.get(btnCount)).setVisibility(View.VISIBLE);
					inviButtons.remove(btnCount);
					btnCount--;
					
				}//end if
				
				break;
				
			case R.id.bBack:
				
				finish();
				
				break;
				
			default:
				
				temp = ((Button )view).getText().toString();
				mtvNewWord.append(temp);
				((Button)view).setVisibility(View.INVISIBLE);
				inviButtons.add((Button)view);
				btnCount++;
				
				break;
				
		}//end switch-case
		
}//end OnClick



//setup TextWatcher
public TextWatcher textwatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			if(mtvNewWord.getText().toString().length()==word.length()){
				
				checkSolution();
				
			}//end if
			
		}//end onTextChanged
		
		@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
		@Override public void afterTextChanged(Editable arg0) {}
		
};//end textwatcher

	
	

// methode to get a random word out of the "expandable list"
private String getNewWord(){
	
		String sourceWord, newWord;
		ExpandableListAdapter AnagramSource=new MyExpandableListAdapter();
		Random groupRandom = new Random();
		Random childRandom = new Random();
		int groupSize = AnagramSource.getGroupCount();
		
		//avoid repetition
		do{
			
			int groupInt = groupRandom.nextInt(groupSize);
			int childSize = AnagramSource.getChildrenCount(groupInt);
			int childInt = childRandom.nextInt(childSize);
			sourceWord = AnagramSource.getChild(groupInt, childInt).toString();
			mtvTheme.setText(AnagramSource.getGroup(groupInt).toString()); // displays the groupname as a hint for the anagram
		
		}while(array.contains(sourceWord));//end do-while
		
		
		
		return sourceWord;
		
}//end getNewWord()



private static String removeEndSpaces(String string){
	
	String newWord=string;
	
	while (newWord.endsWith(" ")) {
		
		newWord = string.substring(0, (newWord.length()-1));
		
	}//end while
	
	return newWord;
	
}//end removeEndSpaces



//reset counters and get new assignment
public void getGame(){
	
		inviButtons.clear();
		btnCount=-1;
		helpCount=0;
		int x =3+level;
		int y=(x<=11)?x:11;
		String temp;
		
		do {
			temp=getNewWord();
			word = removeEndSpaces(temp); 
			
		} while (word.length()>y);//end do-while
		
		array.add(word);
		mtvNewWord.setText("");
		mtvLevel.setText(level+"");
		setGame(word);
		shuffleButtons();
		
}//end getGame()
	


//Setup the game in the layout	
public void setGame(String anagram){
	
	    mllNewWord.removeAllViewsInLayout();
		buttonList=new ArrayList<View>();
		
		for (int i = 0; i < anagram.length(); i++) {
			
			int textsize=(width<400)? height/20:(width/20)-3;
			LayoutInflater btnLetterInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
	        View convertView = btnLetterInflater.inflate(R.layout.anagram_letter, null);
	        Button gameButton = (Button) convertView.findViewById(R.id.bLetter);
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2, 1);
	        
	           gameButton.setLayoutParams(params);
	           gameButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/CRIMFBRG.TTF"));
	           gameButton.setTextSize(textsize);
	           mtvNewWord.setTextSize(textsize);
	         
	           char letter=anagram.charAt(i);
	           char data[] = { letter };
	           String temp = new String(data);
	           
	           gameButton.setText(temp);
	           gameButton.setOnClickListener(this);
	           
	           buttonList.add(gameButton);
	           helpButtons=buttonList;
	           mllNewWord.addView(gameButton);
	           
		}//end for
		
}//end setGame



//Method to check that the word typed matches the solution.
public void checkSolution(){
	
		String myTry = mtvNewWord.getText().toString();
		String checkMessage;
		
		if(word.contentEquals(myTry)){
			
			if(helpCount<word.length()-1){
				
				checkMessage = "Goed zo!";
				level++;
				
			}else{
				
				checkMessage= "Valsspeler!";
				
			}//end nested if-else
			
			mtvNewWord.postDelayed(nextGame, 1500);
			
		}else{
			
			checkMessage = "Fout!";
		}//end if-else
		
		Toast.makeText(getApplicationContext(), checkMessage, Toast.LENGTH_SHORT).show();
		
}//end chekSolution()
	


//link the help function with the buttons in the layout
public void getHelp(){
	
			inviButtons.clear();
			btnCount=-1;
			String helpWord="";
			
			if(helpCount<=word.length()){
				
				helpWord=word.substring(0, helpCount);
				mtvNewWord.setText(helpWord);
				
			}//end if
					
			//empty layout
			mllNewWord.removeAllViewsInLayout();
			
			for (int i=0;i<helpWord.length();i++){
				
				(helpButtons.get(i)).setVisibility(View.INVISIBLE);
				inviButtons.add((Button)helpButtons.get(i));
				btnCount++;
				
			}//end for
			
			// add buttons to layout	
			for(int i = 0 ; i<helpButtons.size();i++){
				
						mllNewWord.addView(helpButtons.get(i));
						
			}//end for
			
}// end needHelp()



//method to shuffle the buttons
public void shuffleButtons(){
	
		String temp;
		buttonList = new ArrayList<View>();
		
		//get the buttons in a list
		for(int i=0;i<mllNewWord.getChildCount();i++){
			
			buttonList.add(mllNewWord.getChildAt(i));
			
		}//end for
		
		//empty layout
		mllNewWord.removeAllViewsInLayout();
		
		//shuffle excluding the solution as a possibility
		do{
			
			temp="";
			Collections.shuffle(buttonList);
			
			for(View v:buttonList){
				
				temp+=((Button)v).getText().toString();
				
			}//end for
			
		}while(temp.contentEquals(word));//end do-while
		
		//add buttons back to layout
		for(View v:buttonList){
			
			mllNewWord.addView(v);
			
		}//end for
		
}//end shuffleButtons()



//Runnable to start a new game with a slight delay
private Runnable nextGame= new Runnable() {
		
		@Override
		public void run() {
			
			getGame();
			
		}//end run
		
};//end nextGame
	
	
	
//  expandablelistadapter
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
 
// create groupnames from reading file
    	private ArrayList<String> groups = MyMethods.getGroupNames(AnagramActivity.this);

// create childnames from reading files
         private List<ArrayList<String>> children =new ArrayList<ArrayList<String>>();
         ArrayList<String> childList=new ArrayList<String>();{
        	 
         	for (int i=0;i<groups.size();++i){
         		
         		childList=MyMethods.getItemList(groups.get(i), childList);
         		children.add(i,childList);
         		
         	}//end for
         	
         }//end childList

         
        public Object getChild(int groupPosition, int childPosition) {
        	
        	ArrayList<String> list=children.get(groupPosition);
            return list.get(childPosition);
            
        }//end getChild

        
        public long getChildId(int groupPosition, int childPosition) {
        	
            return childPosition;
            
        }//end getChildId

        
        public int getChildrenCount(int groupPosition) {
        	
        	ArrayList<String> list=children.get(groupPosition);

        	return list.size();
        	
        }//end getChildrenCount

        
        public TextView getGenericView() {
        	
            TextView textView = new TextView(AnagramActivity.this);

            return textView;
            
        }//end getGenericView
       
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
        	
        	if (convertView == null) {
        		
                LayoutInflater inflater = (LayoutInflater) AnagramActivity.this.getSystemService(AnagramActivity.this.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_row, null);
                
            }//end if
           
            return convertView;
            
        }//end getChildView

        
        public Object getGroup(int groupPosition) {
        	
            return groups.get(groupPosition);
            
        }//end getGroup

        
        public int getGroupCount() {
        	
            return groups.size();
            
        }//end getGroupCount

        
        public long getGroupId(int groupPosition) {
        	
            return groupPosition;
            
        }//end getGroupId

        
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
        	
        	if (convertView == null) {
        		
                LayoutInflater infalInflater = (LayoutInflater) AnagramActivity.this.getSystemService(AnagramActivity.this.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.group_row, null);
                
            }//end if
            
            return convertView;
            
        }//end getGroupView

        
        public boolean isChildSelectable(int groupPosition, int childPosition) {
        	
            return true;
            
        }//end isChildSelectable

        
        public boolean hasStableIds() {
        	
            return true;
            
        }//end hasStableIds
        
}//end MyExpandableListAdapter


}//end AnagramActivity
