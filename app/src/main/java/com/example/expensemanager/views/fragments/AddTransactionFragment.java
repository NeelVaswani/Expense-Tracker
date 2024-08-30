package com.example.expensemanager.views.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.expensemanager.adapters.AccountsAdapter;
import com.example.expensemanager.adapters.CategoryAdapter;
import com.example.expensemanager.R;
import com.example.expensemanager.databinding.FragmentAddTransactionBinding;
import com.example.expensemanager.databinding.ListDialogBinding;
import com.example.expensemanager.databinding.RowTransactionsBinding;
import com.example.expensemanager.models.Account;
import com.example.expensemanager.models.Category;
import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;
import com.example.expensemanager.views.activities.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AddTransactionFragment extends BottomSheetDialogFragment {


    public AddTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentAddTransactionBinding binding;
    Transaction transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater);

        transaction = new Transaction();

        binding.incomeBtn.setOnClickListener(c->{
            binding.incomeBtn.setBackgroundResource(android.R.color.holo_green_dark);
            binding.expenseBtn.setBackgroundResource(R.color.darkGrey);
            transaction.setType(Constants.INCOME);
        });

        binding.expenseBtn.setOnClickListener(c->{
            binding.incomeBtn.setBackgroundResource(R.color.darkGrey);
            binding.expenseBtn.setBackgroundResource(android.R.color.holo_red_dark);
            transaction.setType(Constants.EXPENSE);
        });

        binding.date.setOnClickListener(new View.OnClickListener() {
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

                    binding.date.setText(dateToShow);

                    transaction.setDate(calendar.getTime());
                    transaction.setId(calendar.getTime().getTime());

                });
                datePickerDialog.show();
            }
        });

        binding.category.setOnClickListener(c-> {

            ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
            AlertDialog categoryDialog = new AlertDialog.Builder(getContext()).create();
            categoryDialog.setView(dialogBinding.getRoot());




            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), Constants.categories, new CategoryAdapter.CategoryClickListener() {
                @Override
                public void onCategoryClicked(Category category) {
                    binding.category.setText(category.getCategoryName());
                    transaction.setCategory(category.getCategoryName());
                    categoryDialog.dismiss();
                }
            });
            dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            dialogBinding.recyclerView.setAdapter(categoryAdapter);


            categoryDialog.show();

        });

        binding.account.setOnClickListener(c-> {
            ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
            AlertDialog accountDialog = new AlertDialog.Builder(getContext()).create();
            accountDialog.setView(dialogBinding.getRoot());

            ArrayList<Account> accountArrayList = new ArrayList<>();
            accountArrayList.add(new Account(0, "Cash"));
            accountArrayList.add(new Account(0, "Bank"));
            accountArrayList.add(new Account(0, "Card"));
            accountArrayList.add(new Account(0, "Other"));

            AccountsAdapter accountsAdapter = new AccountsAdapter(getContext(), accountArrayList, new AccountsAdapter.AccountsClickListener() {
                @Override
                public void onAccountSelected(Account account) {
                    binding.account.setText(account.getAccountName());
                    transaction.setAccount(account.getAccountName());

                    accountDialog.dismiss();
                }
            });



            dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
            dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
            dialogBinding.recyclerView.setAdapter(accountsAdapter);

            accountDialog.show();
        });

        binding.saveTransaction.setOnClickListener(c1-> {

            String note = binding.note.getText().toString();
            if (transaction.getType() != null && transaction.getDate() != null && transaction.getCategory() != null && transaction.getAccount() != null && !binding.amount.getText().toString().isEmpty() ) {
                double amount =  Double.parseDouble(binding.amount.getText().toString());
                if (transaction.getType().equals(Constants.EXPENSE)) {
                    transaction.setAmount(amount * -1);
                } else {
                    transaction.setAmount(amount);
                }
                transaction.setNote(note);

                ((MainActivity)getActivity()).viewModel.addTransaction(transaction);
                ((MainActivity)getActivity()).getTransactions();
                dismiss();


            }
            else {
                binding.saveTransaction.setError("Please fill all the fields");
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }


        });

        return binding.getRoot();
    }
}