package com.example.expensemanager.views.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensemanager.R;
import com.example.expensemanager.adapters.TransactionsAdapter;
import com.example.expensemanager.databinding.ActivityMainBinding;
import com.example.expensemanager.databinding.DialogFilterBinding;
import com.example.expensemanager.databinding.FragmentTransactionsBinding;
import com.example.expensemanager.databinding.RowTransactionsBinding;
import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.utils.Helper;
import com.example.expensemanager.viemodels.MainViewModel;
import com.example.expensemanager.views.activities.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;


public class TransactionsFragment extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters

    public TransactionsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentTransactionsBinding binding;



    Calendar calendar;

    Calendar dailyCalendar;

    public MainViewModel viewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentTransactionsBinding.inflate(inflater);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        dailyCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();

        updateDate();

        binding.nextDate.setOnClickListener(c-> {

            if (Constants.SELECTED_TAB == 0) {
                dailyCalendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.DATE, 1);
            }
            else if (Constants.SELECTED_TAB == 1) {
                calendar.add(Calendar.MONTH, 1);
            }

            updateDate();

        });

        binding.prevDate.setOnClickListener(c-> {
            if (Constants.SELECTED_TAB == 0) {
                dailyCalendar.add(Calendar.DATE, -1);
                calendar.add(Calendar.DATE, -1);
            }
            else if (Constants.SELECTED_TAB == 1) {
                calendar.add(Calendar.MONTH, -1);
            }

            updateDate();

        });


        binding.floatingActionButton.setOnClickListener(c-> {
            new AddTransactionFragment().show(getParentFragmentManager(), null);

        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Monthly")) {
                    Constants.SELECTED_TAB = 1;
                    updateDate();

                } else if (tab.getText().equals("Daily")) {
                    Constants.SELECTED_TAB= 0;
                    updateDate();

                }else if (tab.getText().equals("Filter")) {

                    Constants.SELECTED_TAB= 2;
                    DialogFilterBinding binding = DialogFilterBinding.inflate(getLayoutInflater());

                    AlertDialog filterDialog = new AlertDialog.Builder(getContext()).create();
                    filterDialog.setTitle("Set Filter");
                    filterDialog.setView(binding.getRoot());

                    binding.startDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                            datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                calendar.set(Calendar.MONTH, datePicker.getMonth());
                                calendar.set(Calendar.YEAR, datePicker.getYear());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                                String dateToShow = dateFormat.format(calendar.getTime());

                                binding.startDate.setText(dateToShow);
                                viewModel.startDate = calendar.getTime();

                            });
                            datePickerDialog.show();
                        }
                    });
                    binding.endDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                            datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                calendar.set(Calendar.MONTH, datePicker.getMonth());
                                calendar.set(Calendar.YEAR, datePicker.getYear());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                                String dateToShow = dateFormat.format(calendar.getTime());

                                binding.endDate.setText(dateToShow);
                                viewModel.endDate = calendar.getTime();

                            });
                            datePickerDialog.show();
                        }
                    });



                    filterDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", (dialogInterface, i) -> {
                        viewModel.getTransactions(calendar, dailyCalendar, viewModel.startDate, viewModel.endDate);
                        filterDialog.dismiss();
                    });
                    filterDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
                        filterDialog.dismiss();
                    });

                    filterDialog.show();

                    updateDate();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.transactionsList.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.transactions.observe(getViewLifecycleOwner(), new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getActivity()  , transactions);
                binding.transactionsList.setAdapter(transactionsAdapter);
                if(transactions.size() > 0) {
                    binding.emptyState.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                }

            }
        });

        viewModel.totalIncome.observe(getViewLifecycleOwner(), new Observer<Double> () {

            @Override
            public void onChanged(Double aDouble) {
                binding.incomeLbl.setText(String.valueOf(aDouble));

            }
        });
        viewModel.totalExpense.observe(getViewLifecycleOwner(), new Observer<Double> () {

            @Override
            public void onChanged(Double aDouble) {
                binding.expenseLbl.setText(String.valueOf(aDouble));

            }
        });
        viewModel.totalAmount.observe(getViewLifecycleOwner(), new Observer<Double> () {

            @Override
            public void onChanged(Double aDouble) {
                binding.totalLbl.setText(String.valueOf(aDouble));

            }
        });



        viewModel.getTransactions(calendar, dailyCalendar, viewModel.startDate, viewModel.endDate);
        return binding.getRoot();

    }

    void updateDate() {
        if (Constants.SELECTED_TAB == Constants.DAILY) {

            binding.currentDate.setText(Helper.formatDate(dailyCalendar.getTime()));

        } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }else if(Constants.SELECTED_TAB == Constants.FILTER) {
            binding.currentDate.setText("Filter Results");
        }

        viewModel.getTransactions(calendar, dailyCalendar, viewModel.startDate, viewModel.endDate);
    }
}