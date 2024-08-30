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
    Calendar dailyCalendar;
    public Date startDate;
    public Date endDate;


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

    public void getTransactions(Calendar calendar, Calendar dailyCalendar, Date startDate, Date endDate) {
        this.dailyCalendar = dailyCalendar;
        dailyCalendar.set(Calendar.HOUR_OF_DAY, 0);
        dailyCalendar.set(Calendar.MINUTE, 0);
        dailyCalendar.set(Calendar.SECOND, 0);
        dailyCalendar.set(Calendar.MILLISECOND, 0);
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

               if (Constants.SELECTED_ACCOUNT.equals("All")) {

                   newTransactions = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .findAll();
                   income = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .equalTo("type", Constants.INCOME)
                           .sum("amount")
                           .doubleValue();
                   expense = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .equalTo("type", Constants.EXPENSE)
                           .sum("amount")
                           .doubleValue();

                   total = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .sum("amount")
                           .doubleValue();
               }
               else{

                   newTransactions = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .equalTo("account", Constants.SELECTED_ACCOUNT)
                           .findAll();

                   income = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .equalTo("account", Constants.SELECTED_ACCOUNT)
                           .equalTo("type", Constants.INCOME)
                           .sum("amount")
                           .doubleValue();

                   expense = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .equalTo("account", Constants.SELECTED_ACCOUNT)
                           .equalTo("type", Constants.EXPENSE)
                           .sum("amount")
                           .doubleValue();

                   total = realm.where(Transaction.class)
                           .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                           .lessThan("date", new Date(dailyCalendar.getTime().getTime() + 86400000))
                           .equalTo("account", Constants.SELECTED_ACCOUNT)
                           .sum("amount")
                           .doubleValue();
               }


                transactions.setValue(newTransactions);

        }
        else if (Constants.SELECTED_TAB == Constants.MONTHLY)
        {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH, 1);
            Date endTime = calendar.getTime();

            if (Constants.SELECTED_ACCOUNT.equals("All"))
            {
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


            }
            else
            {
                newTransactions = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .findAll();

                income = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .equalTo("type", Constants.INCOME)
                        .sum("amount")
                        .doubleValue();
                expense = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .equalTo("type", Constants.EXPENSE)
                        .sum("amount")
                        .doubleValue();

                total = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .sum("amount")
                        .doubleValue();
            }
            transactions.setValue(newTransactions);


        }else if (Constants.SELECTED_TAB == Constants.FILTER)
        {

            if (Constants.SELECTED_ACCOUNT.equals("All"))
            {
                newTransactions = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .findAll();

                income = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .equalTo("type", Constants.INCOME)
                        .sum("amount")
                        .doubleValue();
                expense = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .equalTo("type", Constants.EXPENSE)
                        .sum("amount")
                        .doubleValue();

                total = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .sum("amount")
                        .doubleValue();


            }
            else
            {
                newTransactions = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .findAll();

                income = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .equalTo("type", Constants.INCOME)
                        .sum("amount")
                        .doubleValue();
                expense = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .equalTo("type", Constants.EXPENSE)
                        .sum("amount")
                        .doubleValue();

                total = realm.where(Transaction.class)
                        .greaterThanOrEqualTo("date", startDate)
                        .lessThanOrEqualTo("date", endDate)
                        .equalTo("account", Constants.SELECTED_ACCOUNT)
                        .sum("amount")
                        .doubleValue();
            }
            transactions.setValue(newTransactions);
        }
        totalAmount.setValue(total);
        totalIncome.setValue(income);
        totalExpense.setValue(expense);


    }

    public void getTransactions(Calendar calendar, String type, Calendar dailyCalendar, Date startDate, Date endDate) {
        this.dailyCalendar = dailyCalendar;
        dailyCalendar.set(Calendar.HOUR_OF_DAY, 0);
        dailyCalendar.set(Calendar.MINUTE, 0);
        dailyCalendar.set(Calendar.SECOND, 0);
        dailyCalendar.set(Calendar.MILLISECOND, 0);
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        RealmResults<Transaction> newTransactions = null;
        if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {


            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", dailyCalendar.getTime())
                    .lessThan("date", new Date(dailyCalendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
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
        } else if (Constants.SELECTED_TAB_STATS == Constants.FILTER)
        {
            newTransactions = realm.where(Transaction.class)
                    .greaterThan("date", startDate)
                    .lessThanOrEqualTo("date", endDate)
                    .equalTo("type", type)
                    .findAll();
        }


        categoriesTransactions.setValue(newTransactions);

    }

    public  void deleteTransaction(Transaction transaction) {
        realm.beginTransaction();
        transaction.deleteFromRealm();
        realm.commitTransaction();
        getTransactions(calendar, dailyCalendar, calendar.getTime(), calendar.getTime());
    }

    public static void addTransaction(Transaction transaction) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();

    }


}
