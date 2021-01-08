package com.cj.ui.widget.dialog2
import com.cj.ui.widget.dialog2.base.BaseDialogView
import org.jetbrains.annotations.NotNull
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.OptionClickCallback
import com.cj.ui.util.ScreenUtil
import com.cj.ui.widget.roundshape.RoundTextView


/**
 * Author:chris - jason
 * Date:2020-02-11.
 * Package:com.cj.base_ui.view.dialog
 * 底部选择框
 */

class BottomOptionsView @JvmOverloads constructor(
    @NotNull context: Context,
    title:String?,
    options: ArrayList<String>,
    callback: OptionClickCallback? = null
) : BaseDialogView(context) {

    private lateinit var rv: RecyclerView
    private lateinit var rtvCancel: RoundTextView
    private var mCallback: OptionClickCallback? = callback
    //是否有title
    private var hasTitle: Boolean = false

    init {
        //显示在底部
        var window = window
        window?.setGravity(Gravity.BOTTOM)

        hasTitle = !TextUtils.isEmpty(title)
        if(hasTitle){
            options.add(0,title!!)
        }

        initList(options)
    }

    private fun initList(options: ArrayList<String>) {
        var adapter = Adapter(options)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(mContext)
        adapter.notifyDataSetChanged()
    }


    inner class Adapter(options: ArrayList<String>) : RecyclerView.Adapter<Holder>() {

        private var ops = options

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            var itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.base_ui_item_bottom_options_dialog_view_layout, parent,false);
            return Holder(itemView)
        }

        override fun getItemCount(): Int {
            return ops.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {

            holder.rtv_option.text = ops[position]

            if (ops.size == 1) {
                holder.v_divider.visibility = View.GONE
            } else {
                if (position == ops.size - 1) {
                    holder.v_divider.visibility = View.GONE
                } else {
                    holder.v_divider.visibility = View.VISIBLE
                }
            }

            holder.rtv_option.setOnClickListener {
                mCallback?.onOptionClick(ops[position], position)
                dismiss()
                mCallback?.onDismiss()
            }


            var lp = holder.rtv_option.layoutParams as (LinearLayout.LayoutParams)

            when (position) {
                0 -> {
                    holder.rtv_option.delegate.cornerRadius_TL = (ScreenUtil.dip2px(mContext, 10f))
                    holder.rtv_option.delegate.cornerRadius_TR = (ScreenUtil.dip2px(mContext, 10f))
                    holder.rtv_option.delegate.cornerRadius_BL = 0
                    holder.rtv_option.delegate.cornerRadius_BR = 0

                    if (hasTitle) {
                        lp.height = ScreenUtil.dip2px(mContext, 32f)
                        holder.rtv_option.setTextColor(Color.parseColor("#999999"))
                        holder.rtv_option.textSize = 12f
                    } else {
                        lp.height = ScreenUtil.dip2px(mContext, 48f)
                        holder.rtv_option.setTextColor(Color.parseColor("#333333"))
                        holder.rtv_option.textSize = 16f
                    }
                }

                (ops.size - 1) -> {
                    holder.rtv_option.delegate.cornerRadius_TL = 0
                    holder.rtv_option.delegate.cornerRadius_TR = 0
                    holder.rtv_option.delegate.cornerRadius_BL = ScreenUtil.dip2px(mContext, 10f)
                    holder.rtv_option.delegate.cornerRadius_BR = ScreenUtil.dip2px(mContext, 10f)
                }
                else -> {
                    holder.rtv_option.delegate.cornerRadius_TL = 0
                    holder.rtv_option.delegate.cornerRadius_TR = 0
                    holder.rtv_option.delegate.cornerRadius_BL = 0
                    holder.rtv_option.delegate.cornerRadius_BR = 0
                }
            }

            holder.rtv_option.layoutParams = lp

        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rtv_option: RoundTextView = itemView.findViewById(R.id.rtv_option)
        var v_divider: TextView = itemView.findViewById(R.id.v_divider)
    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_bottom_options_dialog_view_layout
    }

    override fun bindView(view: View) {
        rv = view.findViewById(R.id.rv)
        rtvCancel = view.findViewById(R.id.rtv_cancel)
        rtvCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rtv_cancel -> dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mCallback?.onDismiss()
    }

}
