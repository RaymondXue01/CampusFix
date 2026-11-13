package com.example.testingapp.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    // Use mutableLiveData to hold the data that can be observed by the view
    private MutableLiveData<String> title;

    // Constructor
    public MainViewModel() {
        title = new MutableLiveData<>();
        title.setValue("CampusFix");
    }

    public LiveData<String> getTitle() {
        return title;
    }
}
