package com.example.expensemanager.viemodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.expensemanager.models.Transaction;
import com.example.expensemanager.utils.Constants;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainViewModel extends AndroidViewModel {
    static Realm realm;
    Calendar calendar;

    public MutableLiveData<RealmResults<Transaction>> transactions = new MutableLiveData<>();
    public MutableLiveData<RealmResults<Transaction>> categoriesTransactions = new MutableLiveData<>();

    public MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    public MutableLiveData<Double> totalExpense = new MutableLiveData<>();
    public MutableLiveData<Double> totalAmount = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        Realm.init(application);
        setupDatabase();


    }

    void setupDatabase() {

        realm = Realm.getDefaultInstance();

    }

    public void getTransactions(Calendar calendar) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        double income =0;
        double expense =0;
        double total =0;
        RealmResults<Transaction> newTransactions = null;
        if (Constants.SELECTED_TAB == Constants.DAILY) {


            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + 86400000))
                    .findAll();

             income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + 86400000))
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .doubleValue();
            expense = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + 86400000))
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .doubleValue();

             total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + 86400000))
                    .sum("amount")
                    .doubleValue();
            transactions.setValue(newTransactions);

        }
        else if (Constants.SELECTED_TAB == Constants.MONTHLY)
        {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH, 1);
            Date endTime = calendar.getTime();

             newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .findAll();

            income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .doubleValue();
             expense = realm.where(Transaction.class)
                     .greaterThanOrEqualTo("date", startTime)
                     .lessThan("date", endTime)
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .doubleValue();

             total = realm.where(Transaction.class)
                     .greaterThanOrEqualTo("date", startTime)
                     .lessThan("date", endTime)
                    .sum("amount")
                    .doubleValue();
            transactions.setValue(newTransactions);


        }
        totalAmount.setValue(total);
        totalIncome.setValue(income);
        totalExpense.setValue(expense);


    }

    public void getTransactions(Calendar calendar, String type) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        RealmResults<Transaction> newTransactions = null;
        if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", type)
                    .findAll();

        } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH,0);

            Date startTime = calendar.getTime();


            calendar.add(Calendar.MONTH,1);
            Date endTime = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", type)
                    .findAll();
        }


        categoriesTransactions.setValue(newTransactions);

    }

    public  void deleteTransaction(Transaction transaction) {
        realm.beginTransaction();
        transaction.deleteFromRealm();
        realm.commitTransaction();
        getTransactions(calendar);
    }

    public static void addTransaction(Transaction transaction) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();

    }

    public static void addTransaction() {
        realm.beginTransaction();


        realm.copyToRealmOrUpdate(new Transaction(Constants.INCOME, "Investment", "Cash", "Some note here", new Date(), 500, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.EXPENSE, "Other", "Bank", "Some note here", new Date(), -900, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.EXPENSE, "Rent", "Other", "Some note here", new Date(), -500, new Date().getTime()));
        realm.copyToRealmOrUpdate(new Transaction(Constants.INCOME, "Business", "Card", "Some note here", new Date(), 500, new Date().getTime()));

        realm.commitTransaction();
    }

}
