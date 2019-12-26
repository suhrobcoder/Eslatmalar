package uz.suhrob.eslatmalar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uz.suhrob.eslatmalar.AddEventActivity;
import uz.suhrob.eslatmalar.EventAlarm;
import uz.suhrob.eslatmalar.R;
import uz.suhrob.eslatmalar.database.EventDBHelper;
import uz.suhrob.eslatmalar.models.Event;
import uz.suhrob.eslatmalar.models.Notify;

/**
 * Created by User on 17.12.2019.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements Filterable {

    private List<Event> eventList;
    private Context context;

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
    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        holder.itemName.setText(eventList.get(position).getName());
        holder.itemContent.setText(eventList.get(position).getContent());
        if (!eventList.get(position).isActive()) {
            holder.alarmSwitchBtn.setImageResource(R.drawable.ic_alarm_off);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String string = charSequence.toString().trim();
                List<Event> filteredList = new ArrayList<>();
                if (string.isEmpty()) {
                    filteredList = eventList;
                } else {
                    for (Event event: eventList) {
                        if (event.getName().toLowerCase().contains(string.toLowerCase()) || event.getContent().toLowerCase().contains(string.toLowerCase())) {
                            filteredList.add(event);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventList = (ArrayList<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemContent;
        ImageView alarmSwitchBtn;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemContent = itemView.findViewById(R.id.item_content);
            alarmSwitchBtn = itemView.findViewById(R.id.alarm_switch_btn);
            alarmSwitchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPos = getAdapterPosition();
                    if (eventList.get(itemPos).isActive()) {
                        eventList.get(itemPos).setActive(false);
                        alarmSwitchBtn.setImageResource(R.drawable.ic_alarm_off);
                        EventDBHelper dbHelper = new EventDBHelper(context);
                        dbHelper.changeActive(eventList.get(itemPos).getId(), false);
                        int notifyId = dbHelper.getNotifyIdWithEventId(eventList.get(itemPos).getId());
                        dbHelper.deleteNotify(notifyId);
                        new EventAlarm().cancelAlarm(context, notifyId);
                        Log.d("AdapterChanges", eventList.get(itemPos).getName() + " is deactivated");
                    } else {
                        eventList.get(itemPos).setActive(true);
                        alarmSwitchBtn.setImageResource(R.drawable.ic_alarm_on);
                        EventDBHelper dbHelper = new EventDBHelper(context);
                        dbHelper.changeActive(eventList.get(itemPos).getId(), true);
                        int notifyId = (int)dbHelper.insertNotify(new Notify(eventList.get(itemPos).getId()));
                        new EventAlarm().setAlarm(context, AddEventActivity.whenNextAlarm(eventList.get(itemPos)), eventList.get(itemPos).getName(), eventList.get(itemPos).getContent(), notifyId);
                        Log.d("AdapterChanges", eventList.get(itemPos).getName() + " is activated");
                    }
                }
            });
        }
    }
}
