package com.example.expensemanager.views.fragments;

import static com.example.expensemanager.utils.Constants.SELECTED_STATS_TYPE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.expensemanager.R;
import com.example.expensemanager.databinding.DialogFilterBinding;
import com.example.expensemanager.databinding.FragmentStatsBinding;
import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.utils.Helper;
import com.example.expensemanager.viemodels.MainViewModel;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmResults;


public class StatsFragment extends Fragment {


        public StatsFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        FragmentStatsBinding binding;

        Calendar calendar;
        Calendar dailyCalendar;
    /*
    0 = Daily
    1 = Monthly
     */


        public MainViewModel viewModel;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = FragmentStatsBinding.inflate(inflater);

            viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
            dailyCalendar = Calendar.getInstance();
            calendar = Calendar.getInstance();
            updateDate();

            binding.incomeBtn.setOnClickListener(view -> {
                binding.incomeBtn.setBackground(getContext().getDrawable(android.R.color.holo_green_dark));
                binding.expenseBtn.setBackground(getContext().getDrawable(R.color.darkGrey));
                binding.expenseBtn.setTextColor(getContext().getColor(R.color.textColor));
                binding.incomeBtn.setTextColor(getContext().getColor(R.color.greenColor));

                SELECTED_STATS_TYPE = Constants.INCOME;
                updateDate();
            });

            binding.expenseBtn.setOnClickListener(view -> {
                binding.incomeBtn.setBackground(getContext().getDrawable(R.color.darkGrey));
                binding.expenseBtn.setBackground(getContext().getDrawable(android.R.color.holo_red_dark));
                binding.incomeBtn.setTextColor(getContext().getColor(R.color.textColor));
                binding.expenseBtn.setTextColor(getContext().getColor(R.color.redColor));

                SELECTED_STATS_TYPE = Constants.EXPENSE;
                updateDate();
            });

            binding.nextDate.setOnClickListener(c-> {
                if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
                    dailyCalendar.add(Calendar.DATE, 1);
                    calendar.add(Calendar.DATE, 1);
                } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
                    calendar.add(Calendar.MONTH, 1);
                }
                updateDate();
            });

            binding.prevDate.setOnClickListener(c-> {
                if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
                    dailyCalendar.add(Calendar.DATE, -1);
                    calendar.add(Calendar.DATE, -1);
                } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
                    calendar.add(Calendar.MONTH, -1);
                }
                updateDate();
            });

            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getText().equals("Monthly")) {
                        Constants.SELECTED_TAB_STATS = 1;
                        updateDate();

                    } else if(tab.getText().equals("Daily")) {
                        Constants.SELECTED_TAB_STATS = 0;
                        updateDate();

                    }else if (tab.getText().equals("Filter")) {

                        Constants.SELECTED_TAB_STATS= 2;
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

                                });
                                datePickerDialog.show();
                            }
                        });



                        filterDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", (dialogInterface, i) -> {
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


            Pie pie = AnyChart.pie();

            viewModel.categoriesTransactions.observe(getViewLifecycleOwner(), new Observer<RealmResults<Transaction>>() {
                @Override
                public void onChanged(RealmResults<Transaction> transactions) {


                    if(transactions.size() > 0) {

                        binding.emptyState.setVisibility(View.GONE);
                        binding.anyChart.setVisibility(View.VISIBLE);

                        List<DataEntry> data = new ArrayList<>();

                        Map<String, Double> categoryMap = new HashMap<>();

                        for(Transaction transaction : transactions) {
                            String category = transaction.getCategory();
                            double amount = transaction.getAmount();

                            if(categoryMap.containsKey(category)) {
                                double currentTotal = categoryMap.get(category).doubleValue();
                                currentTotal += Math.abs(amount);

                                categoryMap.put(category, currentTotal);
                            } else {
                                categoryMap.put(category, Math.abs(amount));
                            }
                        }

                        for(Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                            data.add(new ValueDataEntry(entry.getKey(),entry.getValue()));
                        }
                        pie.data(data);

                    } else {
                        binding.emptyState.setVisibility(View.VISIBLE);
                        binding.anyChart.setVisibility(View.GONE);
                    }


                }
            });

            viewModel.getTransactions(calendar, SELECTED_STATS_TYPE, dailyCalendar, viewModel.startDate, viewModel.endDate);





            binding.anyChart.setChart(pie);


            return binding.getRoot();
        }

        void updateDate() {
            if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {

                binding.currentDate.setText(Helper.formatDate(dailyCalendar.getTime()));

            } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
                binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
            }else if(Constants.SELECTED_TAB_STATS == Constants.FILTER) {
                binding.currentDate.setText("Filter Results");
            }



            viewModel.getTransactions(calendar, SELECTED_STATS_TYPE, dailyCalendar, viewModel.startDate, viewModel.endDate);
        }
    }
