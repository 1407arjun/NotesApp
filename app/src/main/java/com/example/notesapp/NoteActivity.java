package com.example.notesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoteActivity extends AppCompatActivity {
    int noteId;
    boolean trash;
    EditText titleText, bodyText;
    HashMap<String, String> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        trash = intent.getBooleanExtra("trash", false);
        noteId = intent.getIntExtra("noteId", -1);

        titleText = findViewById(R.id.titleEditText);
        bodyText = findViewById(R.id.bodyEditText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!trash) {
            titleText.setEnabled(true);
            bodyText.setEnabled(true);
            if (noteId != -1) {
                if (MainActivity.title.get(noteId).get("Line1").equals("(no title)")){
                    MainActivity.title.get(noteId).put("Line1", "");
                }
                if (MainActivity.title.get(noteId).get("Line3").equals("(no body)")){
                    MainActivity.title.get(noteId).put("Line3", "");
                }
                titleText.setText(MainActivity.title.get(noteId).get("Line1"));
                bodyText.setText(MainActivity.title.get(noteId).get("Line3"));
                toolbar.setTitle("View note");
                DateFormat date =  DateFormat.getTimeInstance(DateFormat.SHORT);
                toolbar.setSubtitle("Edited " + date.format(new Date()));
            } else {
                HashMap<String, String> item = new HashMap<>(2);
                item.put("Line1", "");
                item.put("Line2", "");
                item.put("Line3", "");
                MainActivity.title.add(item);
                MainActivity.simpleAdapter.notifyDataSetChanged();
                noteId = MainActivity.title.size() - 1;
                toolbar.setTitle("New note");
                toolbar.setSubtitle(null);
                bodyText.requestFocus();
            }

            titleText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    MainActivity.title.get(noteId).put("Line1", String.valueOf(s));
                    MainActivity.simpleAdapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                    try {
                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    DateFormat date =  DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                    MainActivity.title.get(noteId).put("Line2", "Last modified on " + date.format(new Date()));
                    date =  DateFormat.getTimeInstance(DateFormat.SHORT);
                    toolbar.setSubtitle("Edited " + date.format(new Date()));
                    MainActivity.simpleAdapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                    try {
                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            bodyText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    MainActivity.title.get(noteId).put("Line3", String.valueOf(s));

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                    try {
                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    DateFormat date =  DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                    MainActivity.title.get(noteId).put("Line2", "Last modified on " + date.format(new Date()));
                    date =  DateFormat.getTimeInstance(DateFormat.SHORT);
                    toolbar.setSubtitle("Edited " + date.format(new Date()));
                    MainActivity.simpleAdapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                    try {
                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            if (noteId != -1) {
                titleText.setEnabled(false);
                bodyText.setEnabled(false);
                titleText.setText(TrashActivity.trashtitle.get(noteId).get("Line1"));
                bodyText.setText(TrashActivity.trashtitle.get(noteId).get("Line3"));
                toolbar.setTitle("View note");
                toolbar.setSubtitle(TrashActivity.trashtitle.get(noteId).get("Line2"));
            }
        }

        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.trash_note){
                    new AlertDialog.Builder(NoteActivity.this, R.style.MyDialogTheme)
                            .setIcon(R.drawable.ic_warning)
                            .setTitle("Move to trash?")
                            .setMessage("Deleted notes are stored in trash until deleted forever")
                            .setPositiveButton("Move to Trash", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    if (MainActivity.title.get(noteId).get("Line1").isEmpty() && !MainActivity.title.get(noteId).get("Line3").isEmpty()){
                                        MainActivity.title.get(noteId).put("Line1", "(no title)");
                                        TrashActivity.trashtitle.add(MainActivity.title.get(noteId));
                                        MainActivity.title.remove(noteId);
                                        Toast.makeText(NoteActivity.this, "Note moved to trash", Toast.LENGTH_SHORT).show();
                                    }else if (!MainActivity.title.get(noteId).get("Line1").isEmpty() && MainActivity.title.get(noteId).get("Line3").isEmpty()) {
                                        MainActivity.title.get(noteId).put("Line3", "(no body)");
                                        TrashActivity.trashtitle.add(MainActivity.title.get(noteId));
                                        MainActivity.title.remove(noteId);
                                        Toast.makeText(NoteActivity.this, "Note moved to trash", Toast.LENGTH_SHORT).show();
                                    }else if (MainActivity.title.get(noteId).get("Line1").isEmpty() && MainActivity.title.get(noteId).get("Line3").isEmpty()){
                                        MainActivity.title.remove(noteId);
                                        Toast.makeText(NoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
                                    }else if (!MainActivity.title.get(noteId).get("Line1").isEmpty() && !MainActivity.title.get(noteId).get("Line3").isEmpty()){
                                        TrashActivity.trashtitle.add(MainActivity.title.get(noteId));
                                        MainActivity.title.remove(noteId);
                                        Toast.makeText(NoteActivity.this, "Note moved to trash", Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.simpleAdapter.notifyDataSetChanged();
                                    if (MainActivity.trashstarted) {
                                        TrashActivity.trashsimpleAdapter.notifyDataSetChanged();
                                    }

                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                    try {
                                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                                        sharedPreferences.edit().putString("trashtitleList", ObjectSerializer.serialize(TrashActivity.trashtitle)).apply();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }else if (item.getItemId() == R.id.restore_note) {
                    new AlertDialog.Builder(NoteActivity.this, R.style.MyDialogTheme)
                            .setIcon(R.drawable.ic_warning)
                            .setTitle("Restore")
                            .setMessage("This will move the note back to My Notes")
                            .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(NoteActivity.this, TrashActivity.class);
                                    startActivity(intent);
                                    MainActivity.title.add(TrashActivity.trashtitle.get(noteId));
                                    TrashActivity.trashtitle.remove(noteId);
                                    MainActivity.simpleAdapter.notifyDataSetChanged();
                                    Toast.makeText(NoteActivity.this, "Note restored", Toast.LENGTH_SHORT).show();
                                    if (MainActivity.trashstarted) {
                                        TrashActivity.trashsimpleAdapter.notifyDataSetChanged();
                                    }

                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                    try {
                                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                                        sharedPreferences.edit().putString("trashtitleList", ObjectSerializer.serialize(TrashActivity.trashtitle)).apply();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }else if (item.getItemId() == R.id.delforever_note) {
                    new AlertDialog.Builder(NoteActivity.this, R.style.MyDialogTheme)
                            .setIcon(R.drawable.ic_warning)
                            .setTitle("Delete forever")
                            .setMessage("This action is irreversible")
                            .setPositiveButton("Delete Forever", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(NoteActivity.this, TrashActivity.class);
                                    startActivity(intent);
                                    TrashActivity.trashtitle.remove(noteId);
                                    Toast.makeText(NoteActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                                    if (MainActivity.trashstarted) {
                                        TrashActivity.trashsimpleAdapter.notifyDataSetChanged();
                                    }

                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                    try {
                                        sharedPreferences.edit().putString("trashtitleList", ObjectSerializer.serialize(TrashActivity.trashtitle)).apply();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }else if (item.getItemId() == R.id.copy_note){
                    temp = (HashMap<String, String>) MainActivity.title.get(noteId).clone();
                    if (temp.get("Line1").isEmpty() && !temp.get("Line3").isEmpty()){
                        temp.put("Line1", "(no title)");
                        MainActivity.title.add(temp);
                        Toast.makeText(NoteActivity.this, "Note copied", Toast.LENGTH_SHORT).show();
                    }else if (!temp.get("Line1").isEmpty() && temp.get("Line3").isEmpty()) {
                        temp.put("Line3", "(no body)");
                        MainActivity.title.add(temp);
                        Toast.makeText(NoteActivity.this, "Note copied", Toast.LENGTH_SHORT).show();
                    }else if (temp.get("Line1").isEmpty() && temp.get("Line3").isEmpty()){
                        Toast.makeText(NoteActivity.this, "Can't copy empty note", Toast.LENGTH_SHORT).show();
                    }else if (!temp.get("Line1").isEmpty() && !temp.get("Line3").isEmpty()){
                        MainActivity.title.add(temp);
                        Toast.makeText(NoteActivity.this, "Note copied", Toast.LENGTH_SHORT).show();
                    }
                    MainActivity.simpleAdapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                    try {
                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }else if (item.getItemId() == R.id.done_note){
                    titleText.clearFocus();
                    manager.hideSoftInputFromWindow(titleText.getWindowToken(), 0);
                    bodyText.clearFocus();
                    manager.hideSoftInputFromWindow(bodyText.getWindowToken(), 0);
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (!trash){
                    if (MainActivity.title.get(noteId).get("Line1").isEmpty() && !MainActivity.title.get(noteId).get("Line3").isEmpty()){
                        MainActivity.title.get(noteId).put("Line1", "(no title)");
                    }else if (!MainActivity.title.get(noteId).get("Line1").isEmpty() && MainActivity.title.get(noteId).get("Line3").isEmpty()) {
                        MainActivity.title.get(noteId).put("Line3", "(no body)");
                    }else if (MainActivity.title.get(noteId).get("Line1").isEmpty() && MainActivity.title.get(noteId).get("Line3").isEmpty()){
                        MainActivity.title.remove(noteId);
                        Toast.makeText(NoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
                    }
                    MainActivity.simpleAdapter.notifyDataSetChanged();
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                    try {
                        sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(NoteActivity.this, MainActivity.class);
                }else{
                    intent = new Intent(NoteActivity.this, TrashActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!trash) {
            getMenuInflater().inflate(R.menu.bottom_menu, menu);
        }else{
            getMenuInflater().inflate(R.menu.trash_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (!trash) {
            if (MainActivity.title.get(noteId).get("Line1").isEmpty() && !MainActivity.title.get(noteId).get("Line3").isEmpty()){
                MainActivity.title.get(noteId).put("Line1", "(no title)");
            }else if (!MainActivity.title.get(noteId).get("Line1").isEmpty() && MainActivity.title.get(noteId).get("Line3").isEmpty()) {
                MainActivity.title.get(noteId).put("Line3", "(no body)");
            }else if (MainActivity.title.get(noteId).get("Line1").isEmpty() && MainActivity.title.get(noteId).get("Line3").isEmpty()){
                MainActivity.title.remove(noteId);
                Toast.makeText(NoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
            }
            MainActivity.simpleAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
            try {
                sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent = new Intent(NoteActivity.this, MainActivity.class);
        }else{
            intent = new Intent(NoteActivity.this, TrashActivity.class);
        }
        startActivity(intent);
        finish();
    }
}