package al.bruno.genericprinterdemo;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by bruno on 6/23/2017.
 */

public class CustomViewHolder<T, VM extends ViewDataBinding> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    private VM binding;
    private RecyclerCallback<VM, T> bindingInterface;
    private RecyclerViewOnClick recyclerViewOnItemClick;
    public CustomViewHolder(View view, RecyclerCallback<VM, T> bindingInterface)
    {
        super(view);
        binding = DataBindingUtil.bind(view);
        this.bindingInterface = bindingInterface;
    }

    public CustomViewHolder(View view, RecyclerCallback<VM, T> bindingInterface, RecyclerViewOnClick recyclerViewOnItemClick)
    {
        super(view);
        binding = DataBindingUtil.bind(view);
        this.bindingInterface = bindingInterface;
        this.recyclerViewOnItemClick = recyclerViewOnItemClick;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    public void bindData(T model)
    {
        bindingInterface.bindData(binding, model);
        binding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        recyclerViewOnItemClick.onItemClick(view, this.getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View view) {
        return recyclerViewOnItemClick.onLongItemClick(view, this.getAdapterPosition());
    }
}
