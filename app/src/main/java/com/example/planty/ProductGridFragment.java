package com.example.planty;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class ProductGridFragment extends Fragment {

    FirebaseDatabase db;

    ImageView tempImgButton, humidImgButton, settingsImgButton, mainImg;
    TextView tempText, humidText, mainText;
    View tempCard, humidCard, settingsCard;
    Button setTempButton, cancelTempButton, setHumidButton, cancelHumidButton, setRefreshButton, cancelRefreshButton;
    TextInputLayout setMinTempL, setMaxTempL, setMinHumidL, setMaxHumidL, setRefreshL;
    TextInputEditText setMinTempT, setMaxTempT, setMinHumidT, setMaxHumidT, setRefreshT;
    int normalMinTemp, normalMaxTemp, normalMinHumid, normalMaxHumid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ms_product_grid_fragment, container, false);
        setUpToolbar(view);

        db = FirebaseDatabase.getInstance();
        readRefresh();

        tempImgButton = view.findViewById(R.id.ic_temp);
        humidImgButton = view.findViewById(R.id.ic_humid);
        settingsImgButton = view.findViewById(R.id.settings);
        mainImg = view.findViewById(R.id.plant_img);

        setHumidButton = view.findViewById(R.id.btn_set_humid);
        cancelHumidButton = view.findViewById(R.id.btn_cancel_humid);
        setTempButton = view.findViewById(R.id.btn_set_temp);
        cancelTempButton = view.findViewById(R.id.btn_cancel_temp);
        setRefreshButton = view.findViewById(R.id.btn_set_refresh);
        cancelRefreshButton = view.findViewById(R.id.btn_cancel_refresh);

        tempText = view.findViewById(R.id.ic_temp_text);
        humidText = view.findViewById(R.id.ic_humid_text);
        mainText = view.findViewById(R.id.main_text);

        setMinTempT = view.findViewById(R.id.temp_min_edit_text);
        setMaxTempT = view.findViewById(R.id.temp_max_edit_text);
        setMinHumidT = view.findViewById(R.id.humid_min_edit_text);
        setMaxHumidT = view.findViewById(R.id.humid_max_edit_text);
        setRefreshT = view.findViewById(R.id.set_refresh_edit_text);

        setMinTempL = view.findViewById(R.id.temp_min_text_input);
        setMaxTempL = view.findViewById(R.id.temp_max_text_input);
        setMinHumidL = view.findViewById(R.id.humid_min_text_input);
        setMaxHumidL = view.findViewById(R.id.humid_max_text_input);
        setRefreshL = view.findViewById(R.id.set_refresh_text_input);

        readSensorData();
        readNormalTemp();
        readNormalHumid();

        tempCard = view.findViewById(R.id.temp_card);
        humidCard = view.findViewById(R.id.humid_card);
        settingsCard = view.findViewById(R.id.refresh_card);

        tempCard.setVisibility(View.INVISIBLE);
        humidCard.setVisibility(View.INVISIBLE);
        settingsCard.setVisibility(View.INVISIBLE);

        tempImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCard.setVisibility(View.VISIBLE);
                readNormalTemp();
                humidCard.setVisibility(View.INVISIBLE);
                settingsCard.setVisibility(View.INVISIBLE);
            }
        });

        humidImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humidCard.setVisibility(View.VISIBLE);
                readNormalHumid();
                tempCard.setVisibility(View.INVISIBLE);
                settingsCard.setVisibility(View.INVISIBLE);
            }
        });

        cancelTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCard.setVisibility(View.INVISIBLE);
            }
        });

        cancelHumidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humidCard.setVisibility(View.INVISIBLE);
            }
        });

        setTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNormalTemp(setMinTempT.getText().toString(), setMaxTempT.getText().toString());
                tempCard.setVisibility(View.INVISIBLE);
                settingsCard.setVisibility(View.INVISIBLE);
            }
        });

        setHumidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNormalHumid(setMinHumidT.getText().toString(), setMaxHumidT.getText().toString());
                humidCard.setVisibility(View.INVISIBLE);
                settingsCard.setVisibility(View.INVISIBLE);
            }
        });

        settingsImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsCard.setVisibility(View.VISIBLE);
                humidCard.setVisibility(View.INVISIBLE);
                tempCard.setVisibility(View.INVISIBLE);
            }
        });

        setRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeRefresh(Integer.valueOf(Objects.requireNonNull(setRefreshT.getText()).toString()));
                settingsCard.setVisibility(View.INVISIBLE);
            }
        });

        cancelRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsCard.setVisibility(View.INVISIBLE);
            }
        });

        view.findViewById(R.id.product_grid).setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.shr_product_grid_background_shape));

        return view;
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void readRefresh() {
        DatabaseReference myRef = db.getReference("DHT11Refresh");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(Integer.class);
                setRefreshT.setText(String.valueOf(value));
                Log.d("Data", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Data", "Failed to read value.", error.toException());
            }
        });
    }

    private void writeRefresh(int refresh) {
        DatabaseReference myRef = db.getReference("DHT11Refresh");
        myRef.setValue(refresh);
    }

    private void readSensorData() {
        final double[] tempAndHumid = new double[2];
        DatabaseReference myRef = db.getReference("DHT11");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempAndHumid[0] = Double.parseDouble(dataSnapshot.child("Temperature").getValue(String.class));
                tempAndHumid[1] = Double.parseDouble(dataSnapshot.child("Humidity").getValue(String.class));
                tempText.setText(dataSnapshot.child("Temperature").getValue(String.class) + "\u2103");
                humidText.setText(dataSnapshot.child("Humidity").getValue(String.class) + "%");

                Log.v("Data", normalMaxTemp + " & " + normalMinTemp);
                Log.v("Data", tempAndHumid[0] + " & " + tempAndHumid[1]);

                if (tempAndHumid[0] < normalMinTemp) {
                    mainText.setText("Too cold, help!");
                    mainImg.setImageResource(R.drawable.ic_cold);
                }
                if (tempAndHumid[0] > normalMaxTemp) {
                    mainText.setText("Oh no, it's too hot");
                    mainImg.setImageResource(R.drawable.ic_hot);
                }
                if (tempAndHumid[0] >= normalMinTemp && tempAndHumid[0] <= normalMaxTemp) {
                    mainText.setText("Your plant is okay!");
                    mainImg.setImageResource(R.drawable.ic_normal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void writeNormalTemp(String minTemp, String maxTemp) {
        DatabaseReference myRef = db.getReference("NormalTemp");
        myRef.child("min").setValue(minTemp);
        myRef.child("max").setValue(maxTemp);
    }

    private void writeNormalHumid(String minHumid, String maxHumid) {
        DatabaseReference myRef = db.getReference("NormalHumid");
        myRef.child("min").setValue(minHumid);
        myRef.child("max").setValue(maxHumid);
    }

    private void readNormalTemp() {
        DatabaseReference myRef = db.getReference("NormalTemp");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                normalMinTemp = Integer.parseInt(dataSnapshot.child("min").getValue(String.class));
                normalMaxTemp = Integer.parseInt(dataSnapshot.child("max").getValue(String.class));
                setMinTempT.setText(dataSnapshot.child("min").getValue(String.class));
                setMaxTempT.setText(dataSnapshot.child("max").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readNormalHumid() { ;
        DatabaseReference myRef = db.getReference("NormalHumid");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                normalMinHumid = Integer.parseInt(dataSnapshot.child("min").getValue(String.class));
                normalMaxHumid = Integer.parseInt(dataSnapshot.child("max").getValue(String.class));
                setMinHumidT.setText(dataSnapshot.child("min").getValue(String.class));
                setMaxHumidT.setText(dataSnapshot.child("max").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
