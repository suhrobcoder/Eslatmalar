package uz.suhrob.eslatmalar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import uz.suhrob.eslatmalar.R;
import uz.suhrob.eslatmalar.database.EventDBHelper;
import uz.suhrob.eslatmalar.models.Event;

/**
 * Created by User on 17.12.2019.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    Context context;

    public EventAdapter(Context context, List<Event> eventList) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, final int position) {
        holder.itemName.setText(eventList.get(position).getName());
        holder.itemContent.setText(eventList.get(position).getContent());
        holder.activeCheckBox.setChecked(eventList.get(position).isActive());

        holder.activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new EventDBHelper(context).changeActive(eventList.get(position).getId(), b);
                eventList.get(position).setActive(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemContent;
        CheckBox activeCheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemContent = itemView.findViewById(R.id.item_content);
            activeCheckBox = itemView.findViewById(R.id.active_checkbox);
        }
    }
}
