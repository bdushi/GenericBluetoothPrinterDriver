package al.bruno.genericprinterdemo;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class CustomAdapter<T, VM extends ViewDataBinding> extends RecyclerView.Adapter<CustomViewHolder<T, VM>>
{
    private Context context;
    private List<T> t;
    private int resources;
    private RecyclerCallback<VM, T> bindingInterface;
    private RecyclerViewOnClick recyclerViewOnClick;

    CustomAdapter(Context context, List<T> t, int resources, RecyclerCallback<VM, T> bindingInterface, RecyclerViewOnClick recyclerViewOnClick) {
        this.t = t;
        this.context = context;
        this.resources = resources;
        this.bindingInterface = bindingInterface;
        this.recyclerViewOnClick = recyclerViewOnClick;
    }

    @Override
    public CustomViewHolder<T, VM> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resources, parent, false);
        if(recyclerViewOnClick != null)
            return new CustomViewHolder<>(view, bindingInterface, recyclerViewOnClick);
        else
            return new CustomViewHolder<>(view, bindingInterface);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder<T, VM> holder, int position) {
        holder.bindData(t.get(position));
    }

    @Override
    public int getItemCount()
    {
        return t == null ? 0 : t.size();
    }

    public List<T> getItems()
    {
        return t;
    }

    public T getItem(int position) {
        return t.get(t.indexOf(t.get(position)));
    }
    public int index(int position)
    {
        return t.indexOf(t.get(position));
    }
    public int index(T tt)
    {
        return t.indexOf(tt);
    }

    public void remove(T t1) {
        t.remove(t1);
        notifyDataSetChanged();
    }

    public void update(int position, T t1) {
        t.set(position, t1);
        notifyDataSetChanged();
    }
}
