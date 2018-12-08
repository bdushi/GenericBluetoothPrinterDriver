package al.bruno.genericprinterdemo;

import android.view.View;

/**
 * Created by bruno on 7/29/2016.
 */
public interface RecyclerViewOnClick {
    void onItemClick(View view, int position);
    boolean onLongItemClick(View view, int position);
}
