package com.codingblocks.suraksha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codingblocks.suraksha.Models.PoliceDetail;

import java.util.ArrayList;

public class PoliceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);

        ArrayList<PoliceDetail> policeDetails = new ArrayList<>();
        policeDetails.add(new PoliceDetail("Police Station (Nearest)", "100"));
        policeDetails.add(new PoliceDetail("Police Helpline (Head Quarters, Thiruvananthapuram)", "0471-324 3000/4000/5000"));
        policeDetails.add(new PoliceDetail("Police Message Centre", "94 97 900000"));
        policeDetails.add(new PoliceDetail("Police High Way Help Line", "9846 100 100"));
        policeDetails.add(new PoliceDetail("Nirbhaya Toll Free Number", "1800 425 1400"));
        policeDetails.add(new PoliceDetail("NORKA ROOTS Call Centre", "1800 425 3939"));
        policeDetails.add(new PoliceDetail("SP Women Cell", "94979 96992"));


        Police adapter = new Police(policeDetails, getApplicationContext());
        RecyclerView recView = findViewById(R.id.recViewPolice);

        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);



    }
}
