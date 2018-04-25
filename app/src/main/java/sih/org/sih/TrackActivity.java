package sih.org.sih;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sih.org.sih.Adapters.ProgressAdapter;
import sih.org.sih.Modals.Stage;

public class TrackActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textError;
    List<Stage> list;
    String genTicket;
    ProgressAdapter adapter;
    DatabaseReference ref;
    Spinner spinner;
    CircleImageView profile_image;
    TextView profile_name;
    int numBags;
    ImageView logout_image;
    TextView srcLocation,destLocation,num_bags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        logout_image = findViewById(R.id.logout_image);

        srcLocation = findViewById(R.id.srcLocation);
        destLocation = findViewById(R.id.destLocation);
        num_bags = findViewById(R.id.bag_count);

        profile_image = findViewById(R.id.profile_image) ;
        profile_name = findViewById(R.id.profile_name);

        textError = findViewById(R.id.textError);

        String email = getIntent().getStringExtra("email");
        Log.d("EMAIL",email);

        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Uri imageUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

        Glide.with(this).load(imageUri).into(profile_image);
        profile_name.setText(name);

        spinner = findViewById(R.id.spinner);

        ref = FirebaseDatabase.getInstance().getReference().child("EmailMap");

        logout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TrackActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout from this account?")
                        .setIcon(R.drawable.googleg_color)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(TrackActivity.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("NO",null).show();
            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int slack = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("email").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        genTicket = snapshot.getKey().toString();
                        slack = 1;
                        break;
                    }
                }
                if(slack == 1) {
                    FirebaseDatabase.getInstance().getReference().child("sih").child(genTicket).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
                    myMeth2();
                }
                else {
                    textError.setText("Email not registered with System");
                    textError.setVisibility(View.VISIBLE);
                    findViewById(R.id.recycler_progress).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void myMeth2() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        Log.d("Generated: ", " "+genTicket);

        ref = FirebaseDatabase.getInstance().getReference().child("sih").child(genTicket);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numBags = dataSnapshot.child("numberOfBags").getValue(Integer.class);
                num_bags.setText(String.valueOf(numBags));
                srcLocation .setText(dataSnapshot.child("src").getValue(String.class));
                destLocation.setText(dataSnapshot.child("dest").getValue(String.class));

                arrayAdapter.clear();
                for(int m = 0 ;m <numBags ; m++) {
                    arrayAdapter.add("Bag "+(m+1));
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myMethod();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                myMethod();
            }
        });
    }

    void myMethod() {

        list = new ArrayList<>();

        adapter = new ProgressAdapter(TrackActivity.this,list);

        recyclerView = findViewById(R.id.recycler_progress);
        recyclerView.setLayoutManager(new LinearLayoutManager(TrackActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference().child("sih").child(genTicket).child(String.valueOf(spinner.getSelectedItemPosition())).child("Status");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Stage stage = dataSnapshot.getValue(Stage.class);
                if (dataSnapshot.getKey().toString().equals("1")) {
                    list.add(0,stage);
                }
                else if(dataSnapshot.getKey().toString().equals("2")) {
                    list.add(1,stage);
                }
                else if (dataSnapshot.getKey().toString().equals("3")) {
                    list.add(2,stage);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Stage stage = dataSnapshot.getValue(Stage.class);
                if (dataSnapshot.getKey().toString().equals("1")) {
                    list.remove(0);
                    list.add(0,stage);
                }
                else if(dataSnapshot.getKey().toString().equals("2")) {
                    list.remove(1);
                    list.add(1,stage);
                }
                else if (dataSnapshot.getKey().toString().equals("3")) {
                    list.remove(2);
                    list.add(2,stage);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}