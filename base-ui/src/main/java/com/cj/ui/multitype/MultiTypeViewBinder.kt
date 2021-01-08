package com.cj.ui.multitype

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Author:chris - jason
 * Date:2020-01-22.
 * Package:com.cj.base_common.multitype
 */
abstract class MultiTypeViewBinder <T>(mContext: Context, @LayoutRes layoutId:Int) :
    ItemViewBinder<T, ViewHolder>(){
    var mContext = mContext
    var layoutId = layoutId

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder.createViewHolder(mContext, parent, layoutId)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: T) {
        convert(holder,item,holder.adapterPosition)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: T, payloads: List<Any>) {
        convertPayloads(holder,item,holder.adapterPosition,payloads)
    }

    abstract fun convert(holder: ViewHolder, t:T, position:Int)

    abstract fun convertPayloads(holder: ViewHolder, item: T, position:Int, payloads: List<Any>)

}