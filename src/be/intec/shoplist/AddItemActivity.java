package be.intec.shoplist;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.intec.shoplist.SpinnerDialog.DialogListener;

public class AddItemActivity extends ExpandableListActivity {
	
	
	
				static final int TAG=0;
				
			    ExpandableListAdapter mAdapter;
			    int lastExpandedGroupPosition;
			    int height;
			    int width;
			    String fileName;
			    String groupNamefortitle;
			    Object tag;
			    
			    int gpos; 
			    int cpos;
			    
			    String myList;
			    Bundle extras;
			    
			    ArrayList<String> spinnerList;
				SpinnerDialog   mSpinnerDialog;
				String[] units = {"kg" , "g" , "L" , "x0.5L" , "x1L" , "x1.5L" , "x2L" , "x" , " fl." , " bl." , " dz" , " zk", " pk", " "};
				String[] unitsFull = {"kg" , "g" , "L" , "x0.5L" , "x1L" , "x1.5L" , "x2L" , "stuks" , "flessen" , "blikken" , "dozen" , "zakken", "pakken", " "};
				String item;
				TextView tvSpinner;
				
				boolean newItemAdded;
				SharedPreferences prefs;
				final static String DATA="welkLijst",LIST="myList";
				
				

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    overridePendingTransition(0, 0);
	    
	    tvSpinner=(TextView) findViewById(R.id.dialog_label);
	    
		prefs = getSharedPreferences(DATA, MODE_PRIVATE);
		myList=prefs.getString(LIST, "currentList");
		
