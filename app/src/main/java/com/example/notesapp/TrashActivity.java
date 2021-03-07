package com.example.notesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TrashActivity extends AppCompatActivity {

    static ArrayList<HashMap<String, String>> trashtitle = new ArrayList<>();
    static SimpleAdapter trashsimpleAdapter;
    LinearLayout trashLL;
    ListView listView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        trashLL = findViewById(R.id.trashLL);
        listView = findViewById(R.id.trashListView);
        SharedPreferences sharedPreferences = TrashActivity.this.getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);

        trashtitle.clear();

        try {
            trashtitle = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(sharedPreferences.getString("trashtitleList", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.textView2);
        if (trashtitle.size() != 0) {
            textView.setText(trashtitle.size() + " Note(s)    ");
            listView.setVisibility(View.VISIBLE);
            trashLL.setVisibility(View.INVISIBLE);
        }else{
            textView.setText("No Notes  ");
            listView.setVisibility(View.INVISIBLE);
            trashLL.setVisibility(View.VISIBLE);
        }

        MainActivity.trashstarted = true;

        trashsimpleAdapter = new SimpleAdapter(this, trashtitle, R.layout.listview_layout, new String[] {"Line1", "Line2", "Line3"}, new int[] {R.id.text1, R.id.text3, R.id.text2});
        listView.setAdapter(trashsimpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TrashActivity.this, NoteActivity.class);
                intent.putExtra("noteId", position);
                intent.putExtra("trash", true);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(TrashActivity.this, R.style.MyDialogTheme)
                        .setIcon(R.drawable.ic_alert)
                        .setTitle("Trash Options")
                        .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.title.add(trashtitle.get(position));
                                trashtitle.remove(position);
                                MainActivity.simpleAdapter.notifyDataSetChanged();
                                Toast.makeText(TrashActivity.this, "Note restored", Toast.LENGTH_SHORT).show();
                                if (MainActivity.trashstarted) {
                                    trashsimpleAdapter.notifyDataSetChanged();
                                }
                                if (trashtitle.size() != 0) {
                                    textView.setText(trashtitle.size() + " Note(s)    ");
                                    listView.setVisibility(View.VISIBLE);
                                    trashLL.setVisibility(View.INVISIBLE);
                                }else{
                                    textView.setText("No Notes  ");
                                    listView.setVisibility(View.INVISIBLE);
                                    trashLL.setVisibility(View.VISIBLE);
                                }


                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                try {
                                    sharedPreferences.edit().putString("titleList", ObjectSerializer.serialize(MainActivity.title)).apply();
                                    sharedPreferences.edit().putString("trashtitleList", ObjectSerializer.serialize(trashtitle)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Delete Forever", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(TrashActivity.this, R.style.MyDialogTheme)
                                        .setIcon(R.drawable.ic_warning)
                                        .setTitle("Are you sure?")
                                        .setMessage("This action is irreversible")
                                        .setPositiveButton("Delete Forever", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trashtitle.remove(position);
                                                Toast.makeText(TrashActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                                                if (MainActivity.trashstarted) {
                                                    trashsimpleAdapter.notifyDataSetChanged();
                                                }
                                                if (trashtitle.size() != 0) {
                                                    textView.setText(trashtitle.size() + " Note(s)    ");
                                                    listView.setVisibility(View.VISIBLE);
                                                    trashLL.setVisibility(View.INVISIBLE);
                                                }else{
                                                    textView.setText("No Notes  ");
                                                    listView.setVisibility(View.INVISIBLE);
                                                    trashLL.setVisibility(View.VISIBLE);
                                                }

                                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notesapp", Context.MODE_PRIVATE);
                                                try {
                                                    sharedPreferences.edit().putString("trashtitleList", ObjectSerializer.serialize(trashtitle)).apply();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
                return true;
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TrashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        textView = findViewById(R.id.textView2);
        if (trashtitle.size() != 0) {
            textView.setText(trashtitle.size() + " Note(s)    ");
            listView.setVisibility(View.VISIBLE);
            trashLL.setVisibility(View.INVISIBLE);
        }else{
            textView.setText("No Notes  ");
            listView.setVisibility(View.INVISIBLE);
            trashLL.setVisibility(View.VISIBLE);
        }
    }
}