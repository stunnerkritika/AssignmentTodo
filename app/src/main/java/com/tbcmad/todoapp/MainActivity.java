package com.tbcmad.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.tbcmad.todoapp.data.TodoDAO;
import com.tbcmad.todoapp.model.ETodo;
import com.tbcmad.todoapp.viewModel.TodoViewModel;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    Fragment fragment;
    FloatingActionButton floatingActionButton;
    TodoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        fragment = new ListTodoFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.list_activity_container, fragment)
                .commit();

        floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_profile:
                startActivity(new Intent(MainActivity.this, Profile.class));
                break;
            case R.id.mnu_delete_all:
                DeleteAll();
                break;
            case R.id.mnu_delete_cpmpleted:
                DeleteAllCompleted();
                break;
            case R.id.mnu_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Email has been sent to you verify it to continue ! ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void DeleteAllCompleted() {
        List<ETodo> todoList = ListTodoFragment.viewModel.getAllTodos().getValue();
        for (int i = 0; i < todoList.size(); i++) {

            if (todoList.get(i).isCompleted()) {
                ETodo todo = todoList.get(i);
                ListTodoFragment.viewModel.deleteById(todo);
            }

        }
        Toast.makeText(getApplicationContext(), "Delete Completed", Toast.LENGTH_LONG).show();


    }

    void DeleteAll() {
        List<ETodo> todoList = ListTodoFragment.viewModel.getAllTodos().getValue();
        for (int i = 0; i < todoList.size(); i++) {
            ETodo todo = todoList.get(i);
            ListTodoFragment.viewModel.deleteById(todo);
        }

        Toast.makeText(getApplicationContext(), "Delete all", Toast.LENGTH_LONG).show();


    }
}