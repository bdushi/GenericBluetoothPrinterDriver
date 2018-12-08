package al.bruno.genericprinterdemo;

import android.databinding.ViewDataBinding;

/**
 * Created by bruno on 6/23/2017.
 */

public interface RecyclerCallback<VM extends ViewDataBinding, T> {
    void bindData(VM binder, T model);
}