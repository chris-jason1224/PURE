package com.cj.ui.multitype

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Author:chris - jason
 * Date:2020-01-22.
 * Package:com.cj.base_common.multitype
 */
class ViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mViews: SparseArray<View> = SparseArray()
    var mConvertView: View = itemView
    private var mContext: Context = context

    companion object {
        //通过View创建ViewHolder
        fun createViewHolder(context: Context, itemView: View): ViewHolder {
            return ViewHolder(context, itemView)
        }

        //通过layout创建ViewHolder
        fun createViewHolder(context: Context, parent: ViewGroup, layoutId: Int): ViewHolder {
            return ViewHolder(
                context,
                LayoutInflater.from(context).inflate(layoutId, parent, false)
            )
        }
    }

    inline fun <reified T : View> getView(vid: Int): T {
        var view = mViews.get(vid)

        if (view == null) {
            view = mConvertView.findViewById(vid)
            mViews.put(vid, view)
        }

        return view as T
    }

    fun getTag(vid: Int): Any {
        val view = getView(vid) as View
        return view.tag
    }

    fun setTag(vid: Int, key: Int, tag: Any) {
        val view = getView(vid) as View
        view.setTag(key, tag)
    }

}