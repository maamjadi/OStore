package hu.ait.ostore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import hu.ait.ostore.data.Item;
import io.realm.Realm;

public class EditItemActivity extends AppCompatActivity {
    public static final String KEY_TODO = "KEY_TODO";
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemDescription;
    private Spinner itemCategory;
    private CheckBox alreadyPurchased;
    private Item todoToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_anim);

        View myView = findViewById(R.id.editView);
        myView.startAnimation(animation);

        //toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().hasExtra(MainActivity.KEY_TODO_ID)) {
            String todoID = getIntent().getStringExtra(MainActivity.KEY_TODO_ID);
            todoToEdit = getRealm().where(Item.class)
                    .equalTo("todoID", todoID)
                    .findFirst();
        }

        itemName = (EditText) findViewById(R.id.itemName);
        itemPrice = (EditText) findViewById(R.id.itemPrice);
        itemDescription = (EditText) findViewById(R.id.itemDescription);
        itemCategory = (Spinner) findViewById(R.id.itemCategory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategory.setAdapter(adapter);

        alreadyPurchased = (CheckBox) findViewById(R.id.alreadyPurchased);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTodo();
            }
        });

        if (todoToEdit != null) {
            itemName.setText(todoToEdit.getItemText());
            itemPrice.setText(String.valueOf(todoToEdit.getItemPrice()));
            itemDescription.setText(todoToEdit.getItemDescription());

            //for value of spinner
            String spinnerString = todoToEdit.getItemCategory();
            ArrayAdapter myadapt = (ArrayAdapter) itemCategory.getAdapter();
            int spinnerPosition = myadapt.getPosition(spinnerString);
            itemCategory.setSelection(spinnerPosition);

            alreadyPurchased.setChecked(todoToEdit.isPurchased());
        }
    }

    public Realm getRealm() {
        return ((MainApplication) getApplication()).getRealmItem();
    }

    private void saveTodo() {
        if ("".equals(itemName.getText().toString())) {
            itemName.setError("can not be empty");
        } else if ("".equals(itemPrice.getText().toString())) {
            itemPrice.setError("please enter the price");
        } else {
            Intent intentResult = new Intent();

            String descriptionString = itemDescription.getText().toString();
            if ("".equals(descriptionString)) {
                descriptionString = "";
            }

            getRealm().beginTransaction();
            todoToEdit.setItemText(itemName.getText().toString());
            todoToEdit.setPurchased(alreadyPurchased.isChecked());
            todoToEdit.setItemPrice(Double.parseDouble(itemPrice.getText().toString()));
            todoToEdit.setItemDescription(descriptionString);
            todoToEdit.setItemCategory(itemCategory.getSelectedItem().toString());
            getRealm().commitTransaction();

            intentResult.putExtra(KEY_TODO, todoToEdit.getTodoID());
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
