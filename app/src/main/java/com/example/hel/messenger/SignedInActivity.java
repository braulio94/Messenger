package com.example.hel.messenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignedInActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    EditText editTextMessage;
    String username;
    String userPhoto;
    RecyclerView recyclerViewMessageList;
    FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        editTextMessage = findViewById(R.id.edit_message);
        username = auth.getCurrentUser().getDisplayName();


        recyclerViewMessageList = findViewById(R.id.messages_list);
        llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.getStackFromEnd();
        recyclerViewMessageList.setLayoutManager(llm);


        initLoadData();

        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Message message = new Message(editTextMessage.getText().toString(),username,userPhoto,null);
               databaseReference.child("messages").push().setValue(message);
               editTextMessage.setText("");

            }
        });
    }

    public  void initLoadData(){
        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(databaseReference.child("messages"), Message.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {

                holder.messageText.setText(model.getText());


            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }
        };

        //codigo para mostrar o ultimo item da lista quando for insirido

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = firebaseRecyclerAdapter.getItemCount();
                int lastMessage = llm.findLastCompletelyVisibleItemPosition();

                if (lastMessage == -1 || (positionStart >= (messageCount - 1) && lastMessage == (positionStart - 1)) ){
                    recyclerViewMessageList.scrollToPosition(positionStart);
                }

            }
        });

        recyclerViewMessageList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
