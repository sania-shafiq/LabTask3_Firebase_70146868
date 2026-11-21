package com.uol.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/** Scenario 2 - the live board. */
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private View layoutEmpty;
    private ExtendedFloatingActionButton fabPost;

    private final ArrayList<LostItem> itemList = new ArrayList<>();
    private LostItemAdapter adapter;

    private DatabaseReference ref;
    private ValueEventListener valueListener;   // kept in a field so it can be removed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvItems = findViewById(R.id.rvItems);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        fabPost = findViewById(R.id.fabPost);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LostItemAdapter(this, itemList);
        rvItems.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("LostFound");

        // "Post an Item" button opens PostItemActivity
        fabPost.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PostItemActivity.class)));

        // Tap a card -> write "Returned" to that item's status only
        adapter.setOnItemClickListener((item, position) -> {
            if (item.getId() == null) {
                return;
            }
            if ("Returned".equalsIgnoreCase(item.getStatus())) {
                Toast.makeText(MainActivity.this, "Already marked as Returned", Toast.LENGTH_SHORT).show();
                return;
            }
            ref.child(item.getId()).child("status").setValue("Returned")
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        });

        // Live read
        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    LostItem item = child.getValue(LostItem.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                // push() keys are time-ordered (oldest first) -> reverse for newest first
                Collections.reverse(itemList);
                adapter.notifyDataSetChanged();

                // Empty-board message
                boolean empty = !snapshot.hasChildren();
                layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                rvItems.setVisibility(empty ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        ref.addValueEventListener(valueListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ref != null && valueListener != null) {
            ref.removeEventListener(valueListener);
        }
    }
}
