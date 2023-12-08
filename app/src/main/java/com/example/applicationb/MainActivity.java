package com.example.applicationb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button loadData;
    FloatingActionButton addAlbum;
    RecyclerView recyclerView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAlbum = findViewById(R.id.addArtist);

        loadData = findViewById(R.id.loadData);
        recyclerView = findViewById(R.id.rv_album);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        loadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayData();
            }
        });

        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        adapter.setItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String userName, String artist) {
                showEditDialog(userName, artist);
            }
        });

        adapter.setDeleteClickListener(new MyAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String userName) {
                performDeleteOperation(userName);
                displayData();
            }
        });
    }

    // Method to retrieve and display data from a content provider
    @SuppressLint({"Range", "NotifyDataSetChanged", "SetTextI18n"})
    public void displayData() {

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.albumprovider/users"), null, null, null, null);

        List<Album> userNames = new ArrayList<>();

        // Extract data from the cursor and populate the list
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Album album = new Album(cursor.getString(cursor.getColumnIndex("title")), cursor.getString(cursor.getColumnIndex("artist")));
                userNames.add(album);
                cursor.moveToNext();
            }
        }

        adapter.setData(userNames);
        adapter.notifyDataSetChanged();

    }

    // Method to show a dialog for editing an entry
    private void showEditDialog(final String userName, final String artist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Entry");

        final EditText editName = new EditText(this);
        editName.setInputType(InputType.TYPE_CLASS_TEXT);
        editName.setText(userName);
        editName.setHint("New Title");

        final EditText editArtist = new EditText(this);
        editArtist.setInputType(InputType.TYPE_CLASS_TEXT);
        editArtist.setText(artist);
        editArtist.setHint("New Artist");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editName);
        layout.addView(editArtist);
        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = editName.getText().toString();
                String newArtist = editArtist.getText().toString();

                if (!newTitle.equals("") && !newArtist.equals("")) {

                    updateEntry(userName, newTitle, newArtist);
                    displayData();
                    notifyDataModification();
                }else {
                    Toast.makeText(MainActivity.this, "Add All Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Method to show a dialog for adding a new entry
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Entry");

        // Set up the input
        final EditText editName = new EditText(this);
        editName.setInputType(InputType.TYPE_CLASS_TEXT);
        editName.setHint("Title");

        final EditText editArtist = new EditText(this);
        editArtist.setInputType(InputType.TYPE_CLASS_TEXT);
        editArtist.setHint("Artist");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editName);
        layout.addView(editArtist);
        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = editName.getText().toString();
                String newArtist = editArtist.getText().toString();

                if (!newTitle.equals("") && !newArtist.equals("")) {
                    // Update the entry in Application A
                    ContentValues values = new ContentValues();
                     values.put("title", newTitle);
                     values.put("artist", newArtist);
                     Uri uri = Uri.parse("content://com.example.albumprovider/users");
                     getContentResolver().insert(uri, values);

                     adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(MainActivity.this, "Add All Fields", Toast.LENGTH_SHORT).show();
                }

                displayData();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Method to update an entry in the content provider
    @SuppressLint("NotifyDataSetChanged")
    private void updateEntry(String userName, String newName, String newArtist) {
        // Implement a method to update the entry in Application A using a ContentResolver
        // For example:
         ContentValues values = new ContentValues();
         values.put("title", newName);
         values.put("artist", newArtist);
         Uri uri = Uri.parse("content://com.example.albumprovider/users");
         getContentResolver().update(uri, values, "title=?", new String[]{userName});

         adapter.notifyDataSetChanged();
    }

    // Method to perform delete operation on an entry in the content provider
    @SuppressLint("NotifyDataSetChanged")
    private void performDeleteOperation(String userName) {
         Uri uri = Uri.parse("content://com.example.albumprovider/users");
         getContentResolver().delete(uri, "title=?", new String[]{userName});
         adapter.notifyDataSetChanged();
    }

    // Method to notify data modification through a broadcast
    private void notifyDataModification() {
        Intent intent = new Intent("com.example.DATA_MODIFIED");
        sendBroadcast(intent);
    }

}