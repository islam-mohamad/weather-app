package com.islam.weatherapp.common.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter

class AppRecyclerViewAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>
) :
    ListAdapter<T, AppRecyclerViewAdapter.ViewHolder<ViewDataBinding>>(diffCallback) {

    private var layoutId: Int? = null
    private var clickCallback: RecyclerViewCallback? = null

    constructor(clickCallback: RecyclerViewCallback, diffCallback: DiffUtil.ItemCallback<T>) : this(diffCallback) {
        this.clickCallback = clickCallback
    }


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder<ViewDataBinding> {
        val bind: ViewDataBinding? =
            DataBindingUtil.bind(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
        return ViewHolder(bind!!)
    }

    /**
     * set variables to the layout item
     *  model variable for the entity class
     *  callback variable is an interface far handling clicks on layout item views
     * */
    @NonNull
    override fun onBindViewHolder(@NonNull holder: ViewHolder<ViewDataBinding>, position: Int) {
        val model: T = getItem(position)
        holder.binding.setVariable(BR.model, model)
        holder.binding.setVariable(BR.callback, clickCallback)
        holder.binding.executePendingBindings()
    }

    override fun getItemViewType(position: Int): Int {
        return layoutId!!
    }

    /**
     * set the recyclerView item layout ID
     * @sample layoutId can be set like  R.layout.item_recyclerView
     * */
    fun setItemViewType(layoutId: Int) {
        this.layoutId = layoutId
    }

    class ViewHolder<V : ViewDataBinding>(val binding: V) :
        RecyclerView.ViewHolder(binding.root)
}