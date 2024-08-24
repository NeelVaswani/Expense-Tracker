package com.example.expensemanager.views.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.viemodels.MainViewModel;
import com.example.expensemanager.R;
import com.example.expensemanager.databinding.ActivityMainBinding;

import com.example.expensemanager.views.fragments.StatsFragment;
import com.example.expensemanager.views.fragments.TransactionsFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    Calendar calendar;
    Calendar dailyCalendar;



    public MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        viewModel = new ViewModelProvider(this).get(MainViewModel.class);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transactions");


        Constants.setCategories();
        calendar = Calendar.getInstance();
        dailyCalendar = Calendar.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new TransactionsFragment());
        transaction.commit();

        binding.toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.All) {
                    Constants.SELECTED_ACCOUNT = "All";
                    getSupportActionBar().setTitle("Transactions");
                    getTransactions();
                } else if (item.getItemId() == R.id.Cash) {
                    Constants.SELECTED_ACCOUNT = "Cash";
                    getSupportActionBar().setTitle("Cash Transactions");
                    getTransactions();
                }else if (item.getItemId() == R.id.Bank) {
                    Constants.SELECTED_ACCOUNT = "Bank";
                    getSupportActionBar().setTitle("Bank Transactions");
                    getTransactions();
                }else if (item.getItemId() == R.id.Card) {
                    Constants.SELECTED_ACCOUNT = "Card";
                    getSupportActionBar().setTitle("Card Transactions");
                    getTransactions();
                }else if (item.getItemId() == R.id.Other) {
                    Constants.SELECTED_ACCOUNT = "Other";
                    getSupportActionBar().setTitle("Other Transactions");
                    getTransactions();
                }
                return false;
            }

        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (item.getItemId() == R.id.Transactions) {
                    getSupportFragmentManager().popBackStack();
                    getSupportActionBar().setTitle("Transactions");

                } else if (item.getItemId() == R.id.Stats) {
                    transaction.replace(R.id.content, new StatsFragment());
                    transaction.addToBackStack(null);
                    getSupportActionBar().setTitle("Statistics");
                }
                transaction.commit();
                return true;
            }
        });
    }


    public void getTransactions() {
        viewModel.getTransactions(calendar, dailyCalendar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}


