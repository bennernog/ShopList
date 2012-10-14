package be.intec.shoplist;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
// Class for custom dialog
public class SpinnerDialog extends Dialog {
    private ArrayList<String> mList;
    private Context mContext;
    private Spinner mSpinner;
    private EditText mEditText;
    private TextView mTextView;

   public interface DialogListener {
        public void ready(int spinnerIndex, String value, String input);
        public void cancelled();
    }

    private DialogListener mReadyListener;
    private String mInput;

// constructor
    
    public SpinnerDialog(Context context, ArrayList<String> list, String input, DialogListener readyListener) {
        super(context);
        mReadyListener = readyListener;
        mContext = context;
        mList = new ArrayList<String>();
        mList = list;
        mInput = input;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// setting up the layout
        setContentView(R.layout.spinner_dialog);
        mSpinner = (Spinner) findViewById (R.id.dialog_spinner);
        mEditText = (EditText) findViewById(R.id.editText1);
        mTextView = (TextView) findViewById(R.id.dialog_label);
        mTextView.setText(mInput);
        mTextView.setTextSize(15);
        		
        MyArrayAdapter<String> adapter = new MyArrayAdapter<String> (mContext, R.layout.spinner_item,R.id.tvi, mList);
        mSpinner.setAdapter(adapter);

        Button buttonOK = (Button) findViewById(R.id.dialogOK);
        Button buttonCancel = (Button) findViewById(R.id.dialogCancel);
        
        
        // setting custom font
        mTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/CRIMFBRG.TTF"));
        buttonOK.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/CRIMFBRG.TTF"));
        buttonCancel.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/CRIMFBRG.TTF"));
        mEditText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/CRIMFBRG.TTF"));
        mEditText.requestFocus();
        
        
// setting up buttons
        buttonOK.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View v) {
            	String value = mEditText.getText().toString();
                int n = mSpinner.getSelectedItemPosition();
                mReadyListener.ready(n, value,mInput);
                SpinnerDialog.this.dismiss();
            }
        });
        
        buttonCancel.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View v) {
                mReadyListener.cancelled();
                SpinnerDialog.this.dismiss();
            }
        });
    }
    public class MyArrayAdapter<T> extends ArrayAdapter<T> {
    	
//    	public Context mContext;

    	public MyArrayAdapter(Context context, int resource,
    			int textViewResourceId, List<T> objects) {
    	
    		super(context, resource, textViewResourceId, objects);
    	
    	}
    	
    	@Override  
    	public View getDropDownView(int position, View convertView, ViewGroup parent){
    		View v = super.getView(position, convertView, parent);
       	 ((TextView)v).setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/CRIMFBRG.TTF"));
       	 v.setMinimumHeight(40);
       	 return v;
    	}
    	
    	@Override  
    	public View getView(int position, View convertView, ViewGroup parent)
    	{
    	 View v = super.getView(position, convertView, parent);
    	 ((TextView)v).setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/CRIMFBRG.TTF"));
    	 return v;
    	}
    	
    }
}

