package hu.ait.ostore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import hu.ait.ostore.MainActivity;
import hu.ait.ostore.R;
import hu.ait.ostore.data.Item;
import hu.ait.ostore.touch.ItemTouchHelperAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class ItemRecyclerAdapter
        extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Item> todoList;

    private Context context;

    private Realm realmTodo;


    public ItemRecyclerAdapter(Context context, Realm realmTodo) {
        this.context = context;
        this.realmTodo = realmTodo;

        RealmResults<Item> todoResult = realmTodo.where(Item.class).findAll().sort("itemText", Sort.ASCENDING);

        todoList = new ArrayList<Item>();

        for (int i = 0; i < todoResult.size(); i++) {
            todoList.add(todoResult.get(i));

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_row, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvTodo.setText(todoList.get(position).getItemText());
        holder.tvItemPrice.setText((String.valueOf(todoList.get(position).getItemPrice())));
        holder.tvItemDesc.setText(todoList.get(position).getItemDescription());
        holder.cbDone.setChecked(todoList.get(position).isPurchased());

        String category = todoList.get(position).getItemCategory();

        if ("T-Shirt".equals(category)) {
            holder.ivIcon.setImageResource(R.drawable.shirt);
        } else if ("Electronics".equals(category)) {
            holder.ivIcon.setImageResource(R.drawable.electronic);
        } else {
            holder.ivIcon.setImageResource(R.drawable.food);
        }

        holder.cbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realmTodo.beginTransaction();
                todoList.get(holder.getAdapterPosition()).setPurchased(holder.cbDone.isChecked());
                realmTodo.commitTransaction();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).openEditActivity(holder.getAdapterPosition(),
                        todoList.get(holder.getAdapterPosition()).getTodoID()
                );
            }
        });

    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }


    public void addTodo(String itemTitle, Double itemPrice, String itemDescription, String itemCategory) {
        realmTodo.beginTransaction();
        Item newTodo = realmTodo.createObject(Item.class, UUID.randomUUID().toString());
        newTodo.setItemText(itemTitle);
        newTodo.setPurchased(false);
        newTodo.setItemPrice(itemPrice);
        newTodo.setItemDescription(itemDescription);
        newTodo.setItemCategory(itemCategory);
        realmTodo.commitTransaction();

        todoList.add(0, newTodo);

        notifyItemInserted(0);
    }

    public void updateTodo(String todoID, int positionToEdit) {
        Item todo = realmTodo.where(Item.class)
                .equalTo("todoID", todoID)
                .findFirst();

        todoList.set(positionToEdit, todo);

        notifyItemChanged(positionToEdit);
    }


    @Override
    public void onItemDismiss(int position) {
        realmTodo.beginTransaction();
        todoList.get(position).deleteFromRealm();
        realmTodo.commitTransaction();


        todoList.remove(position);

        // refreshes the whole list
        //notifyDataSetChanged();
        // refreshes just the relevant part that has been deleted
        notifyItemRemoved(position);
    }

    public void dismissAllItems() {

        int numOfItems = getItemCount();
        realmTodo.beginTransaction();
        for (int i=0; i<numOfItems; ++i) {
            todoList.get(i).deleteFromRealm();
        }
        realmTodo.commitTransaction();

        todoList.clear();

        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        /*todoList.add(toPosition, todoList.get(fromPosition));
        todoList.remove(fromPosition);*/

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(todoList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(todoList, i, i - 1);
            }
        }


        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbDone;
        private TextView tvTodo;
        private TextView tvItemPrice;
        private TextView tvItemDesc;
        private ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            cbDone = (CheckBox) itemView.findViewById(R.id.cbDone);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvTodo = (TextView) itemView.findViewById(R.id.tvTodo);
            tvItemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            tvItemDesc = (TextView) itemView.findViewById(R.id.tvItemDesc);
        }
    }

}
