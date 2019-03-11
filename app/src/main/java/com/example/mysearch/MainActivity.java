package com.example.mysearch;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {
    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this) ;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

         mUserDatabase = FirebaseDatabase.getInstance().getReference(KeyString.TranslatorC) ;


        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });
        firebaseUserSearch("");
    }

    private void firebaseUserSearch(String searchText) {

        Toast.makeText(MainActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Translator, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Translator, UsersViewHolder>(
                Translator.class,
                R.layout.list_layout,
                UsersViewHolder.class,
                firebaseSearchQuery

        ) {

            protected void populateViewHolder(UsersViewHolder viewHolder, Translator model, int position) {


                viewHolder.setDetails(getApplicationContext(), model);

            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);

    }


    // View Holder Class

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(final Context ctx, final Translator item){

            TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView user_status = (TextView) mView.findViewById(R.id.status_text);
            ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i =new Intent(ctx,Translator.class);
                    // send the data in intent
                    i.putExtra("data", item);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }
            });

            user_name.setText(item.getName());
            user_status.setText(item.getBio());
            Glide.with(ctx).load(item).into(user_image);


        }


    }
}
