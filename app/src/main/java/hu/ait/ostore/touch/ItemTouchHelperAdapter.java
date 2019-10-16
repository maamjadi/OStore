package hu.ait.ostore.touch;

public interface ItemTouchHelperAdapter {

    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
