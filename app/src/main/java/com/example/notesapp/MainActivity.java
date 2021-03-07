package com.example.notesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static ArrayList<HashMap<String, String>> title = new ArrayList<>();
    static SimpleAdapter simpleAdapter;
    static boolean trashstarted;
    LinearLayout notesLL;
    ListView listView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trashstarted = false;
        notesLL = findViewById(R.id.notesLL);
        listView = findViewById(R.id.notesListView);
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);

        title.clear();

        try {
            title = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(sharedPreferences.getString("titleList", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
            TrashActivity.trashtitle = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(sharedPreferences.getString("trashtitleList", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.textView);
        if (title.size() != 0) {
            textView.setText(title.size() + " Note(s)    ");
            listView.setVisibility(View.VISIBLE);
            notesLL.setVisibility(View.INVISIBLE);
        }else{
            textView.setText("No Notes  ");
            listView.setVisibility(View.INVISIBLE);
            notesLL.setVisibility(View.VISIBLE);
        }

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.gototrash) {
                    Intent intent = new Intent(MainActivity.this, TrashActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton addNote = findViewById(R.id.floatingActionButton);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("trash", false);
                startActivity(intent);
            }
        });

        simpleAdapter = new SimpleAdapter(this, title, R.layout.listview_layout, new String[] {"Line1", "Line2", "Line3"}, new int[] {R.id.text1, R.id.text3, R.id.text2});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("noteId", position);
                intent.putExtra("trash", false);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme)
                        .setIcon(R.drawable.ic_alert)
                        .setTitle("Note Options")
                        .setPositiveButton("Move to Trash", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TrashActivity.trashtitle.add(title.get(position));
                                title.remove(position);
                                simpleAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Note moved to trash", Toast.LENGTH_SHORT).show();
                                if (trashstarted) {
                                    TrashActivity.trashsimpleAdapter.notifyDataSetChanged();
                                }
                                if (title.size() != 0) {
                                    textView.setText(title.size() + " Note(s)    ");
                                    listView.setVisibility(View.VISIBLE);
                                    notesLL.setVisibility(View.INVISIBLE);
                                }else{
                                    textView.setText("No Notes  ");
                                    listView.setVisibility(View.INVISIBLE);
                                    notesLL.setVisibility(View.VISIBLE);
                                }

                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                try {
                                    sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(title)).apply();
                                    sharedPreferences.edit().putString("trashtitleList", ObjectSerializer.serialize(TrashActivity.trashtitle)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Copy Note", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                title.add((HashMap<String, String>) title.get(position).clone());
                                simpleAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Note copied", Toast.LENGTH_SHORT).show();
                                if (title.size() != 0) {
                                    textView.setText(title.size() + " Note(s)    ");
                                    listView.setVisibility(View.VISIBLE);
                                    notesLL.setVisibility(View.INVISIBLE);
                                }else{
                                    textView.setText("No Notes  ");
                                    listView.setVisibility(View.INVISIBLE);
                                    notesLL.setVisibility(View.VISIBLE);
                                }

                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                try {
                                    sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(title)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (title.size() != 0) {
            textView.setText(title.size() + " Note(s)    ");
            listView.setVisibility(View.VISIBLE);
            notesLL.setVisibility(View.INVISIBLE);
        }else{
            textView.setText("No Notes  ");
            listView.setVisibility(View.INVISIBLE);
            notesLL.setVisibility(View.VISIBLE);
        }
    }
}

