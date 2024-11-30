package model;

import java.util.List;

public class OrderModel {
    public OrderModel(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    List<String> ingredients;
}
