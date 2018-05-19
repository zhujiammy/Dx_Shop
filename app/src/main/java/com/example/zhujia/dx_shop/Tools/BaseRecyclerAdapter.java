package com.example.zhujia.dx_shop.Tools;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;


public abstract class BaseRecyclerAdapter<T extends BaseRecyclerAdapter.BaseRecyclerViewHolder> extends RecyclerView.Adapter<T> {


    private OnItemClickListener mListener ;
    private OnItemLongClickListener mLongListener ;


    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        T holder ;
        holder = createViewHolder(LayoutInflater.from(parent.getContext()),parent, viewType) ;
        if (holder == null){
            holder = createViewHolder(LayoutInflater.from(parent.getContext()), viewType) ;
        }

        return holder;
    }


    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onItemClick(v,holder.getPosition(),getItemId(holder.getPosition()));
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongListener != null){
                    return mLongListener.onItemLongClick(v,holder.getPosition(),getItemId(holder.getPosition()));
                }
                return false;
            }
        });
        onBindViewHolder((T) holder, position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener ;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mLongListener = listener ;
    }



    static public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder{
        public BaseRecyclerViewHolder(View itemView) {
            super(itemView);
        }
        public <K extends View> K findView(int id){
            return (K) itemView.findViewById(id);
        }
    }

    @Deprecated
    public T createViewHolder(LayoutInflater inflater, int viewType){
        return null ;
    }

    public T createViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType){
        return null ;
    };

    public abstract void onBindViewHolder(T holder, int position,List<Map<String,Object>> data);

    public interface OnItemClickListener{
        void onItemClick(View view, int position, long id) ;

    }
    public interface OnItemLongClickListener{
        boolean onItemLongClick(View view, int position, long id) ;

    }
}
