package sih.org.sih.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

import sih.org.sih.Modals.Stage;
import sih.org.sih.R;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    Context mContext;
    List<Stage> list;

    public ProgressAdapter(Context mContext, List<Stage> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.single_item,null);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        Stage stage = list.get(position);
	    TextDrawable greenDrawable = TextDrawable.builder().beginConfig().textColor(Color.WHITE).endConfig().buildRound(String.valueOf(position), Color.BLUE);
	    TextDrawable redDrawable = TextDrawable.builder().beginConfig().textColor(Color.WHITE).endConfig().buildRound(String.valueOf(position),Color.GRAY);
        if(position==(list.size()-1))
        {
        	holder.sep.setVisibility(View.INVISIBLE);
        }
        if(stage.isStatus()) {

            holder.stage_element.setImageDrawable(greenDrawable);
            holder.textView.setText("Item Reached!!");
        }
        else {

	        holder.stage_element.setImageDrawable(redDrawable);
            holder.textView.setText("Waiting for Item!!");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        View sep;
        ImageView stage_element;
        TextView textView;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            stage_element = itemView.findViewById(R.id.stage_element);
            textView = itemView.findViewById(R.id.statusText);
            sep = itemView.findViewById(R.id.separator);
        }
    }

}
