package pt.privateChat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import android.widget.LinearLayout.LayoutParams;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class ChatActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final int VIBRATION_LENGHT = 50;
    protected static final int INTENT_GALLERY_PIC_REQ = 1;
    protected static final int INTENT_TAKE_PHOTO_REQ = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Maike");     
        actionBar.setSubtitle("last seen today at 16:10");
        
        newMessage("moiii",null,false);
        newMessage("hello",null,true);
    }

    /**
     * Backward-compatible version of {@link ActionBar#getThemedContext()} that
     * simply returns the {@link android.app.Activity} if
     * <code>getThemedContext</code> is unavailable.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Context getActionBarThemedContextCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getActionBar().getThemedContext();
        } else {
            return this;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        Fragment fragment = new DummySectionFragment();
        Bundle args = new Bundle();
        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return true;
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	//Check which item
    	switch (item.getItemId()) {
    	case R.id.action_attachment:
    		openAttach();
    		return true;	
    	case R.id.action_key:
    		setKey();
    		return true;
		default:
			return super.onOptionsItemSelected(item);
		}    	
    }
    
    
    public void sendMessage(View view){
    	EditText textBox = (EditText) findViewById(R.id.messageBox);
    	String text = textBox.getText().toString();
    	if(text == ""){
    		return;
    	}
    	textBox.setText("");
    	
    	newMessage(text,null,true);
    }
	private void setKey() {
		// TODO Auto-generated method stub
		 
	}                                                

	private void openAttach() {
		View view = findViewById(R.id.action_attachment);
		PopupMenu popup = new PopupMenu(this, view);
		getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
		popup.show();
	}
	
	
	public void newMessage(String message,Uri image, boolean mine){
		LinearLayout board = (LinearLayout) findViewById(R.id.message_board);
		LinearLayout  messageView = (LinearLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.message,
				board,
                  false);
		//Set side
		int side = (mine) ?  Gravity.RIGHT : Gravity.LEFT;
		messageView.setGravity(side);
		
		
		//Handle Image
		ImageView imageView = (ImageView) messageView.findViewById(R.id.messageImage);
		if(image == null){
			messageView.removeView(imageView);
		}else{
			imageView.setImageURI(image);
		}
		
		
		//Set Text content
		TextView text = (TextView) messageView.findViewById(R.id.messageText);
		if(message == null){
			messageView.removeView(text);
		}else{
			text.setText(message);
		}
		
		
		//Set last seen
		TextView seenText = (TextView) messageView.findViewById(R.id.messageSeen);
		if(mine){
			messageView.removeView(seenText);
		}else{
			Calendar date = Calendar.getInstance();
			StringBuilder sb = new StringBuilder();
			sb.append("Seen ");
			sb.append(date.get(Calendar.HOUR_OF_DAY));
			sb.append(":");
			sb.append(date.get(Calendar.MINUTE));
			seenText.setText(sb.toString());
		}
		
		
		//Add to board
		board.addView(messageView);
		
		ScrollView scroll = (ScrollView) findViewById(R.id.messages_scroll);
		scroll.fullScroll(View.FOCUS_DOWN);
		
		//Notify user
		if(!mine)
			vibrate(VIBRATION_LENGHT);
	}
	
	 public void vibrate(int ms) {
	        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	        if (ms != -1) {
	            mVibrator.vibrate(ms);
	        } else {
	            long[] pattern = { 0, 100, 50, 100, 50, 100 };
	            mVibrator.vibrate(pattern, -1);
	        }
	}
	 
	 
	 
	 /*
	  * Get Photos
	  */
	 public boolean attach_from_gallery(MenuItem item){
		 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		 intent.setType("image/*");
         startActivityForResult(Intent.createChooser(intent,
                 "Select Picture"), INTENT_GALLERY_PIC_REQ);
         startActivityForResult(intent, INTENT_GALLERY_PIC_REQ);
		return false;

	 }
	 
	 public boolean attach_photo(MenuItem item){
		return false;
//         Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//         startActivityForResult(takePictureIntent, INTENT_TAKE_PHOTO_REQ);
	 }
	 
	 public boolean attach_video(MenuItem item){
		return false;
		 //
	 }
	 
	 
	 public void onActivityResult(int requestCode,int resultCode,Intent data){
	      if (resultCode != RESULT_OK) {
	    	  return;
	      }
	      switch (requestCode) {
		case INTENT_GALLERY_PIC_REQ:
			Uri selectedImageUri = data.getData();
			InputStream iStream;
			newMessage(null, selectedImageUri, true);
			sendImage(selectedImageUri);
		//TODO: Other methods to send
		default:
			break;
		}
	 }

	private void sendImage(Uri selectedImageUri) {
		// TODO Auto-generated method stub
		
	}

}
