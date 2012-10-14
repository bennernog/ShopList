package be.intec.shoplist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Environment;
import android.util.Log;

public class MyMethods extends Activity{
	
// method to read the group names out of the raw folder into an ArrayList
	public  static ArrayList<String> getGroupNames(Activity activity){
	
		ArrayList<String> groupNames = new ArrayList<String>();{
			
			
			try{
				InputStream input = activity.getResources().openRawResource(be.intec.shoplist.R.raw.aaaaagroup);
				InputStream file= input;
				BufferedReader reader=new BufferedReader(new InputStreamReader(file),150);
				String groupString;
				while ((groupString=reader.readLine())!=null) {
					groupNames.add(groupString);
				}
				removeDuplicates(groupNames);
				Collections.sort(groupNames);
			}catch(IOException e){e.printStackTrace();}}
    	
    	return groupNames;
    }
	
// method to read a file into an ArrayList
	public static ArrayList<String> makeListFromFile(String fileName){
		ArrayList<String> list=new ArrayList<String>();
		File file=new File(Environment.getExternalStorageDirectory(),"ShopList/"+fileName);
		try{
		    	FileReader fileReader=new FileReader(file);
		        BufferedReader reader=new BufferedReader(fileReader,150);
		        String temp;
				while ((temp=reader.readLine())!=null) {
					if(temp.length()>1){
						String string = temp;
						list.add(string);
			    	}
				}
				reader.close();
				fileReader.close();
				removeDuplicates(list);
		}catch(IOException e){e.printStackTrace();
		}
    	return list;
		
	}
	
// makeListFromFile() specified to files in the Items/ folder
	public static ArrayList<String> getItemList(String fileName, ArrayList<String> list) {
		list = makeListFromFile("Items/" + fileName);
		Collections.sort(list);
		return list;
	}
	
// makeListFromFile() specified to files in the Lists/ folder
	public static ArrayList<String> getList(String fileName) {
		ArrayList<String> list= makeListFromFile("Lists/"+fileName);
		
		return list;
	}
// makeListFromFile() specified to files in the Lists/ folder
	public static Set<String> getSet(String fileName) {
		ArrayList<String> list= new ArrayList<String>();
		list = makeListFromFile("Lists/"+fileName);
		
		return getSetFromArray(list);
	}
private static Set<String> getSetFromArray(ArrayList<String> list){
	Set<String> set = new HashSet<String>();
	
	for(String s:list){
		
		set.add(s);
		
	}//end for:
	
	return set;
}//end getSetFromArray
	 
private static ArrayList<String> getArrayFromSet(Set<String> set){
	ArrayList<String> list= new ArrayList<String>();
	
	for(String s:set){
		
		list.add(s);
		
	}//end for:
	
	return list;
}//end getArrayFromSet

//method to write an ArrayList to a file
	public static void writeToFile(ArrayList<String> list, String fileName, boolean appendable){
		File file =new File(Environment.getExternalStorageDirectory(),"ShopList/"+fileName);
				 try {
				FileWriter writer=new FileWriter(file, appendable);
				 for(int i=0;i<list.size();i++){
					 writer.write(list.get(i));
					 writer.write(System.getProperty("line.separator"));
				 }
//				 Log.d("WRITE_TO_FILE", fileName+" - size: "+list.size()+" - "+appendable);
				 writer.close();
				 } catch (IOException e) {
				 e.printStackTrace();
				 }
	}
//Method to delete file
	public static void deleteList(String fileName){
		File file =new File(Environment.getExternalStorageDirectory(),"ShopList/Lists/"+fileName);
		file.delete();
	}
// writeToFile() specified to overwrite files in the Lists/ folder
	public static void writeListToFile(ArrayList<String> list, String fileName){
		removeDuplicates(list);
		writeToFile(list,"Lists/"+fileName,false);
	}
	
// writeToFile() specified to add strings to files in the Items/ folder
	public static void writeItemToItems(String item, String fileName){
		ArrayList<String> list=new ArrayList<String>();
		list.add(item);
		
		writeToFile(list,"Items/"+fileName,true);
	}

// writeToFile() specified to add strings to files in the Lists/ folder
	public static void writeItemToList(String item, String fileName){
		ArrayList<String> list=new ArrayList<String>();
		list.add(item);
		Log.d("WRITE_ITEM", fileName+"/"+list.size()+"/"+item);
		
		writeToFile(list,"Lists/"+fileName,true);
	}
	
	
	
//methode to get a list of users's lists
	public static ArrayList<String> getMyLists(){
		ArrayList<String> list = new ArrayList<String>(); 
		File file=new File(Environment.getExternalStorageDirectory(),"ShopList/Lists");
		String[] myLists=file.list();
		for(int i=0;i<myLists.length;i++) {
				
				list.add(myLists[i]);
				
		}
		
		list.remove("currentList");
		list.remove("scratchFile");
		Collections.sort(list);
		
		return list;
	}
	
	
	public static void saveNewList(String fileName, ArrayList<String> list) {
		File file = new File(Environment.getExternalStorageDirectory(),"ShopList/Lists/"+fileName);
		writeListToFile(list, fileName);
	}



	 public static void prepNewList(){
		 ArrayList<String> temp = new ArrayList<String>();
		 temp.add(System.getProperty("line.separator"));
		 writeListToFile(temp, "currentList");
		 writeListToFile(temp, "scratchFile");
	 }
	
// Method to remove duplicates from an ArrayList
		public static void removeDuplicates(ArrayList<?> arlList) {
			Set set = new HashSet();
			List newList = new ArrayList();
			for (Iterator iter = arlList.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (set.add(element)) {
					newList.add(element);
				}
			}
			arlList.clear();
			arlList.addAll(newList);
		}
}
