package hu.ait.ostore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import hu.ait.ostore.adapter.ItemRecyclerAdapter;
import hu.ait.ostore.touch.ItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_TODO_ID = "KEY_TODO_ID";
    public static final int REQUEST_CODE_EDIT = 101;

    private ItemRecyclerAdapter itemRecyclerAdapter;
    private RecyclerView recyclerItem;

    private int positionToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MainApplication)getApplication()).openRealm();

        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_item_anim);
        View myView = findViewById(R.id.include);

        myView.startAnimation(myAnimation);

        setupUI();
    }

    private void setupUI() {
        setUpToolBar();
        setUpAddTodoUI();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerItem = (RecyclerView) findViewById(R.id.recyclerItem);
        recyclerItem.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerItem.setLayoutManager(layoutManager);

        itemRecyclerAdapter = new ItemRecyclerAdapter(this, ((MainApplication)getApplication()).getRealmItem());
        recyclerItem.setAdapter(itemRecyclerAdapter);

        // adding touch support
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(itemRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerItem);

    }

    private void setUpAddTodoUI() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                showAddTodoDialog();
            }
        });
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void showAddTodoDialog() {

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.popup_anim);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_edit_item_alert_dialog, null);
        builder.setTitle("New Item");

        mView.startAnimation(animation);

        final EditText itemName = (EditText) mView.findViewById(R.id.itemName);
        final EditText itemPrice = (EditText) mView.findViewById(R.id.itemPrice);
        final EditText itemDescription = (EditText) mView.findViewById(R.id.itemDescription);
        final Spinner itemCategory = (Spinner) mView.findViewById(R.id.itemCategory);
        final Button addBtn = (Button) mView.findViewById(R.id.addBtn);
        final Button cancelBtn = (Button) mView.findViewById(R.id.cancelBtn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategory.setAdapter(adapter);

        builder.setView(mView);

        final AlertDialog dialog = builder.create();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descriptionString = itemDescription.getText().toString();
                if ("".equals(descriptionString)) {
                    descriptionString = "";
                }
                final String finalDescriptionString = descriptionString;

                if ("".equals(itemName.getText().toString())) {
                    itemName.setError("can not be empty");
                } else if ("".equals(itemPrice.getText().toString())) {
                    itemPrice.setError("please enter the price");
                } else {
                    //itemRecyclerAdapter.addTodo(new Item(etTodoText.getText().toString(), false));
                    itemRecyclerAdapter.addTodo(
                            itemName.getText().toString(),
                            Double.valueOf(itemPrice.getText().toString()),
                            finalDescriptionString,
                            itemCategory.getSelectedItem().toString());

                    recyclerItem.scrollToPosition(0);
                    dialog.dismiss();
                }
            }
            });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void openEditActivity(int index, String todoID) {
        positionToEdit = index;

        Intent startEdit = new Intent(this, EditItemActivity.class);

        startEdit.putExtra(KEY_TODO_ID, todoID);

        startActivityForResult(startEdit, REQUEST_CODE_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == REQUEST_CODE_EDIT) {
                    String todoID  = data.getStringExtra(
                            EditItemActivity.KEY_TODO);

                    itemRecyclerAdapter.updateTodo(todoID, positionToEdit);
                }
                break;
            case RESULT_CANCELED:
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainApplication)getApplication()).closeRealm();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar Item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            itemRecyclerAdapter.dismissAllItems();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
