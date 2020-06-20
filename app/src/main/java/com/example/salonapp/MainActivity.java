package com.example.salonapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.flags.Flag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView tvUserName;
    CheckBox cbMassage, cbHairCut, cbMustache_beared, cbFacial,cbWaxing;
    Button btnSubmit,  btnChsDate;
    EditText etBookingDate;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase Booking;
    long maxid=0;
    DatabaseReference bookingref;
    FloatingActionButton fabDial;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private static final String CHANNEL_ID = "Salon Booking";
    public static final String CHANNEL_NAME = "Salon Booking";
    public static final String CHANNEL_DESC = " Salon Booking";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new   NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        tvUserName = findViewById(R.id.tvUserName);
        cbMassage = findViewById(R.id.cbMassage);
        cbHairCut = findViewById(R.id.cbHairCut);
        cbMustache_beared = findViewById(R.id.cbMustache_Beared);
        cbFacial = findViewById(R.id.cbFacial);
        cbWaxing = findViewById(R.id.cbWaxing);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnChsDate = findViewById(R.id.btnChsDate);
        fabDial = findViewById(R.id.fabDial);
        firebaseAuth = FirebaseAuth.getInstance();
        etBookingDate = findViewById(R.id.etBookingdate);
        Booking = FirebaseDatabase.getInstance();
        bookingref = Booking.getReference("BookingData");


        bookingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    maxid = (dataSnapshot.getChildrenCount());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        final Date today = new Date();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        final Date Time = Calendar.getInstance().getTime();








        String email = firebaseAuth.getCurrentUser().getEmail();
        tvUserName.setText("Welcome " + email);

        btnChsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        fabDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Intent.ACTION_DIAL);
                a.setData(Uri.parse("tel:" +"9999999999"));
                startActivity(a);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                final Date today1 = new Date();
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/dd/MM");

                        String date = etBookingDate.getText().toString();
                        if (date.length() == 0) {
                            etBookingDate.setError("Enter date");
                            etBookingDate.requestFocus();
                            return;
                        }

                        if(date.equals(today1.toString()) ){

                            Toast.makeText(MainActivity.this, "Please select Today's Date", Toast.LENGTH_SHORT).show();
                            etBookingDate.requestFocus();
                            return;

                        }

                        String services = "";
                        if (cbHairCut.isChecked())
                            services = services + "HairCut";
                        if (cbMustache_beared.isChecked())
                            services = services + " Mustache and beared";
                        if (cbMassage.isChecked())
                            services = services + " massage";
                        if (cbFacial.isChecked())
                            services = services + " Facial";
                        if (cbWaxing.isChecked())
                            services = services + " Waxing";

                        if (services.length() == 0) {
                            Toast.makeText(MainActivity.this, "Atleast select one Service", Toast.LENGTH_SHORT).show();
                            cbHairCut.requestFocus();
                            return;
                        }


                        String service = "" + services;


                        String email = firebaseAuth.getCurrentUser().getEmail();
                        String time = Time.toString();

                        String name = getIntent().getStringExtra("Name");
                        BookingData bookingData = new BookingData(
                                email,name, service,time);
                        Booking.getReference("BookingData")
                                .child(simpleDateFormat.format(today))
                                .child(String.valueOf(maxid+1))
                                .setValue(bookingData);
                         Toast.makeText(MainActivity.this,
                        "Booking Successful!", Toast.LENGTH_SHORT).show();
                        displayNotification();





                        etBookingDate.setText("");
                        cbMustache_beared.setChecked(false);
                        cbMassage.setChecked(false);
                        cbFacial.setChecked(false);
                        cbWaxing.setChecked(false);
                        cbHairCut.setChecked(false);

                    }


        });








}

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)


        );
        datePickerDialog.show();


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month= month + 1;

        String date = "" +year + "/" + dayOfMonth  + "/" + month ;
        etBookingDate.setText(date);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.aboutus == item.getItemId())
        {
            Intent intent = new Intent(MainActivity.this,AboutUsActivity.class);
            startActivity(intent);
        }

        if (R.id.Logout == item.getItemId())
        {
            firebaseAuth.signOut();
            Intent b = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(b);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit");
        alertDialog.setMessage("Do you want to exit");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(1);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog a = alertDialog.create();
        a.show();
    }// end of onBackpressedNo

    private void displayNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_black)
                .setContentTitle("Salon Booking App")
                .setContentText("Booking Successful")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,builder.build());
    }



}

