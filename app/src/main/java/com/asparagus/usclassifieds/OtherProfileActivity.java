package com.asparagus.usclassifieds;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OtherProfileActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static User user = GlobalHelper.user;
    private User otherUser;
    private int friendshipStatus;
    private Spinner respondSpinner;
    private String response;
    private ArrayList<String> options = new ArrayList<>();
    private Button friendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalHelper.updateUserList();
        setContentView(R.layout.activity_other_profile);
        Intent intent = getIntent();
        otherUser = (User) intent.getSerializableExtra("other_user");

        friendButton = findViewById(R.id.friend_button);

        response = "accept";
        respondSpinner = findViewById(R.id.respond_spinner);
        respondSpinner.setBackgroundColor(Color.WHITE);
        options.add("accept");
        options.add("reject");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        respondSpinner.setAdapter(adapter);
        respondSpinner.setOnItemSelectedListener(this);

        respondSpinner.setVisibility(View.GONE);


        friendshipStatus = 0;   //no friendship between users

        if(user.getIncomingFriendRequests().containsKey(otherUser.userID)) {
            friendshipStatus = 1;   //user has incoming friend request from other
            respondSpinner.setVisibility(View.VISIBLE);
            friendButton.setEnabled(false);
            friendButton.setText("Respond");
        } else if(user.getOutgoingFriendRequests().containsKey(otherUser.userID)) {
            friendshipStatus = 2;   //user has outgoing friend request to other
            friendButton.setText("Cancel Request");
        } else if(user.getFriends().containsKey(otherUser.userID)) {
            friendshipStatus = 3;   //user and other are friends
            friendButton.setText("Remove Friend");
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        response = parent.getItemAtPosition(pos).toString();
        friendButton.setEnabled(true);
    }

    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();

        TextView textView;

        /*
        Normally, the GlobalHelper's getUser() method
        retrieves the logged-in person's information. We
        need to have the GlobalHelper refer to the person
        whose page is the one currently being viewed
         */

        textView = findViewById(R.id.other_first_name);
        textView.setText(otherUser.getFirstName());

        textView = findViewById(R.id.other_last_name);
        textView.setText(otherUser.getLastName());

        textView = findViewById(R.id.other_phone_number);
        textView.setText(otherUser.getPhone());

        textView = findViewById(R.id.other_street_number);
        textView.setText(otherUser.getStreetNumber());

        textView = findViewById(R.id.other_street_name);
        textView.setText(otherUser.getStreetName());

        textView = findViewById(R.id.other_city_name);
        textView.setText(otherUser.getCity());

        textView = findViewById(R.id.other_state_code);
        textView.setText(otherUser.getState());

        textView = findViewById(R.id.other_zip_code);
        textView.setText(otherUser.getZipCode());

        textView = findViewById(R.id.other_description);
        textView.setText(otherUser.getDescription());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_button:
                if(friendshipStatus == 0) {
                    final Map<String, String> reqType = new HashMap<String, String>() {{
                        put(otherUser.userID, "request");
                    }};
                    HashMap<String, Object> result = new HashMap<String, Object>() {{
                        put(user.userID, reqType);
                    }};
                    // If not friends
                    if (Objects.equals(user.friends.get(otherUser.userID), null)) {
                        FirebaseDatabase.getInstance().getReference("friendrequests").child(user.userID).setValue(reqType);
                    }

                    user.getOutgoingFriendRequests().put(otherUser.userID, "true");
                    friendButton.setText("Cancel Request");
                    friendshipStatus = 2;
                } else if (friendshipStatus == 1) {
                    if(response.equals("accept")) {
                        //add other as a friend to user
                        final Map<String, String> reqType = new HashMap<String, String>() {{
                            put(otherUser.userID, "accept");
                        }};
                        FirebaseDatabase.getInstance().getReference("friendrequests").child(user.userID).setValue(reqType);
                        user.getFriends().put(otherUser.userID, "true");
                        user.getIncomingFriendRequests().remove(otherUser.userID);
                        GlobalHelper.setUser(user);
                        friendshipStatus = 3;
                        friendButton.setText("Remove Friend");

                    } else {
                        //remove friend request from other
                        final Map<String, String> reqType = new HashMap<String, String>() {{
                            put(otherUser.userID, "reject");
                        }};
                        FirebaseDatabase.getInstance().getReference("friendrequests").child(user.userID).setValue(reqType);
                        user.getIncomingFriendRequests().remove(otherUser.userID);
                        GlobalHelper.setUser(user);
                        friendshipStatus = 0;
                        friendButton.setText("Add Friend");
                    }
                    respondSpinner.setVisibility(View.GONE);
                } else if (friendshipStatus == 2) {
                    final Map<String, String> reqType = new HashMap<String, String>() {{
                        put(otherUser.userID, "reject");
                    }};
                    FirebaseDatabase.getInstance().getReference("friendrequests").child(user.userID).setValue(reqType);
                    user.getOutgoingFriendRequests().remove(otherUser.userID);
                    GlobalHelper.setUser(user);
                    friendshipStatus = 0;
                    friendButton.setText("Add Friend");
                } else if (friendshipStatus == 3) {
                    FirebaseDatabase.getInstance().getReference("users").child(user.userID).child("friends").child(otherUser.userID).removeValue();
                    FirebaseDatabase.getInstance().getReference("users").child(otherUser.userID).child("friends").child(user.userID).removeValue();
                    user.getFriends().remove(otherUser.userID);
                    GlobalHelper.setUser(user);
                    friendshipStatus = 0;
                    friendButton.setText("Add Friend");
                }
                break;
            /* User clicks the view listings button for the other otherUser */
            case R.id.other_listings_button:
                Intent i = new Intent();
                setResult(12345);
                finish();
                break;
        }
    }
}
