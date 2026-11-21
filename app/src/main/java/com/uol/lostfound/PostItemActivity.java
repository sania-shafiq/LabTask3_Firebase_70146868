package com.uol.lostfound;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/** Scenario 1 - Post an Item screen. */
public class PostItemActivity extends AppCompatActivity {

    private EditText etItemName, etLocation, etContact;
    private RadioGroup rgType;
    private Button btnPost;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Post an Item");
            bar.setDisplayHomeAsUpEnabled(true);   // back arrow
        }

        etItemName = findViewById(R.id.etItemName);
        etLocation = findViewById(R.id.etLocation);
        etContact = findViewById(R.id.etContact);
        rgType = findViewById(R.id.rgType);
        btnPost = findViewById(R.id.btnPost);

        // Reference to the LostFound node
        ref = FirebaseDatabase.getInstance().getReference("LostFound");

        btnPost.setOnClickListener(v -> postItem());
    }

    private void postItem() {
        String name = etItemName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        // Refuse empty fields with setError()
        if (name.isEmpty()) {
            etItemName.setError("Item name is required");
            etItemName.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }
        if (contact.isEmpty()) {
            etContact.setError("Contact is required");
            etContact.requestFocus();
            return;
        }

        String type = (rgType.getCheckedRadioButtonId() == R.id.rbFound) ? "Found" : "Lost";

        // push() creates a unique, time-ordered key
        String key = ref.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Could not create a key", Toast.LENGTH_SHORT).show();
            return;
        }

        LostItem item = new LostItem(key, name, type, location, contact, "Open");

        btnPost.setEnabled(false);   // avoid double posting while waiting

        ref.child(key).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PostItemActivity.this, "Item posted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                btnPost.setEnabled(true);
                String msg = (task.getException() != null)
                        ? task.getException().getMessage() : "Post failed";
                Toast.makeText(PostItemActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
