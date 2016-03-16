package com.vilyever.androidjsonmodel.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.vilyever.jsonmodel.Json;
import com.vilyever.jsonmodel.JsonModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Basket b = new Basket();
        Fruit f = new Fruit();
        f.no = 9;
        f.name = "test";
        b.fruits = new ArrayList<>();
        b.fruits.add(f);
        Apple a = new Apple();
        a.no = 1;
        a.name = "apple";
        a.color = "red";
        b.fruits.add(a);

        Orange o = new Orange();
        o.no = 7;
        o.name = "or";
        o.weight = "2kg";
        b.apple = o;

        System.out.println("bbbbbbbbbb " + b.toJson().toString());
        Basket jsonBasket = new Json<>(Basket.class).modelFromJsonString(b.toJson().toString());
        System.out.println("jsonBasket " + jsonBasket.toJson().toString());

        Apple apple = new Apple();
        apple.no = 1;
        apple.name = "apple";
        apple.color = "red";

        Basket basket = new Basket();
        basket.fruits = new ArrayList<>();
        basket.applse = new ArrayList<>();
        basket.oranges = new ArrayList<>();
        basket.apple = apple;

        for (int i = 0; i < 2; i++) {
            Orange orange = new Orange();
            orange.no = 2 * i;
            orange.name = "fruit orange " + i;
            orange.weight = "1" + i + "kg";
            basket.fruits.add(orange);
        }
        for (int i = 0; i < 2; i++) {
            Orange orange = new Orange();
            orange.no = 2 * i + 8;
            orange.name = "orange " + i;
            orange.weight = "1" + i + "kg";
            basket.oranges.add(orange);
        }
        for (int i = 0; i < 2; i++) {
            Apple app = new Apple();
            app.name = "apple " + i;
            app.color = "blue " + i;
            basket.applse.add(app);
        }


        Buck buck = new Buck();
        buck.baskets = new ArrayList<>();
        buck.baskets.add(basket);
        buck.integer = 22;
        buck.right = true;
        buck.fnumber = 8.7f;
        buck.aDouble = 11.13335;
        buck.type = Type.On;
        buck.date = new Date(0);
        buck.apples = new Apple[2];
        for (int i = 0; i < 2; i++) {
            Apple app = new Apple();
            app.no = i;
            app.name = "app " + i;
            app.color = "yellow " + i;
            buck.apples[i] = app;
        }

        System.out.println("buck " + buck.toJson());
        Buck jsonBuck = new Json<>(Buck.class).modelFromJson(buck.toJson());
        System.out.println("juck " + jsonBuck.toJson());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class Fruit extends JsonModel {
        @JsonKey("nam")
        public String name;

        public long no;
    }
    public static class Apple extends Fruit {
        @JsonKey("col")
        public String color;

        @Override
        public HashMap<String, String> getJsonKeyBindingDictionary() {
            HashMap dictionary =  super.getJsonKeyBindingDictionary();
            dictionary.put("name", "aname");
            return dictionary;
        }
    }
    public static class Orange extends Fruit {
        @JsonKey("wei")
        public String weight;
    }
    public static class Basket extends JsonModel {
        public List<Fruit> fruits;

        @JsonKey("apples")
        public List<Apple> applse;

        public List<Orange> oranges;
        public Fruit apple;
    }

    public static class Buck extends JsonModel {
        public List<Basket> baskets;

        @JsonKeyIgnore
        public int integer;

        public float fnumber;
        public double aDouble;
        public boolean right;
        public Type type;
        public Date date;
        public Apple[] apples;
    }
    public enum Type {
        Off, On;
    }

}
