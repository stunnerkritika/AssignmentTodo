package com.tbcmad.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.util.EthiopicCalendar;
import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbcmad.todoapp.model.ETodo;
import com.tbcmad.todoapp.viewModel.TodoViewModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class ListTodoFragment extends Fragment {
    View rootView;
    RecyclerView rvListTodo;
    public static TodoViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_list_todo, container, false);
        rvListTodo = rootView.findViewById(R.id.list_item_todo_rv_list_todo);


        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        LinearLayoutManager manager= new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvListTodo.setLayoutManager(manager);
        updateRV();

        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                List<ETodo> todoList = viewModel.getAllTodos().getValue();
                TodoAdaptor adaptor = new TodoAdaptor(todoList);
                ETodo todo = adaptor.getTodoAt(viewHolder.getAdapterPosition());
                viewModel.deleteById(todo);
            }
        }).attachToRecyclerView(rvListTodo);

        return  rootView;
    }

    void updateRV(){
        viewModel.getAllTodos().observe(this, new Observer<List<ETodo>>() {
            @Override
            public void onChanged(List<ETodo> eTodos) {
                TodoAdaptor adaptor = new TodoAdaptor(eTodos);
                rvListTodo.setAdapter(adaptor);
            }
        });
    }

    private class TodoHolder extends RecyclerView.ViewHolder{
        public View titles;
        TextView title, date;
        Button deleteById;
        Button completeById;

        public TodoHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_todo, parent, false));
            titles = itemView.findViewById(R.id.list_item_tv_titles);
            title = itemView.findViewById(R.id.list_item_tv_title);
            date = itemView.findViewById(R.id.list_item_tv_date);
            deleteById = itemView.findViewById(R.id.delete_button);
            completeById = itemView.findViewById(R.id.complete_button);

            completeById.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    TodoAdaptor adaptor = new TodoAdaptor(viewModel.getAllTodos().getValue());
                    int i = getAdapterPosition();
                    ETodo todo = adaptor.getTodoAt(i);

                    if(todo.isCompleted())
                    {
                        completeById.setBackgroundColor(R.color.red);
                    }
                    else{
                        completeById.setBackgroundColor(R.color.green);

                    }
                    todo.setCompleted(!todo.isCompleted());
                    viewModel.update(todo);
                }
            });

            deleteById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TodoAdaptor adaptor = new TodoAdaptor(viewModel.getAllTodos().getValue());
                    int i = getAdapterPosition();
                    ETodo todo = adaptor.getTodoAt(i);
                    viewModel.deleteById(todo);
                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadUpdateItem();
                }
            });
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadUpdateItem();
                }
            });
        }


        void loadUpdateItem(){
            TodoAdaptor adaptor = new TodoAdaptor(viewModel.getAllTodos().getValue());
            int i = getAdapterPosition();
            ETodo todo = adaptor.getTodoAt(i);
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("TodoId",todo.getId());
            startActivity(intent);
            Toast.makeText(getContext(),"Update Item: " + todo.getId(), Toast.LENGTH_LONG).show();
        }
        public void bind(ETodo todo){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            title.setText(todo.getTitle());
            date.setText(sdf.format(todo.getTodoDate()));
        }
    }

    private class TodoAdaptor extends RecyclerView.Adapter<TodoHolder>{
        List<ETodo> eTodoList;
        public TodoAdaptor(List<ETodo> todoList)
        {
            eTodoList = todoList;
        }

        @NonNull
        @Override
        public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TodoHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TodoHolder holder, int position) {
            ETodo todo = eTodoList.get(position);
            LinearLayout layout =(LinearLayout)((ViewGroup)holder.titles.getParent());


            if(!todo.isCompleted())
            {
                switch (todo.getPriority()) {
                    case 1:
                        layout.setBackgroundColor(getResources().getColor(R.color.color_high));
                        break;
                    case 2:
                        layout.setBackgroundColor(getResources().getColor(R.color.color_medium));
                        break;
                    case 3:
                        layout.setBackgroundColor(getResources().getColor(R.color.color_low));
                        break;
                }
            }
            else{
                layout.setBackgroundColor(getResources().getColor(R.color.green));
            }

            holder.bind(todo);
        }

        @Override
        public int getItemCount() {
            return eTodoList.size();
        }

        public ETodo getTodoAt(int position){
            return eTodoList.get(position);
        }
    }
}