package com.example.bilabilamart.listener;

import com.example.bilabilamart.model.DrinkModel;
import java.util.List;

public interface IDrinkLoadListener {
    void onDrinkLoadSuccess(List<DrinkModel> drinkModelList);
    void onDrinkLoadFailed(String message);
}

