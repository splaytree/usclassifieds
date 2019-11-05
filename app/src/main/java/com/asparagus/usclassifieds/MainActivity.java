package com.asparagus.usclassifieds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final int RC_START = 2;
    private static final int RC_STOP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("onCreate() MAIN ");
//
//        GlobalHelper.insert();
//        System.out.println("Inserted successefully");
//        Point tempPoint = new Point(new Position(34,-118));
//        GlobalHelper.addNewUser("jltanner@usc.edu","John","Tanner","9498128890",tempPoint,"12345678");
       // System.out.println("users/123456789: " + GlobalHelper.userExists("123456789"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("onStart() MAIN ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume() MAIN ");

        if(GlobalHelper.getEmail().equals("")) {

            System.out.println("start sign in intent");
            Intent signInIntent = new Intent(this, SignIn.class);
            startActivityForResult(signInIntent, RC_START);

        }
        else if(GlobalHelper.getUser() == null) {

            System.out.println("In second if statement: " + GlobalHelper.getUser());
            //TODO --> check if user is in DB, if not go to edit_profile activity and update DB, o.w. go to homepage
        }
        else {
            System.out.println("Start home page intent: " + GlobalHelper.getUser());
            Intent homePageIntent = new Intent(this, Home.class);
            startActivityForResult(homePageIntent, RC_STOP);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause() MAIN ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop() MAIN ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("onActivityResult() MAIN ");

        if(resultCode == Activity.RESULT_OK) {

            //TODO --> check if user is in Firebase, if not go to edit_profile activity and update DB, o.w. go to homepage
            //TODO --> use GlobalHelper.setUser( *** ) here if user is found

        } else if(resultCode == Activity.RESULT_CANCELED) {
            //TODO --> Sign out and redirect back to sign in activity
            GlobalHelper.setEmail("");
            GlobalHelper.setID("");
            GlobalHelper.signOut();
        }
    }
}