	    DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		
	}//end oncreate
	    
	
	
	@Override
	protected void onResume() {
		  
		super.onResume();
		newItemAdded=false;
		
		//	Set up our adapter
		mAdapter = new MyExpandableListAdapter();
		setListAdapter(mAdapter);
		
		//  get rid of the ugly indicator
		getExpandableListView().setGroupIndicator(null);
		
		// initiate OnChildClickListener
		getExpandableListView().setOnChildClickListener(childListener);	
		
	}//end onResume
	  
	  
	    
	// method to add item to itemlist
	public void newItem(int groupPosition,int childPosition) {
		
		gpos=groupPosition;
		cpos=childPosition;
		AlertDialog.Builder newItemDialog = new AlertDialog.Builder(this);
		// Customize Title
		int titlePad = (width/480)*25;
		TextView customTitleView = new TextView(this);
		customTitleView.setText("~ "+groupNamefortitle+" ~");
		customTitleView.setTextSize(width/20);
		customTitleView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
		LinearLayout.LayoutParams titleParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		customTitleView.setLayoutParams(titleParams);
		customTitleView.setGravity(Gravity.CENTER);
		customTitleView.setPadding(0,titlePad , 0, titlePad);//int left, int top, int right, int bottom
		newItemDialog.setCustomTitle(customTitleView);
		newItemDialog.setMessage("Typ het nieuwe artikel en druk ok");
		
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
		newItemDialog.setView(input);
	
		newItemDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				
				item = input.getText().toString();
				MyMethods.writeItemToItems(item, groupNamefortitle);
				dialog.dismiss();
				newItemAdded=true;
				setQuantity();
	
			}//end onClick
		
		});//end setPositiveButton
		
		newItemDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				
				dialog.cancel();
				
			}//end onClick
	
		});//end setNegativeButton
		
		// Set font for alertdialog widgets
		AlertDialog alert=newItemDialog.show();
		((TextView)alert.findViewById(android.R.id.message)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
		((TextView)alert.findViewById(android.R.id.button1)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
		((TextView)alert.findViewById(android.R.id.button2)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
		
		alert.show();
		
	}//end new item
	    
	    
	    
	// boolean to check edittext
	public boolean getInput(String input){
		
		boolean b;
		
		if (input.contains("1")||input.contains("2")||input.contains("3")||input.contains("4")||input.contains("5")||input.contains("6")||input.contains("7")||input.contains("8")||input.contains("9")){
			
			b=true;
			
		}else{
			
			b=false;
			
		}//end if-else
		
		return b;
		
	}//end getInput
	    
	    
	    
	//dialog to specify the quantity
	public void setQuantity(){
		// Create dialog with spinner and edit text
	    
	    spinnerList = new ArrayList<String>();
	    for(int i=0;i<units.length;i++){
	    	
	    	spinnerList.add(unitsFull[i]);
	    	
	    }//end for
	    
	    mSpinnerDialog = new SpinnerDialog(AddItemActivity.this, spinnerList,item, new DialogListener() {
			
			@Override
			public void ready(int spinnerIndex, String value, String input) {
				
				String temp=getInput(value)?value+units[spinnerIndex]+" "+input:input;
				
					MyMethods.writeItemToList(temp, myList);
					if(newItemAdded){
						
						startActivity(getIntent());
						finish();
						
					}//end if
					
			}//end ready
			
			@Override
			public void cancelled() {
				
				if(newItemAdded){
					
					startActivity(getIntent());
					finish();
					
				}//end if
				
			}//end cancelled
			
		});//end SpinnerDialog
	    
	    mSpinnerDialog.requestWindowFeature(mSpinnerDialog.getWindow().FEATURE_NO_TITLE);
	    
		mSpinnerDialog.show();
		
	}//end setQuantity
	    
	    
	    
	// Setup OnChildClickListener    to get the selected item on the shopping list
	public OnChildClickListener childListener= new OnChildClickListener() {
		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
			
			getExpandableListView().collapseGroup(groupPosition);
			String string = ((TextView)v).getText().toString();
			
			if(string.startsWith("Item toevoegen")){
				
				newItem(groupPosition,childPosition);
				
			}else{
				
				item=mAdapter.getChild(groupPosition, childPosition).toString();
				setQuantity();
	
			}//end if-else
			
			return true;
			
		}//end onChildClick
		
	};//end onChildClickListener
	
	
	
	//get groupPosition to collapse if other is opened
	public OnGroupClickListener groupListener = new OnGroupClickListener() {
		
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {
			
			lastExpandedGroupPosition=groupPosition;
			v.requestFocus();
			
			return true;
		
		}//end onGroupClick
		
	};//end onGroupClickListener
		
	
		
		
	// Inner class for a custom expandablelistadapter
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	    	
		
		//  create groupnames from reading file
		private ArrayList<String> groups = MyMethods.getGroupNames(AddItemActivity.this);
	
		// create childnames from reading files
	    private List<ArrayList<String>> children =new ArrayList<ArrayList<String>>();
		    ArrayList<String> childList=new ArrayList<String>();{
		    	
		     	for (int i=0;i<groups.size();++i){
		     		
		     		childList=MyMethods.getItemList(groups.get(i), childList);
		     		children.add(i,childList);
		     		
		     	}//end for
		     	
		     }//end ChildList
	
		    
	    public Object getChild(int groupPosition, int childPosition) {
	    	
	    	ArrayList<String> list=children.get(groupPosition);
	    	
	        return list.get(childPosition);
	        
	    }//end getChild
	
	    
	    public long getChildId(int groupPosition, int childPosition) {
	    	
	        return childPosition;
	        
	    }//end getChildId
	
	    
	    public int getChildrenCount(int groupPosition) {
	    	
	    	ArrayList<String> list=children.get(groupPosition);
	
	    	return list.size()+1; // +1 to add extra child
	     
	    }//end getChildrenCount
	
	    
	    public TextView getGenericView() {
	    	
	        TextView textView = new TextView(AddItemActivity.this);
	
	        return textView;
	        
	    }//end getGenericView
	 
	    
	    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	    	
	    	int textsize = (width<400)? (height/20)-6:(width/20)-6;// make sure list is readable on small devices
	    	
	    	//link with XML file
	    	if (convertView == null) {
	    		
	            LayoutInflater inflater = (LayoutInflater) AddItemActivity.this.getSystemService(AddItemActivity.this.LAYOUT_INFLATER_SERVICE);
	           	convertView = inflater.inflate(R.layout.child_row, null);
	            
	        }//end if
	
	        TextView textView = (TextView) convertView.findViewById(R.id.tvChild);
	        textView.setWidth(width);
	        
	        //	set up "Button" to add items
	        if(isLastChild){ // Set extra child for adding items - always remains last child
	        	
	        	textView.setText("Item toevoegen aan "+groupNamefortitle);
	        	textView.setTextSize(textsize);
	        	
	        } else if(!isLastChild){ // normal behavior for setting children
	        	
	        	textView.setText(getChild(groupPosition, childPosition).toString()+" ");
	        	textView.setTextSize(textsize);
	        	
	        }//end if-else
	        
	        String string = textView.getText().toString();
	        
	        //        Get a white frame ONLY around add item "Button"
	        if(string.startsWith("Item toevoegen aan ")){
	        	
	        	 Drawable d=getResources().getDrawable(R.drawable.border);
	             textView.setBackgroundDrawable(d);
	             textView.setGravity(Gravity.CENTER_HORIZONTAL);
	             
	        }else{
	        	
	        	textView.setBackgroundDrawable(null);
	        	
	        }//end if-else
	        
	        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
	       
	        return convertView;
	        
	    }//end getChildView
	    
	    
	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	    	
	        return true;
	        
	    }//end isChildSelectable
	    
	    
	    public Object getGroup(int groupPosition) {
	    	
	        return groups.get(groupPosition);
	        
	    }//end getGroup
	
	    
	    public int getGroupCount() {
	    	
	        return groups.size();
	        
	    }//end getGroupCount
	
	    
	    public long getGroupId(int groupPosition) {
	    	
	        return groupPosition;
	        
	    }//end getGroupId
	
	    
	    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	    	
	    	// 	Layout Parameters for group view
	    	int left = width/40;
	    	int right = left/10;
	    	int top;
	    	int bottom;
	    	int textsize;
	    	
	    	if(width<400){
	    		
	        	top = left/2;
	        	bottom = top;
	    		textsize = height/20;
	    		
	    	}else{
	    		
	        	top = left;
	        	bottom = top;
	    		textsize=width/20;
	    		
	    	}//end if-else
	    	
	    	if (convertView == null) {
	    		
	            LayoutInflater iflater = (LayoutInflater) AddItemActivity.this.getSystemService(AddItemActivity.this.LAYOUT_INFLATER_SERVICE);
	            convertView = iflater.inflate(R.layout.group_row, null);
	            
	        }//end if
	
	        TextView textView = (TextView) convertView.findViewById(R.id.tvGroup); 
	        
	        textView.setText(getGroup(groupPosition).toString()+" ");
	        textView.setWidth(width);
	        textView.setTextSize(textsize);
	        textView.setPadding(left, top, right, bottom);
	        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CRIMFBRG.TTF"));
	
	        
	        return convertView;
	        
	    }//end getGroupView
	    
	
	    //	collapse the old expanded group, if not the same as new group to expand
		@Override
	    public void onGroupExpanded(int groupPosition){
			
	        if(groupPosition != lastExpandedGroupPosition){
	        	
	          getExpandableListView().collapseGroup(lastExpandedGroupPosition);
	          getExpandableListView().setSelection(groupPosition);
	          
	        }//end if
	
	        super.onGroupExpanded(groupPosition);           
	        lastExpandedGroupPosition = groupPosition;
	        groupNamefortitle=getGroup(groupPosition).toString();
	        
	    }//end onGroupExpanded 
	
		
	    public boolean hasStableIds() {
	    	
	        return true;
	        
	    }//end hasStableIds
	    
	   
	
	}//end MyExpandaleListAdapter




}//end ShopListActivity