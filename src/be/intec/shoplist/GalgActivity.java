package be.intec.shoplist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GalgActivity extends Activity {
	
	
	
			Button mbtnA,mbtnB,mbtnC,mbtnD,mbtnE,mbtnF,mbtnG,mbtnH,mbtnI,mbtnJ,mbtnK,mbtnL,mbtnM,mbtnN,mbtnO,mbtnP,mbtnQ,mbtnR,mbtnS,mbtnT,mbtnU,mbtnV,mbtnW,mbtnX,mbtnY,mbtnZ,mbtnTip;
			Button[] buttons={mbtnA,mbtnB,mbtnC,mbtnD,mbtnE,mbtnF,mbtnG,mbtnH,mbtnI,mbtnJ,mbtnK,mbtnL,mbtnM,mbtnN,mbtnO,mbtnP,mbtnQ,mbtnR,mbtnS,mbtnT,mbtnU,mbtnV,mbtnW,mbtnX,mbtnY,mbtnZ};
			
			ImageButton mbtnTerug;
			TextView mtvWord;
			LinearLayout mllpic;
			
			String tip, word, guesses;
			int count;
			
			Typeface crimeFighter;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(0, 0);
		setContentView(R.layout.galg);
		
		crimeFighter=Typeface.createFromAsset(getAssets(),"fonts/CRIMFBRG.TTF");
		
		for(int i=0;i<buttons.length;i++){
			
			int id=R.id.BtnA+i;
			buttonize(buttons[i], id);
			
		}//end for
		
		buttonize2(mbtnTip, R.id.bTip);
		buttonize(mbtnN, R.id.BtnN);
		mbtnTerug=(ImageButton)findViewById(R.id.bTerug);
		mbtnTerug.setOnClickListener(listener);
		mtvWord= (TextView) findViewById(R.id.tvWord);
		mtvWord.setTypeface(crimeFighter);
		
		newGame();
	}//end onCreate
	
	
	
private void newGame(){
	
	do {
		
		word = newWord();
		
	} while (word.length()>14);
	//end do-While(limit assignment to 14 characters
	
	
	guesses = "";
	
	count = 0;
	
	setBGpic();
	
	setUpWord("");
	
}//end newGame



public void setUpWord(String letter){
	
	char[] charr=word.toCharArray();
	ArrayList<String> letters = new ArrayList<String>();
	ArrayList<String> symbols = new ArrayList<String>();
	
	
	// add letters to list & keep symbols in a different list and remember the positions
	for (int i = 0; i<charr.length;i++){
		
		String temp = Character.toString(charr[i]);
		
		if(temp.contentEquals(" ")||temp.contentEquals("-")||temp.contentEquals(letter)){
			
			symbols.add(i+"°"+temp);
			
		}else if(guesses.contains(temp)){
			
			symbols.add(i+"°"+temp);
			
		}else{
			
			letters.add(" _");
			
		}//end if-else if-else
		
	}//end for
	
	for(int i = 0;i<symbols.size();i++){
		
		String temp = symbols.get(i);
		String[] tempSplit = temp.split("°");
		int index = Integer.parseInt(tempSplit[0]);
		
		letters.add(index,tempSplit[1]);
			
	}//end for
	
	// concatenates letters back into a string
	String wordFinal = "";
	
	for(int i =0;i<letters.size();i++){
		
		wordFinal+=letters.get(i);
	}//end for
	
	mtvWord.setText(wordFinal);
	
}//end setUpWord



public void checkLetter(Button button){
	
	String letter = button.getText().toString().toLowerCase();
	
	if(word.contains(letter)){
		
		setUpWord(letter);
		guesses+=letter;
		
	}else{
		
		count++;
		setBGpic();
		
		if (count>6){
			
			mtvWord.setText(word);
			Toast.makeText(GalgActivity.this, "Oeps!", Toast.LENGTH_SHORT).show();
			mtvWord.postDelayed(b, 3500);
			
		}//end nested if
		
	}//end if-else
	
}//end checkLetter



public void checkSolution(){
	
	String solution = mtvWord.getText().toString();
	
	if(word.contentEquals(solution)&&count<7){
		
		Toast.makeText(GalgActivity.this, "Goed zo!", Toast.LENGTH_SHORT).show();
		mtvWord.setText(word+" ");
		mtvWord.postDelayed(b, 1500);
		
	}//end if
	
}//end checkSolution



private Runnable b= new Runnable() {
	
	@Override
	public void run() {
		
		Intent intent = new Intent(GalgActivity.this, GalgActivity.class);
		startActivity(intent);
		finish();
		
	}//:end run
	
};//end Runnable



private void setBGpic(){
	
	mllpic=(LinearLayout) findViewById(R.id.llPic);
	int id = count + R.drawable.galgje_00;
	mllpic.setBackgroundResource(id);
	
}//end setBGpic



public void buttonize(Button button, int id){
	
	button=(Button)findViewById(id);
	button.setOnClickListener(listener);
	button.setTypeface(null,Typeface.BOLD);
	
}



public void buttonize2(Button button, int id){
	
	button=(Button)findViewById(id);
	button.setOnClickListener(listener);
	button.setTypeface(crimeFighter);
	
}
//end buttonize


public OnClickListener listener = new OnClickListener() {
	
	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		
		case R.id.bTip:
			
			Toast.makeText(GalgActivity.this, tip, Toast.LENGTH_SHORT).show();
			
			break;
			
		case R.id.bTerug:
			
			finish();
			
			break;

		default:
			
			checkLetter((Button)view);
			checkSolution();
			((Button)view).setVisibility(-1);
			
			break;
			
		}//end switch-case
		
	}//end onClick
	
};//end listener



private String newWord(){
	
	String string = getNewWord();
	
	while(string.contains(",")||string.contains("ç")||string.contains("à")||string.contains("è")||string.contains("é")||string.contains("0")||string.contains("1")||string.contains("2")||string.contains("3")||string.contains("4")||string.contains("5")||string.contains("6")||string.contains("7")||string.contains("8")||string.contains("9")){
		
		string = getNewWord();
		
	}//end while
	
	return string;

}//end newWord



// methode to get a random word out of the "expandable list"
private String getNewWord(){
	
	ExpandableListAdapter source=new MyExpandableListAdapter();
	int groupSize = source.getGroupCount();
	Random groupRandom = new Random();
	int groupInt = groupRandom.nextInt(groupSize);
	Random childRandom = new Random();
	int childSize = source.getChildrenCount(groupInt);
	int childInt = childRandom.nextInt(childSize);
	String sourceWord = source.getChild(groupInt, childInt).toString().toLowerCase();
	tip=source.getGroup(groupInt).toString(); // displays the groupname as a hint for the anagram
	
	return sourceWord;
	
}//end getNewWord




public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	 
	// create groupnames from reading file
	    	private ArrayList<String> groups = MyMethods.getGroupNames(GalgActivity.this);

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
	        	
	            TextView textView = new TextView(GalgActivity.this);

	            return textView;
	            
	        }//end getGenericView
	       
	        
	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
	                View convertView, ViewGroup parent) {
	        	
	        	if (convertView == null) {
	        		
	                LayoutInflater inflater = (LayoutInflater) GalgActivity.this.getSystemService(GalgActivity.this.LAYOUT_INFLATER_SERVICE);
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
	        		
	                LayoutInflater infalInflater = (LayoutInflater) GalgActivity.this.getSystemService(GalgActivity.this.LAYOUT_INFLATER_SERVICE);
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
}
